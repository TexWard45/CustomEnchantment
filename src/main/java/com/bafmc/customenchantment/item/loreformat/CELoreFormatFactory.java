package com.bafmc.customenchantment.item.loreformat;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CELoreFormatFactory extends CEItemFactory<CELoreFormat> {
    public CELoreFormat create(ItemStack itemStack) {
        return new CELoreFormat(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.LORE_FORMAT);
    }
}
