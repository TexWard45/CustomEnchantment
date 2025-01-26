package com.bafmc.customenchantment.menu.artifactupgrade;

import com.bafmc.bukkit.api.PlaceholderAPI;
import com.bafmc.bukkit.bafframework.api.BafFrameworkAPI;
import com.bafmc.bukkit.bafframework.feature.requirement.RequirementManager;
import com.bafmc.bukkit.feature.execute.Execute;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.*;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.artifact.CEArtifactStorage;
import com.bafmc.customenchantment.menu.MenuAbstract;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeAddReason;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeConfirmReason;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeData;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.custommenu.menu.CMenuView;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArtifactUpgradeMenu extends MenuAbstract {
    public static final String MENU_NAME = "artifact-upgrade";
    public static final String SELECTED_ARTIFACT_ITEM = "selected-artifact";
    public static final String PREVIEW_ARTIFACT_ITEM = "preview-artifact";
    public static final String REMIND_ITEM = "remind";
    public static final String REMIND_INGREDIENT_ITEM = "remind-ingredient";
    public static final String REMIND_ARTIFACT_CONFIRM_ITEM = "remind-artifact-confirm";
    public static final String INGREDIENT_PREVIEW_ITEM = "ingredient-preview";
    @Getter
    @Setter
    private static ArtifactUpgradeSettings settings;

    public ArtifactUpgradeMenu(CMenuView menuView, Player player) {
        super(menuView, player);
    }

    private CEArtifact selectedArtifact;
    private CEArtifact previewArtifact;
    private List<CEItem> ingredientItems = new ArrayList<>();
    private List<Double> ingredientPoints = new ArrayList<>();
    private double totalPoint;

    public ArtifactUpgradeAddReason addItem(ItemStack clickedItem, CEItem ceItem, InventoryClickEvent e) {
        if (selectedArtifact == null) {
            if (ceItem instanceof CEArtifact ceArtifact) {
                int currentLevel = ceArtifact.getData().getLevel();
                int maxLevel = ceArtifact.getData().getConfigData().getMaxLevel();

                if (currentLevel >= maxLevel) {
                    return ArtifactUpgradeAddReason.MAX_LEVEL;
                }

                String name = ceArtifact.getData().getPattern();
                CEArtifactStorage storage = (CEArtifactStorage) CEAPI.getCEItemStorage(CEItemType.ARTIFACT);
                ItemStack previewItem = storage.getItemStackByParameter(name, currentLevel + 1, 1);
                this.previewArtifact = (CEArtifact) CEAPI.getCEItem(previewItem);
                this.selectedArtifact = ceArtifact;

                updateMenu();
                e.setCurrentItem(null);
                return ArtifactUpgradeAddReason.ADD_SELECTED_ARTIFACT;
            }else {
                return ArtifactUpgradeAddReason.NOT_ARTIFACT;
            }
        }else {
            double requiredPoint = settings.getRequiredPoint(ceItem);
            if (requiredPoint > 0) {
                if (ingredientItems.size() >= settings.getMaxIngredientCount()) {
                    return ArtifactUpgradeAddReason.MAX_INGREDIENT;
                }

                addIngredient(ceItem, requiredPoint);

                updateMenu();
                e.setCurrentItem(null);
                return ArtifactUpgradeAddReason.ADD_INGREDIENT;
            }else {
                return ArtifactUpgradeAddReason.NOT_INGREDIENT;
            }
        }
    }

    public ArtifactUpgradeConfirmReason confirmUpgrade() {
        if (selectedArtifact == null) {
            return ArtifactUpgradeConfirmReason.NO_SELECTED_ARTIFACT;
        }
        if (ingredientItems.isEmpty()) {
            return ArtifactUpgradeConfirmReason.NO_INGREDIENT;
        }
        ArtifactUpgradeData artifactUpgradeData = getArtifactUpgradeData();
        if (RequirementManager.checkFailedRequirement(player, artifactUpgradeData.getRequirementList())) {
            return ArtifactUpgradeConfirmReason.NOTHING;
        }

        CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        Chance chance = new Chance(getChance());
        if (!cePlayer.isFullChance() && !chance.work()) {
            com.bafmc.bukkit.feature.requirement.RequirementManager.instance().payRequirements(player, artifactUpgradeData.getRequirementList());
            clearIngredient();
            returnItems();
            updateMenu();
            return ArtifactUpgradeConfirmReason.FAIL_CHANCE;
        }

        com.bafmc.bukkit.feature.requirement.RequirementManager.instance().payRequirements(player, artifactUpgradeData.getRequirementList());
        ItemStack itemStack = previewArtifact.exportTo();
        broadcastUpgradeSuccess(player, itemStack);
        clearSelectedArtifact();
        clearIngredient();
        InventoryUtils.addItem(player, itemStack);
        updateMenu();
        return ArtifactUpgradeConfirmReason.SUCCESS;
    }

    public void updateMenu() {
        updateArtifactSlot();
        updatePreviewSlot();
        updateIngredientSlots();
        updateArtifactConfirm();
    }

    public void updateIngredientSlots() {
        List<Integer> slots = getSlots(INGREDIENT_PREVIEW_ITEM);
        for (int i = 0; i < slots.size(); i++) {
            int slot = slots.get(i);
            if (i < ingredientItems.size()) {
                CEItem ceItem = ingredientItems.get(i);
                menuView.setTemporaryItem(slot, ceItem.getDefaultItemStack());
            }else {
                menuView.removeTemporaryItem(slot);
            }
        }
    }

    public void updateArtifactSlot() {
        updateSlots(SELECTED_ARTIFACT_ITEM, selectedArtifact == null ? null : selectedArtifact.getDefaultItemStack());
    }

    public void updatePreviewSlot() {
        if (this.selectedArtifact == null) {
            updateSlots(PREVIEW_ARTIFACT_ITEM, null);
            return;
        }

        updateSlots(PREVIEW_ARTIFACT_ITEM, this.previewArtifact == null ? null : this.previewArtifact.exportTo());
    }

    public void updateArtifactConfirm() {
        ArtifactUpgradeData artifactUpgradeData = getArtifactUpgradeData();
        if (artifactUpgradeData == null) {
            this.updateSlots(REMIND_ITEM, null);
        }else {
            if (!canUpgrade()) {
                this.updateSlots(REMIND_ITEM, getItemStack(player, REMIND_INGREDIENT_ITEM));
            }else {
                List<String> requirementLore = BafFrameworkAPI.getRequirementLore(player, artifactUpgradeData.getRequirementList());
                requirementLore = PlaceholderAPI.setPlaceholders(player, requirementLore);

                ItemStack itemStack = getItemStack(player, REMIND_ARTIFACT_CONFIRM_ITEM);

                PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
                placeholderBuilder.put("{requirement_description}", requirementLore);
                placeholderBuilder.put("{chance}", StringUtils.formatNumber(getChance()));

                ItemStackUtils.setItemStack(itemStack, placeholderBuilder.build());
                this.updateSlots(REMIND_ITEM, itemStack);
            }
        }
    }

    public void broadcastUpgradeSuccess(Player player, ItemStack itemStack) {
        Execute execute = settings.getBroadcastUpgradeSuccessExecute();

        PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
        placeholderBuilder.put("{player_name}", player.getName());
        placeholderBuilder.put("{artifact_display}", itemStack.getItemMeta().getDisplayName());

        execute.execute(player, placeholderBuilder.build());
    }

    public void returnItems() {
        if (selectedArtifact != null) {
            InventoryUtils.addItem(player, selectedArtifact.getDefaultItemStack());
            selectedArtifact = null;
        }
        if (ingredientItems.size() > 0) {
            for (CEItem ceItem : ingredientItems) {
                InventoryUtils.addItem(player, ceItem.getDefaultItemStack());
            }
            ingredientItems.clear();
        }
    }

    public void returnItem(String name, int slot) {
        if (name.equals(SELECTED_ARTIFACT_ITEM)) {
            if (selectedArtifact != null) {
                InventoryUtils.addItem(player, selectedArtifact.getDefaultItemStack());
                selectedArtifact = null;
                previewArtifact = null;
            }

            updateMenu();
        }else if (name.equals(INGREDIENT_PREVIEW_ITEM)) {
            int index = -1;

            List<Integer> slots = getSlots(INGREDIENT_PREVIEW_ITEM);
            for (int i = 0; i < slots.size(); i++) {
                if (slots.get(i) == slot) {
                    index = i;
                    break;
                }
            }

            if (index == -1 || index >= ingredientItems.size()) {
                return;
            }

            CEItem ceItem = ingredientItems.get(index);
            removeIngredient(index);

            InventoryUtils.addItem(player, ceItem.getDefaultItemStack());
            updateMenu();
        }else if (name.startsWith(REMIND_ITEM)) {
            ArtifactUpgradeConfirmReason reason = confirmUpgrade();

            CustomEnchantmentMessage.send(player, "menu.artifactupgrade.confirm." + EnumUtils.toConfigStyle(reason));
            updateMenu();
        }
    }

    public ArtifactUpgradeData getArtifactUpgradeData() {
        return settings.getArtifactUpgradeData(previewArtifact);
    }

    public double getChance() {
        ArtifactUpgradeData artifactUpgradeData = getArtifactUpgradeData();
        if (artifactUpgradeData == null) {
            return 0;
        }

        double requiredPoint = artifactUpgradeData.getRequiredPoint();
        return Math.max(Math.min(1, totalPoint / requiredPoint), 0) * 100;
    }

    public void addIngredient(CEItem ceItem, double point) {
        ingredientItems.add(ceItem);
        ingredientPoints.add(point);
        totalPoint += point;
    }

    public void removeIngredient(int index) {
        totalPoint -= ingredientPoints.get(index);
        ingredientItems.remove(index);
        ingredientPoints.remove(index);
    }

    public void clearIngredient() {
        ingredientItems.clear();
        ingredientPoints.clear();
        totalPoint = 0;
    }

    public void clearSelectedArtifact() {
        selectedArtifact = null;
        previewArtifact = null;
    }

    public boolean canUpgrade() {
        return totalPoint > 0 && selectedArtifact != null;
    }
}