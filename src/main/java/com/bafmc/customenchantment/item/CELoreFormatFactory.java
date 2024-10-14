package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CELoreFormatFactory extends CEItemFactory<CELoreFormat> {
    public CELoreFormat create(ItemStack itemStack) {
        return new CELoreFormat(itemStack);
    }
}
