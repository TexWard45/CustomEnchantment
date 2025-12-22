package com.bafmc.customenchantment.task;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import com.bafmc.customenchantment.item.skin.CESkin;
import com.bafmc.customenchantment.player.CEPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OutfitTopInventoryAsyncTask extends BukkitRunnable {
    private final Map<String, PlayerItemTracker> playerItemTrackers = new ConcurrentHashMap<>();

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            processPlayer(player);
        }
    }

    public PlayerItemTracker getPlayerItemTracker(String playerName) {
        return playerItemTrackers.get(playerName);
    }

    private void processPlayer(Player player) {
        if (CustomEnchantment.instance().isInReload()) {
            return;
        }

        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getType() == InventoryType.CRAFTING) {
            return;
        }

        String playerName = player.getName();
        PlayerItemTracker tracker = new PlayerItemTracker();
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        CEOutfit ceOutfit = cePlayer.getEquipment().getCEOutfit();

        processInventoryItems(inventory, cePlayer, ceOutfit, tracker);

        updatePlayerTracker(playerName, tracker);
    }

    private void processInventoryItems(Inventory inventory, CEPlayer cePlayer, CEOutfit ceOutfit, PlayerItemTracker tracker) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack == null) continue;

            itemStack = itemStack.clone();
            CEItem ceItem = CEAPI.getCEItem(itemStack);

            if (!(ceItem instanceof CEWeaponAbstract weapon)) continue;

            if (ceOutfit != null) {
                processWithOutfit(cePlayer, tracker, slot, weapon, ceOutfit, itemStack);
            } else if (weapon instanceof CESkin skin) {
                processSkinWithoutOutfit(tracker, slot, skin, itemStack);
            }
        }
    }

    private void updatePlayerTracker(String playerName, PlayerItemTracker tracker) {
        if (tracker.isUpdated()) {
            playerItemTrackers.put(playerName, tracker);
        } else {
            playerItemTrackers.remove(playerName);
        }
    }

    private void processWithOutfit(CEPlayer cePlayer, PlayerItemTracker tracker, int slot,
                                   CEWeaponAbstract weapon, CEOutfit outfit, ItemStack itemStack) {
        if (weapon instanceof CEOutfit || !hasValidCustomType(weapon)) {
            return;
        }

        String skinName = getSkinNameFromOutfit(cePlayer, weapon, outfit);
        if (skinName == null) {
            return;
        }

        weapon = extractWeaponFromSkin(weapon, skinName);
        if (weapon == null) {
            return;
        }

        applySkinToWeapon(tracker, slot, weapon, skinName, itemStack);
    }

    private boolean hasValidCustomType(CEWeaponAbstract weapon) {
        String customType = weapon.getCustomType();
        return customType != null && !customType.isEmpty();
    }

    private String getSkinNameFromOutfit(CEPlayer cePlayer, CEWeaponAbstract weapon, CEOutfit outfit) {
        String customType = weapon.getCustomType();
        int skinIndex = cePlayer.getEquipment().getSkinIndex(outfit.getData().getPattern(), customType);
        String skinName = outfit.getData().getConfigByLevelData().getSkinByIndex(customType, skinIndex);

        return (skinName != null && !skinName.isEmpty()) ? skinName : null;
    }

    private CEWeaponAbstract extractWeaponFromSkin(CEWeaponAbstract weapon, String skinName) {
        if (weapon instanceof CESkin skin) {
            if (skin.getData().getPattern().equals(skinName)) {
                return null; // Same skin, no change needed
            }
            ItemStack weaponStack = skin.getUnifyWeapon().getItemStack(CEUnifyWeapon.Target.WEAPON);
            return (CEWeaponAbstract) CEAPI.getCEItem(weaponStack);
        }
        return weapon;
    }

    private void applySkinToWeapon(PlayerItemTracker tracker, int slot, CEWeaponAbstract weapon,
                                   String skinName, ItemStack originalItemStack) {
        ItemStack skinItemStack = getSkinItemStack(skinName);
        if (skinItemStack == null) {
            return;
        }

        CESkin ceSkin = (CESkin) CEAPI.getCEItem(skinItemStack);
        ApplyReason applyReason = ceSkin.applyTo(weapon);
        CEItem resultItem = applyReason.getSource();

        if (resultItem != null) {
            tracker.setItemHistory(slot, originalItemStack, resultItem);
        }
    }

    private void processSkinWithoutOutfit(PlayerItemTracker tracker, int slot, CESkin skin, ItemStack itemStack) {
        ItemStack weaponItemStack = skin.getUnifyWeapon().getItemStack(CEUnifyWeapon.Target.WEAPON);
        CEItem weaponItem = CEAPI.getCEItem(weaponItemStack);
        tracker.setItemHistory(slot, itemStack, weaponItem);
    }

    private ItemStack getSkinItemStack(String skinName) {
        Parameter parameter = new Parameter(List.of(skinName));
        List<ItemStack> itemStacks = CustomEnchantment.instance()
                .getCeItemStorageMap()
                .get(CEItemType.SKIN)
                .getItemStacksByParameter(parameter);

        return (itemStacks != null && !itemStacks.isEmpty()) ? itemStacks.get(0) : null;
    }

    public static class PlayerItemTracker {
        private final Map<Integer, PlayerItemHistory> itemHistories = new ConcurrentHashMap<>();

        public PlayerItemHistory getItemHistory(int slot) {
            return itemHistories.get(slot);
        }

        public void setItemHistory(int slot, ItemStack oldItemStack, CEItem newCEItem) {
            PlayerItemHistory history = new PlayerItemHistory();
            history.oldItemStack = oldItemStack;
            history.sourceItem = newCEItem;
            history.newItemStack = newCEItem != null ? newCEItem.exportTo() : null;
            itemHistories.put(slot, history);
        }

        public boolean isUpdated() {
            return !itemHistories.isEmpty();
        }
    }

    @Getter
    public static class PlayerItemHistory {
        private ItemStack oldItemStack;
        private ItemStack newItemStack;
        private CEItem sourceItem;
    }
}