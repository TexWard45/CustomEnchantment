package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.utils.EquipSlot;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class OutfitItemAsyncTask extends BukkitRunnable {
    private static final Logger logger = Logger.getLogger(OutfitItemAsyncTask.class.getName());
    private final Map<String, PlayerItemTracker> playerItemTrackers = new ConcurrentHashMap<>();
    private PlayerItemTracker currentTracker;

    public PlayerItemTracker getPlayerItemTracker(String playerName) {
        return playerItemTrackers.get(playerName);
    }

    public static class PlayerItemTracker {
        private Map<Integer, PlayerItemHistory> itemHistories = new ConcurrentHashMap<>();
        @Getter
        private CEWeaponAbstract wingsWeapon;
        private boolean forceUpdate;

        public PlayerItemHistory getItemHistory(int slot) {
            return itemHistories.get(slot);
        }

        public void setItemHistory(int slot, ItemStack oldItemStack, ItemStack newItemStack) {
            PlayerItemHistory history = new PlayerItemHistory();
            history.oldItemStack = oldItemStack;
            history.newItemStack = newItemStack;
            itemHistories.put(slot, history);
        }

        public boolean isUpdated() {
            return !itemHistories.isEmpty() || forceUpdate;
        }
    }

    @Getter
    public static class PlayerItemHistory {
        private ItemStack oldItemStack;
        private ItemStack newItemStack;
    }

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            run(player);
        }
    }

    public void run(Player player) {
        if (CustomEnchantment.instance().isInReload()) {
            return;
        }

        String playerName = player.getName();
        PlayerItemTracker tracker = new PlayerItemTracker();

        PlayerInventory inventory = player.getInventory();
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);

        CEOutfit ceOutfit = cePlayer.getEquipment().getCEOutfit();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null) continue;
            itemStack = itemStack.clone();

            CEItem ceItem = CEAPI.getCEItem(itemStack);
            if (!(ceItem instanceof CEWeaponAbstract ceWeaponAbstract)) continue;

            if (ceOutfit != null) {
                processWithOutfit(cePlayer, tracker, i, ceWeaponAbstract, ceOutfit, itemStack);
            } else if (ceWeaponAbstract instanceof CESkin skin) {
                processSkinWithoutOutfit(tracker, i, skin, itemStack);
            }
        }

        if (ceOutfit != null) {
            updateWingsSkin(cePlayer, tracker, ceOutfit);
        }

        if (tracker.isUpdated()) {
            playerItemTrackers.put(playerName, tracker);
        }else {
            playerItemTrackers.remove(playerName);
        }
    }

    private void updateWingsSkin(CEPlayer cePlayer, PlayerItemTracker tracker, CEOutfit outfit) {
        int skinIndex = cePlayer.getEquipment().getSkinIndex(outfit.getData().getPattern(), "WINGS");
        CEWeaponAbstract ceWeaponAbstract = cePlayer.getEquipment().getWings();
        if (skinIndex == -1) {
            if (ceWeaponAbstract != null) {
                tracker.wingsWeapon = null;
                tracker.forceUpdate = true;
            }
            return;
        }

        String skinName = outfit.getData().getConfigByLevelData().getSkinByIndex("WINGS", skinIndex);
        if (skinName == null || skinName.isEmpty()) {
            return;
        }

        if (ceWeaponAbstract instanceof CESkin skin && skin.getData().getPattern().equals(skinName)) {
            return;
        }

        tracker.wingsWeapon = (CESkin) CEAPI.getCEItem(getSkinItemStack(skinName));
        tracker.forceUpdate = true;
    }

    private boolean processWithOutfit(CEPlayer cePlayer, PlayerItemTracker tracker, int slot, CEWeaponAbstract weapon, CEOutfit outfit, ItemStack itemStack) {
        if (weapon instanceof CEOutfit) {
            return false;
        }

        String customType = weapon.getCustomType();
        if (customType == null || customType.isEmpty()) {
            return false;
        }

        int skinIndex = cePlayer.getEquipment().getSkinIndex(outfit.getData().getPattern(), customType);
        String skinName = outfit.getData().getConfigByLevelData().getSkinByIndex(customType, skinIndex);
        if (skinName == null || skinName.isEmpty()) {
            return false;
        }

        if (weapon instanceof CESkin skin) {
            if (skin.getData().getPattern().equals(skinName)) {
                return false;
            }else {
                weapon = (CEWeaponAbstract) CEAPI.getCEItem(skin.getUnifyWeapon().getItemStack(CEUnifyWeapon.Target.WEAPON));
            }
        }

        ItemStack skinItemStack = getSkinItemStack(skinName);
        if (skinItemStack == null) {
            return false;
        }

        CESkin ceSkin = (CESkin) CEAPI.getCEItem(skinItemStack);
        ApplyReason applyReason = ceSkin.applyTo(weapon);
        CEItem source = applyReason.getSource();

        if (source != null) {
            tracker.setItemHistory(slot, itemStack, source.exportTo());
            return true;
        }
        return false;
    }

    private boolean processSkinWithoutOutfit(PlayerItemTracker tracker, int slot, CESkin skin, ItemStack itemStack) {
        ItemStack weaponItemStack = skin.getUnifyWeapon().getItemStack(CEUnifyWeapon.Target.WEAPON);
        tracker.setItemHistory(slot, itemStack, weaponItemStack);
        return true;
    }

    private ItemStack getSkinItemStack(String skinName) {
        Parameter parameter = new Parameter(List.of(skinName));
        List<ItemStack> itemStacks = CustomEnchantment.instance()
                .getCeItemStorageMap()
                .get(CEItemType.SKIN)
                .getItemStacksByParameter(parameter);

        return (itemStacks != null && !itemStacks.isEmpty()) ? itemStacks.get(0) : null;
    }
}