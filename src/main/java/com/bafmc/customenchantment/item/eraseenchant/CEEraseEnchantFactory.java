package com.bafmc.customenchantment.item.eraseenchant;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CEEraseEnchantFactory extends CEItemFactory<CEEraseEnchant> {
    public CEEraseEnchant create(ItemStack itemStack) {
        return new CEEraseEnchant(itemStack);
    }
}
