package com.bafmc.customenchantment.task;

import com.bafmc.bukkit.task.PlayerPerTickTask;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class UnbreakableArmorTask extends PlayerPerTickTask {
    @Override
    public int getPlayerPerTick() {
        return CustomEnchantment.instance().getMainConfig().getUnbreakableArmorPlayerPerTick();
    }

    @Override
    public void run(Player player) {
        if (!CustomEnchantment.instance().getMainConfig().isUnbreakableArmorEnable()) {
            return;
        }

        for (EquipSlot equipSlot : EquipSlot.ARMOR_ARRAY) {
            ItemStack itemStack = equipSlot.getItemStack(player);

            String type = CEAPI.getCEItemType(itemStack);
            if (type == null || !type.equals(CEItemType.WEAPON)) {
                continue;
            }

            if (!itemStack.isUnbreakable()) {
                itemStack.setUnbreakable(true);
                itemStack.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                if (equipSlot == EquipSlot.HELMET) {
                    player.getInventory().setHelmet(itemStack);
                } else if (equipSlot == EquipSlot.CHESTPLATE) {
                    player.getInventory().setChestplate(itemStack);
                } else if (equipSlot == EquipSlot.LEGGINGS) {
                    player.getInventory().setLeggings(itemStack);
                } else if (equipSlot == EquipSlot.BOOTS) {
                    player.getInventory().setBoots(itemStack);
                }
            }
        }
    }
}
