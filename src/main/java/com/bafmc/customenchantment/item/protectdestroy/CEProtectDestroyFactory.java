package com.bafmc.customenchantment.item.protectdestroy;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CEProtectDestroyFactory extends CEItemFactory<CEProtectDestroy> {
    public CEProtectDestroy create(ItemStack itemStack) {
        return new CEProtectDestroy(itemStack);
    }
}
