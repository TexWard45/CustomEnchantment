package com.bafmc.customenchantment.item.banner;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.inventory.ItemStack;

public class CEBannerFactory extends CEItemFactory<CEBanner> {
    public CEBanner create(ItemStack itemStack) {
        return new CEBanner(itemStack);
    }
}
