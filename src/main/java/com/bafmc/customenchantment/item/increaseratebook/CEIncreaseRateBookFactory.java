package com.bafmc.customenchantment.item.increaseratebook;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CEIncreaseRateBookFactory extends CEItemFactory<CEIncreaseRateBook> {
    public CEIncreaseRateBook create(ItemStack itemStack) {
        return new CEIncreaseRateBook(itemStack);
    }
}
