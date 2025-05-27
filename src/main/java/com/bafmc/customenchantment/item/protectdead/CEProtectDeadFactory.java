package com.bafmc.customenchantment.item.protectdead;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEProtectDeadFactory extends CEItemFactory<CEProtectDead> {
    public CEProtectDead create(ItemStack itemStack) {
        return new CEProtectDead(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.PROTECT_DEAD);
    }
}
