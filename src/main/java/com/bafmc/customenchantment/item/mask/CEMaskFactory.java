package com.bafmc.customenchantment.item.mask;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEMaskFactory extends CEItemFactory<CEMask> {
    public CEMask create(ItemStack itemStack) {
        return new CEMask(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.MASK);
    }
}
