package com.bafmc.customenchantment.item.gem;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CEGemFactory extends CEItemFactory<CEGem> {
    public CEGem create(ItemStack itemStack) {
        return new CEGem(itemStack);
    }
}
