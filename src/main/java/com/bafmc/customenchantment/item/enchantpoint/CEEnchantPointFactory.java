package com.bafmc.customenchantment.item.enchantpoint;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEEnchantPointFactory extends CEItemFactory<CEEnchantPoint> {
    public CEEnchantPoint create(ItemStack itemStack) {
        return new CEEnchantPoint(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.ENCHANT_POINT);
    }
}
