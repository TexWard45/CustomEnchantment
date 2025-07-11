package com.bafmc.customenchantment.item.sigil;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CESigilFactory extends CEItemFactory<CESigil> {
    public CESigil create(ItemStack itemStack) {
        return new CESigil(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.SIGIL);
    }
}
