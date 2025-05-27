package com.bafmc.customenchantment.item.removegem;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CERemoveGemFactory extends CEItemFactory<CERemoveGem> {
    public CERemoveGem create(ItemStack itemStack) {
        return new CERemoveGem(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.REMOVE_GEM);
    }
}

