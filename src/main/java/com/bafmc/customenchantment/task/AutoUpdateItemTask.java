package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.task.PlayerPerTickTask;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.gem.CEGem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AutoUpdateItemTask extends PlayerPerTickTask {
    @Override
    public int getPlayerPerTick() {
        return 1;
    }

    @Override
    public void run(Player player) {
        if (CustomEnchantment.instance().isInReload()) {
            return;
        }

        Inventory inventory = player.getInventory();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            if (!itemStack.hasItemMeta()) {
                continue;
            }

            CEGem ceItem = new CEGem(itemStack);
            if (!ceItem.isMatchType(ceItem.getType())) {
                continue;
            }

            ItemStack exportItem = ceItem.exportTo();

            ItemMeta itemMeta = itemStack.getItemMeta();
            ItemMeta exportItemMeta = exportItem.getItemMeta();

            boolean needUpdate = false;
            if (itemMeta.hasDisplayName() && exportItemMeta.hasDisplayName()) {
                if (!itemMeta.getDisplayName().equals(exportItemMeta.getDisplayName())) {
                    needUpdate = true;
                }
            }

            if (itemMeta.hasLore() && exportItemMeta.hasLore()) {
                if (!itemMeta.getLore().equals(exportItemMeta.getLore())) {
                    needUpdate = true;
                }
            }

            if (needUpdate) {
                inventory.setItem(i, ceItem.exportTo());
            }
        }
    }
}
