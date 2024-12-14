package com.bafmc.customenchantment.item.gemdrill;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CEGemDrillFactory extends CEItemFactory<CEGemDrill> {
    public CEGemDrill create(ItemStack itemStack) {
        return new CEGemDrill(itemStack);
    }
}
