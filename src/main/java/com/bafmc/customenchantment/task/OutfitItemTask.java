package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.task.PlayerPerTickTask;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.menu.equipment.EquipmentMenu;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerEquipment;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;
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
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        PlayerEquipment equipment = cePlayer.getEquipment();
        PlayerInventory inventory = player.getInventory();
        boolean updated = false;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);
            OutfitItemAsyncTask.PlayerItemHistory itemHistory = playerItemTracker.getItemHistory(i);
            if (itemHistory == null) {
                continue;
            }

            ItemStack oldItem = itemHistory.getOldItemStack();
            if (oldItem == null || oldItem.getType() == Material.AIR) {
                ItemStack newItem = itemHistory.getNewItemStack();
                inventory.setItem(i, newItem);
                equipment.setCEItemCache(i, itemHistory.getSourceItem());

                updated = true;
                continue;
            }

            if (currentItem == null || currentItem.getType() == Material.AIR) {
                continue;
            }

            if (!currentItem.equals(oldItem)) {
                continue;
            }

            ItemStack newItem = itemHistory.getNewItemStack();
            inventory.setItem(i, newItem);
            equipment.setCEItemCache(i, itemHistory.getSourceItem());

            updated = true;
        }

        CEWeaponAbstract oldWings = cePlayer.getEquipment().getWings();
        CEWeaponAbstract newWings = playerItemTracker.getWingsWeapon();

        // Check if wings actually changed
        boolean wingsChanged = hasWingsChanged(oldWings, newWings) && !playerItemTracker.isNoNeedUpdateWings();
        if (wingsChanged) {
            if (CEAPI.isInCombat(player)) {
                handleCombatWingsChange(player, cePlayer, equipment);
            } else {
                handleNonCombatWingsChange(player, cePlayer, equipment, oldWings, newWings);
            }
            updated = true;
        }

        if (updated) {
            updateEquipmentMenu(player);
        }
    }

    private void updateEquipmentMenu(Player player) {
        EquipmentMenu menu = EquipmentMenu.getMenu(player);
        if (menu != null) {
            menu.updateMenuWithPreventAction();
        }
    }

    private boolean hasWingsChanged(CEWeaponAbstract oldWings, CEWeaponAbstract newWings) {
        return oldWings != newWings && (oldWings == null || newWings == null || !Objects.equals(oldWings.getData().getPattern(), newWings.getData().getPattern()));
    }

    private void handleCombatWingsChange(Player player, CEPlayer cePlayer, PlayerEquipment equipment) {
        ItemStack offhandItem = equipment.getOffhandItemStack();

        cePlayer.getEquipment().setWings(null);

        player.getInventory().setItem(EquipmentSlot.OFF_HAND, offhandItem);
    }

    private void handleNonCombatWingsChange(Player player, CEPlayer cePlayer, PlayerEquipment equipment,
                                            CEWeaponAbstract oldWings, CEWeaponAbstract newWings) {
        cePlayer.getEquipment().setWings(newWings);

        ItemStack oldOffhandItemStack = equipment.getOffhandItemStack();
        ItemStack currentOffhandItemStack = EquipSlot.OFFHAND.getItemStack(player);

        if (shouldUpdateOffhandItemStack(oldWings, newWings, currentOffhandItemStack)) {
            cePlayer.getEquipment().setOffhandItemStack(currentOffhandItemStack);
        }

        ItemStack newOffHandItem = newWings != null ? newWings.getDefaultItemStack() : oldOffhandItemStack;
        player.getInventory().setItem(EquipmentSlot.OFF_HAND, newOffHandItem);
    }

    private boolean shouldUpdateOffhandItemStack(CEWeaponAbstract oldWings, CEWeaponAbstract newWings, ItemStack currentOffhandItemStack) {
        if (newWings != null && newWings.getDefaultItemStack().equals(currentOffhandItemStack)) {
            return false;
        }

        return oldWings == null || !oldWings.getDefaultItemStack().equals(currentOffhandItemStack);
    }
}
