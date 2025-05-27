package com.bafmc.customenchantment.item.banner;

import com.bafmc.customenchantment.item.CEItemFactory;
import com.bafmc.customenchantment.item.CEItemType;
import org.bukkit.inventory.ItemStack;

public class CEBannerFactory extends CEItemFactory<CEBanner> {
    public CEBanner create(ItemStack itemStack) {
        return new CEBanner(itemStack);
    }

    public boolean isMatchType(String type) {
        return type.equals(CEItemType.BANNER);
    }
}
