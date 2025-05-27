package com.bafmc.customenchantment.item.book;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEBookFactory extends CEItemFactory<CEBook> {
    public CEBook create(ItemStack itemStack) {
        return new CEBook(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.BOOK);
    }
}
