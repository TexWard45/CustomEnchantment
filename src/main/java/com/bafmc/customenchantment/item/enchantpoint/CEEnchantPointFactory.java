package com.bafmc.customenchantment.item.enchantpoint;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CEEnchantPointFactory extends CEItemFactory<CEEnchantPoint> {
    public CEEnchantPoint create(ItemStack itemStack) {
        return new CEEnchantPoint(itemStack);
    }
}
