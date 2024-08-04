package me.texward.customenchantment.filter;

import com._3fmc.bukkit.config.AdvancedConfigurationSection;
import com._3fmc.bukkit.feature.filter.ItemStackFilter;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.item.CEItem;
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
