package com.bafmc.customenchantment.item.randombook;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CERandomBookFactory extends CEItemFactory<CERandomBook> {
    public CERandomBook create(ItemStack itemStack) {
        return new CERandomBook(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.RANDOM_BOOK);
    }
}
