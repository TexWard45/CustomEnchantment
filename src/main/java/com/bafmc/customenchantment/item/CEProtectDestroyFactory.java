package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CEProtectDestroyFactory extends CEItemFactory<CEProtectDestroy> {
    public CEProtectDestroy create(ItemStack itemStack) {
        return new CEProtectDestroy(itemStack);
    }
}
