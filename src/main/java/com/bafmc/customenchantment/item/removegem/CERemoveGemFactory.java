package com.bafmc.customenchantment.item.removegem;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CERemoveGemFactory extends CEItemFactory<CERemoveGem> {
    public CERemoveGem create(ItemStack itemStack) {
        return new CERemoveGem(itemStack);
    }
}

