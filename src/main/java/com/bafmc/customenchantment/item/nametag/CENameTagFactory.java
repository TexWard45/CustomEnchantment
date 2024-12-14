package com.bafmc.customenchantment.item.nametag;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CENameTagFactory extends CEItemFactory<CENameTag> {
    public CENameTag create(ItemStack itemStack) {
        return new CENameTag(itemStack);
    }
}
