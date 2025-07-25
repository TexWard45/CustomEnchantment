package com.bafmc.customenchantment.item.banner;

import com.bafmc.bukkit.utils.MaterialUtils;
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

    public boolean isMatchType(ItemStack itemStack) {
        return MaterialUtils.isSimilar(itemStack.getType(), "BANNER") || MaterialUtils.isSimilar(itemStack.getType(), "DRAGON_HEAD");
    }

    public boolean isAutoGenerateNewItem() {
        return true;
    }
}
