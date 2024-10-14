package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CEBookFactory extends CEItemFactory<CEBook> {
    public CEBook create(ItemStack itemStack) {
        return new CEBook(itemStack);
    }
}
