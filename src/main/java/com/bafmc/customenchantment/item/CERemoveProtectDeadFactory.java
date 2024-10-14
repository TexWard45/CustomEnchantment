package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;

public class CERemoveProtectDeadFactory extends CEItemFactory<CERemoveProtectDead> {
    public CERemoveProtectDead create(ItemStack itemStack) {
        return new CERemoveProtectDead(itemStack);
    }
}
