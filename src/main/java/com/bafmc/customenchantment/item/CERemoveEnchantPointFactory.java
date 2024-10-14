package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CERemoveEnchantPointFactory extends CEItemFactory<CERemoveEnchantPoint> {
    public CERemoveEnchantPoint create(ItemStack itemStack) {
        return new CERemoveEnchantPoint(itemStack);
    }
}

