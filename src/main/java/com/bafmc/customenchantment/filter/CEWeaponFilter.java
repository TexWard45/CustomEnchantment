package com.bafmc.customenchantment.filter;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.feature.filter.ItemStackFilter;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CEWeaponFilter extends ItemStackFilter.ItemFilter {
    public boolean isMatch(int amount, ItemStack itemStack, ItemMeta itemMeta, AdvancedConfigurationSection config) {
        List<String> ceTypeWhitelist = config.getStringList("ce-type-whitelist");
        if (ceTypeWhitelist.isEmpty()) {
            return true;
        }

        CEItem ceItem = CEAPI.getCEItem(itemStack);
        if (ceItem == null) {
            return false;
        }

        return ceTypeWhitelist.contains(ceItem.getType());
    }
}
