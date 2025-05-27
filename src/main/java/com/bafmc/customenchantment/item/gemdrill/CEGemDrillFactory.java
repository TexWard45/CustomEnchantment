package com.bafmc.customenchantment.item.gemdrill;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEGemDrillFactory extends CEItemFactory<CEGemDrill> {
    public CEGemDrill create(ItemStack itemStack) {
        return new CEGemDrill(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.GEM_DRILL);
    }
}
