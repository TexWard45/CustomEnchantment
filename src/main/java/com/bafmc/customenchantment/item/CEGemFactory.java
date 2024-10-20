package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CEGemFactory extends CEItemFactory<CEGem> {
    public CEGem create(ItemStack itemStack) {
        return new CEGem(itemStack);
    }
}
