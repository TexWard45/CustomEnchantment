package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CEMaskFactory extends CEItemFactory<CEMask> {
    public CEMask create(ItemStack itemStack) {
        return new CEMask(itemStack);
    }
}
