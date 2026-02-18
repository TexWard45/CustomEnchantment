package com.bafmc.customenchantment.menu.artifactupgrade;

import com.bafmc.bukkit.api.PlaceholderAPI;
import com.bafmc.bukkit.bafframework.api.BafFrameworkAPI;
import com.bafmc.bukkit.bafframework.custommenu.menu.AbstractMenu;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.ClickData;
import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.bukkit.bafframework.custommenu.menu.item.list.DefaultItem;
import com.bafmc.bukkit.bafframework.feature.requirement.RequirementManager;
import com.bafmc.bukkit.feature.execute.Execute;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.feature.requirement.RequirementList;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.InventoryUtils;
import com.bafmc.bukkit.utils.ItemStackUtils;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.artifact.CEArtifactStorage;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeAddReason;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeConfirmReason;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeData;
import com.bafmc.customenchantment.menu.artifactupgrade.item.ArtifactIngredientPreviewItem;
import com.bafmc.customenchantment.menu.artifactupgrade.item.ArtifactRemindItem;
import com.bafmc.customenchantment.menu.artifactupgrade.item.PreviewArtifactItem;
import com.bafmc.customenchantment.menu.artifactupgrade.item.SelectedArtifactItem;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ArtifactUpgradeCustomMenu extends AbstractMenu<MenuData, ArtifactUpgradeExtraData> {

    public static final String MENU_NAME = "artifact-upgrade";

    @Getter @Setter
    private static ArtifactUpgradeSettings settings;

    @Override
    public String getType() {
        return MENU_NAME;
    }

    @Override
    public void registerItems() {
        registerItem(DefaultItem.class);
        registerItem(SelectedArtifactItem.class);
        registerItem(PreviewArtifactItem.class);
        registerItem(ArtifactIngredientPreviewItem.class);
        registerItem(ArtifactRemindItem.class);
    }

    @Override
    public void handlePlayerInventoryClick(ClickData data) {
        ItemStack clickedItem = data.getEvent().getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir() || clickedItem.getAmount() > 1) {
            return;
        }

        CEItem ceItem = CEAPI.getCEItem(clickedItem);
        if (ceItem == null) {
            return;
        }

        ArtifactUpgradeAddReason reason = addItem(clickedItem, ceItem);

        if (reason == ArtifactUpgradeAddReason.ADD_SELECTED_ARTIFACT
                || reason == ArtifactUpgradeAddReason.ADD_INGREDIENT) {
            data.getEvent().setCurrentItem(null);
        }

        CustomEnchantmentMessage.send(data.getPlayer(),
                "menu.artifactupgrade.add-item." + EnumUtils.toConfigStyle(reason));
    }

    @Override
    public void handleClose() {
        returnAllItems();
    }

    // ==================== Business Logic ====================

    public ArtifactUpgradeAddReason addItem(ItemStack clickedItem, CEItem ceItem) {
        ArtifactUpgradeSettings settings = extraData.getSettings();

        if (extraData.getSelectedArtifact() == null) {
            if (ceItem instanceof CEArtifact ceArtifact) {
                int currentLevel = ceArtifact.getData().getLevel();
                int maxLevel = ceArtifact.getData().getConfigData().getMaxLevel();

                if (currentLevel >= maxLevel) {
                    return ArtifactUpgradeAddReason.MAX_LEVEL;
                }

                String name = ceArtifact.getData().getPattern();
                CEArtifactStorage storage = (CEArtifactStorage) CEAPI.getCEItemStorage(CEItemType.ARTIFACT);
                ItemStack previewItem = storage.getItemStackByParameter(name, currentLevel + 1, 1);
                extraData.setPreviewArtifact((CEArtifact) CEAPI.getCEItem(previewItem));
                extraData.setSelectedArtifact(ceArtifact);

                updateMenu();
                return ArtifactUpgradeAddReason.ADD_SELECTED_ARTIFACT;
            } else {
                return ArtifactUpgradeAddReason.NOT_ARTIFACT;
            }
        } else {
            double requiredPoint = settings.getRequiredPoint(ceItem);
            if (requiredPoint > 0) {
                if (extraData.getIngredientItems().size() >= settings.getMaxIngredientCount()) {
                    return ArtifactUpgradeAddReason.MAX_INGREDIENT;
                }

                extraData.addIngredient(ceItem, requiredPoint);

                updateMenu();
                return ArtifactUpgradeAddReason.ADD_INGREDIENT;
            } else {
                return ArtifactUpgradeAddReason.NOT_INGREDIENT;
            }
        }
    }

    public ArtifactUpgradeConfirmReason confirmUpgrade() {
        if (extraData.getSelectedArtifact() == null) {
            return ArtifactUpgradeConfirmReason.NO_SELECTED_ARTIFACT;
        }
        if (extraData.getIngredientItems().isEmpty()) {
            return ArtifactUpgradeConfirmReason.NO_INGREDIENT;
        }

        ArtifactUpgradeData artifactUpgradeData = getArtifactUpgradeData();
        if (RequirementManager.checkFailedRequirement(owner, artifactUpgradeData.getRequirementList())) {
            return ArtifactUpgradeConfirmReason.NOTHING;
        }

        CEPlayer cePlayer = CEAPI.getCEPlayer(owner);

        Chance chance = new Chance(getChance());
        if (!cePlayer.isFullChance() && !chance.work()) {
            com.bafmc.bukkit.feature.requirement.RequirementManager.instance()
                    .payRequirements(owner, artifactUpgradeData.getRequirementList());
            extraData.clearIngredient();
            returnSelectedArtifact();
            updateMenu();
            return ArtifactUpgradeConfirmReason.FAIL_CHANCE;
        }

        com.bafmc.bukkit.feature.requirement.RequirementManager.instance()
                .payRequirements(owner, artifactUpgradeData.getRequirementList());
        ItemStack resultItem = extraData.getPreviewArtifact().exportTo();
        broadcastUpgradeSuccess(resultItem);
        extraData.clearSelectedArtifact();
        extraData.clearIngredient();
        InventoryUtils.addItem(owner, resultItem);
        updateMenu();
        return ArtifactUpgradeConfirmReason.SUCCESS;
    }

    // ==================== Item Management ====================

    public void returnSelectedArtifact() {
        if (extraData.getSelectedArtifact() != null) {
            InventoryUtils.addItem(owner, extraData.getSelectedArtifact().getDefaultItemStack());
            extraData.clearSelectedArtifact();

            // Also return ingredients when artifact is returned
            returnIngredients();
        }
        updateMenu();
    }

    public void returnIngredientAt(int index) {
        if (index < 0 || index >= extraData.getIngredientItems().size()) {
            return;
        }

        CEItem ceItem = extraData.getIngredientItems().get(index);
        extraData.removeIngredient(index);

        InventoryUtils.addItem(owner, ceItem.getDefaultItemStack());
        updateMenu();
    }

    private void returnIngredients() {
        for (CEItem ceItem : extraData.getIngredientItems()) {
            InventoryUtils.addItem(owner, ceItem.getDefaultItemStack());
        }
        extraData.clearIngredient();
    }

    private void returnAllItems() {
        if (extraData.getSelectedArtifact() != null) {
            InventoryUtils.addItem(owner, extraData.getSelectedArtifact().getDefaultItemStack());
        }
        for (CEItem ceItem : extraData.getIngredientItems()) {
            InventoryUtils.addItem(owner, ceItem.getDefaultItemStack());
        }
    }

    // ==================== Menu Rendering ====================

    public void updateMenu() {
        updateArtifactSlot();
        updatePreviewSlot();
        updateIngredientSlots();
        updateArtifactConfirm();
    }

    private void updateArtifactSlot() {
        updateSlots("selected-artifact",
                extraData.getSelectedArtifact() == null ? null : extraData.getSelectedArtifact().getDefaultItemStack());
    }

    private void updatePreviewSlot() {
        if (extraData.getSelectedArtifact() == null) {
            updateSlots("preview-artifact", null);
            return;
        }
        updateSlots("preview-artifact",
                extraData.getPreviewArtifact() == null ? null : extraData.getPreviewArtifact().exportTo());
    }

    private void updateIngredientSlots() {
        List<Integer> slots = getSlotsByName("ingredient-preview");
        for (int i = 0; i < slots.size(); i++) {
            int slot = slots.get(i);
            if (i < extraData.getIngredientItems().size()) {
                CEItem ceItem = extraData.getIngredientItems().get(i);
                inventory.setItem(slot, ceItem.getDefaultItemStack());
            } else {
                inventory.setItem(slot, getTemplateItemStack("ingredient-preview"));
            }
        }
    }

    private void updateArtifactConfirm() {
        ArtifactUpgradeData artifactUpgradeData = getArtifactUpgradeData();
        if (artifactUpgradeData == null) {
            updateSlots("remind", null);
        } else {
            if (!extraData.canUpgrade()) {
                updateSlots("remind", getTemplateItemStack("remind-ingredient"));
            } else {
                List<String> requirementLore = BafFrameworkAPI.getRequirementLore(owner, artifactUpgradeData.getRequirementList());
                requirementLore = PlaceholderAPI.setPlaceholders(owner, requirementLore);

                ItemStack confirmItem = getTemplateItemStack("remind-artifact-confirm");
                if (confirmItem == null) {
                    return;
                }

                PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
                placeholderBuilder.put("{requirement_description}", requirementLore);
                placeholderBuilder.put("{chance}", StringUtils.formatNumber(getChance()));

                ItemStackUtils.setItemStack(confirmItem, placeholderBuilder.build());
                updateSlots("remind", confirmItem);
            }
        }
    }

    // ==================== Helpers ====================

    public double getChance() {
        ArtifactUpgradeData artifactUpgradeData = getArtifactUpgradeData();
        if (artifactUpgradeData == null) {
            return 0;
        }

        double requiredPoint = artifactUpgradeData.getRequiredPoint();
        if (requiredPoint == 0) {
            return 0;
        }
        return Math.max(Math.min(1, extraData.getTotalPoint() / requiredPoint), 0) * 100;
    }

    public ArtifactUpgradeData getArtifactUpgradeData() {
        return extraData.getSettings().getArtifactUpgradeData(extraData.getPreviewArtifact());
    }

    private void broadcastUpgradeSuccess(ItemStack itemStack) {
        Execute execute = extraData.getSettings().getBroadcastUpgradeSuccessExecute();
        if (execute == null) {
            return;
        }

        PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
        placeholderBuilder.put("{player_name}", owner.getName());
        placeholderBuilder.put("{artifact_display}", itemStack.getItemMeta().getDisplayName());

        execute.execute(owner, placeholderBuilder.build());
    }

}
