package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CEEnchantPointFactory extends CEItemFactory<CEEnchantPoint> {
    public CEEnchantPoint create(ItemStack itemStack) {
        return new CEEnchantPoint(itemStack);
    }
}
