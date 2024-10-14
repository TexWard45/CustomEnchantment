package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CENameTagFactory extends CEItemFactory<CENameTag> {
    public CENameTag create(ItemStack itemStack) {
        return new CENameTag(itemStack);
    }
}
