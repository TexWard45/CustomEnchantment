package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CEEraseEnchantFactory extends CEItemFactory<CEEraseEnchant> {
    public CEEraseEnchant create(ItemStack itemStack) {
        return new CEEraseEnchant(itemStack);
    }
}
