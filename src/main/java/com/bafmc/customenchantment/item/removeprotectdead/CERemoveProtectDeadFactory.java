package com.bafmc.customenchantment.item.removeprotectdead;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CERemoveProtectDeadFactory extends CEItemFactory<CERemoveProtectDead> {
    public CERemoveProtectDead create(ItemStack itemStack) {
        return new CERemoveProtectDead(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.REMOVE_PROTECT_DEAD);
    }
}
