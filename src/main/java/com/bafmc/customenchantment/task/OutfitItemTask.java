package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.task.PlayerPerTickTask;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import com.bafmc.customenchantment.item.skin.CESkin;
import com.bafmc.customenchantment.menu.equipment.EquipmentMenu;
import com.bafmc.customenchantment.player.CEPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class OutfitItemTask extends PlayerPerTickTask {
    private static final Logger logger = Logger.getLogger(OutfitItemTask.class.getName());
    private OutfitItemAsyncTask outfitItemAsyncTask;

    public OutfitItemTask(OutfitItemAsyncTask outfitItemAsyncTask) {
        this.outfitItemAsyncTask = outfitItemAsyncTask;
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

        OutfitItemAsyncTask.PlayerItemTracker playerItemTracker = outfitItemAsyncTask.getPlayerItemTracker(player.getName());
        if (playerItemTracker == null) {
            return;
        }

        long startTime = System.nanoTime();

        PlayerInventory inventory = player.getInventory();
        boolean updated = false;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);
            OutfitItemAsyncTask.PlayerItemHistory itemHistory = playerItemTracker.getItemHistory(i);
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

            updated = true;
        }

        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        CEWeaponAbstract oldWings = cePlayer.getEquipment().getWings();
        CEWeaponAbstract newWings = playerItemTracker.getWingsWeapon();

        // Check if wings actually changed
        boolean wingsChanged = (oldWings == null && newWings != null) ||
                (oldWings != null && newWings == null) ||
                (oldWings != null && !oldWings.equals(newWings));

        if (wingsChanged) {
            cePlayer.getEquipment().setWings(newWings);

            // Handle off-hand item based on combat status and newWings availability
            if (CEAPI.isInCombat(player)) {
                player.getInventory().setItem(EquipmentSlot.OFF_HAND, null);
            } else {
                ItemStack offHandItem = (newWings != null) ? newWings.getDefaultItemStack() : null;
                player.getInventory().setItem(EquipmentSlot.OFF_HAND, offHandItem);
            }

            updated = true;
        }

        if (updated) {
            long endTime = System.nanoTime();
            double executionTimeMs = (endTime - startTime) / 1_000_000.0;

            updateEquipmentMenu(player);

            logger.info(String.format("OutfitItemTask execution time for player %s: %.3f ms)",
                    player.getName(), executionTimeMs));
        }
    }

    private void updateEquipmentMenu(Player player) {
        EquipmentMenu menu = EquipmentMenu.getMenu(player);
        if (menu != null) {
            menu.updateMenuWithPreventAction();
        }
    }
}
