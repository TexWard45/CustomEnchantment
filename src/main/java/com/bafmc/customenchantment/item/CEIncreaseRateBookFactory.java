package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CEIncreaseRateBookFactory extends CEItemFactory<CEIncreaseRateBook> {
    public CEIncreaseRateBook create(ItemStack itemStack) {
        return new CEIncreaseRateBook(itemStack);
    }
}
