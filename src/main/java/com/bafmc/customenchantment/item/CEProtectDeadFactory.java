package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CEProtectDeadFactory extends CEItemFactory<CEProtectDead> {
    public CEProtectDead create(ItemStack itemStack) {
        return new CEProtectDead(itemStack);
    }
}
