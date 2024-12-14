package com.bafmc.customenchantment.item.removeprotectdead;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CERemoveProtectDeadFactory extends CEItemFactory<CERemoveProtectDead> {
    public CERemoveProtectDead create(ItemStack itemStack) {
        return new CERemoveProtectDead(itemStack);
    }
}
