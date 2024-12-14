package com.bafmc.customenchantment.item.removeenchant;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CERemoveEnchantFactory extends CEItemFactory<CERemoveEnchant> {
    public CERemoveEnchant create(ItemStack itemStack) {
        return new CERemoveEnchant(itemStack);
    }
}
