package com.bafmc.customenchantment.item.nametag;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CENameTagFactory extends CEItemFactory<CENameTag> {
    public CENameTag create(ItemStack itemStack) {
        return new CENameTag(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.NAME_TAG);
    }
}
