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

        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        if (cePlayer.getTitleOpenInventory() != null) {
            List<String> outfitTitleUpdateBlacklist = CustomEnchantment.instance().getMainConfig().getOutfitTitleUpdateBlacklist();
            if (outfitTitleUpdateBlacklist.contains(cePlayer.getTitleOpenInventory())) {
                return;
            }
        }

        String playerName = player.getName();
        PlayerItemTracker tracker = new PlayerItemTracker();
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
            if (ceItem instanceof CESkin skin) {
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

    private void processSkinWithoutOutfit(PlayerItemTracker tracker, int slot, CESkin skin, ItemStack itemStack) {
        ItemStack weaponItemStack = skin.getUnifyWeapon().getItemStack(CEUnifyWeapon.Target.WEAPON);
        CEItem weaponItem = CEAPI.getCEItem(weaponItemStack);
        tracker.setItemHistory(slot, itemStack, weaponItem);
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