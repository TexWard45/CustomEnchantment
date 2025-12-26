package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.task.PlayerPerTickTask;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.menu.equipment.EquipmentMenu;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerEquipment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.logging.Logger;

public class OutfitTopInventoryTask extends PlayerPerTickTask {
    private static final Logger logger = Logger.getLogger(OutfitTopInventoryTask.class.getName());
    private OutfitTopInventoryAsyncTask outfitTopInventoryAsyncTask;

    public OutfitTopInventoryTask(OutfitTopInventoryAsyncTask outfitTopInventoryAsyncTask) {
        this.outfitTopInventoryAsyncTask = outfitTopInventoryAsyncTask;
    }

    @Override
    public int getPlayerPerTick() {
        return CustomEnchantment.instance().getMainConfig().getOutfitTaskPerPlayer();
    }

    @Override
    public void run(Player player) {
        if (CustomEnchantment.instance().isInReload()) {
            return;
        }

        OutfitTopInventoryAsyncTask.PlayerItemTracker playerItemTracker = outfitTopInventoryAsyncTask.getPlayerItemTracker(player.getName());
        if (playerItemTracker == null) {
            return;
        }

        long startTime = System.nanoTime();
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        PlayerEquipment ceEquipment = cePlayer.getEquipment();
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getType() == InventoryType.CRAFTING) {
            return;
        }

        if (cePlayer.getTitleOpenInventory() != null) {
            List<String> outfitTitleUpdateBlacklist = CustomEnchantment.instance().getMainConfig().getOutfitTitleUpdateBlacklist();
            if (outfitTitleUpdateBlacklist.contains(cePlayer.getTitleOpenInventory())) {
                return;
            }
        }

        boolean updated = false;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);
            OutfitTopInventoryAsyncTask.PlayerItemHistory itemHistory = playerItemTracker.getItemHistory(i);
            if (itemHistory == null) {
                continue;
            }

            if (currentItem == null) {
                continue;
            }

            ItemStack oldItem = itemHistory.getOldItemStack();
            if (!currentItem.equals(oldItem)) {
                continue;
            }

            ItemStack newItem = itemHistory.getNewItemStack();
            inventory.setItem(i, newItem);
            ceEquipment.setCEItemCache(i, itemHistory.getSourceItem());

            updated = true;
        }

        if (updated) {
            long endTime = System.nanoTime();
            double executionTimeMs = (endTime - startTime) / 1_000_000.0;

            logger.info(String.format("OutfitItemTask execution time for player %s: %.3f ms)",
                    player.getName(), executionTimeMs));
        }
    }
}
