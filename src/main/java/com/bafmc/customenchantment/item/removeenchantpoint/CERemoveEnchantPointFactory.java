package com.bafmc.customenchantment.item.removeenchantpoint;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CERemoveEnchantPointFactory extends CEItemFactory<CERemoveEnchantPoint> {
    public CERemoveEnchantPoint create(ItemStack itemStack) {
        return new CERemoveEnchantPoint(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.REMOVE_ENCHANT_POINT);
    }
}

