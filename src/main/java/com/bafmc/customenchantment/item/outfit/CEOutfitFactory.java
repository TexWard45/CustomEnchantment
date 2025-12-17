package com.bafmc.customenchantment.item.outfit;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEOutfitFactory extends CEItemFactory<CEOutfit> {
    public CEOutfit create(ItemStack itemStack) {
        return new CEOutfit(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.OUTFIT);
    }
}
