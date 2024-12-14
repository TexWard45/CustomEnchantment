package com.bafmc.customenchantment.item.mask;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CEMaskFactory extends CEItemFactory<CEMask> {
    public CEMask create(ItemStack itemStack) {
        return new CEMask(itemStack);
    }
}
