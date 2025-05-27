package com.bafmc.customenchantment.item.increaseratebook;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEIncreaseRateBookFactory extends CEItemFactory<CEIncreaseRateBook> {
    public CEIncreaseRateBook create(ItemStack itemStack) {
        return new CEIncreaseRateBook(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.INCREASE_RATE_BOOK);
    }
}
