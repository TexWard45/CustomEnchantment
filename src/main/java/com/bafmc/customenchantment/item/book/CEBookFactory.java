package com.bafmc.customenchantment.item.book;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CEBookFactory extends CEItemFactory<CEBook> {
    public CEBook create(ItemStack itemStack) {
        return new CEBook(itemStack);
    }
}
