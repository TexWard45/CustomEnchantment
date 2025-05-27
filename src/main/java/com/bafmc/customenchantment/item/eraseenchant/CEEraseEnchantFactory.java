package com.bafmc.customenchantment.item.eraseenchant;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEEraseEnchantFactory extends CEItemFactory<CEEraseEnchant> {
    public CEEraseEnchant create(ItemStack itemStack) {
        return new CEEraseEnchant(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.EARSE_ENCHANT);
    }
}
