package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CERemoveEnchantFactory extends CEItemFactory<CERemoveEnchant> {
    public CERemoveEnchant create(ItemStack itemStack) {
        return new CERemoveEnchant(itemStack);
    }
}
