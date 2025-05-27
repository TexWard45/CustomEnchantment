package com.bafmc.customenchantment.item.gem;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEGemFactory extends CEItemFactory<CEGem> {
    public CEGem create(ItemStack itemStack) {
        return new CEGem(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.GEM);
    }
}
