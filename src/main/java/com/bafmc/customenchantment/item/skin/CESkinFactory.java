package com.bafmc.customenchantment.item.skin;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CESkinFactory extends CEItemFactory<CESkin> {
    public CESkin create(ItemStack itemStack) {
        return new CESkin(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.SKIN);
    }
}
