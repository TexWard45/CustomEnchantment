package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CERandomBookFactory extends CEItemFactory<CERandomBook> {
    public CERandomBook create(ItemStack itemStack) {
        return new CERandomBook(itemStack);
    }
}
