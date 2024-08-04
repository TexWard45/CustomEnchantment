package me.texward.customenchantment.filter;

import com._3fmc.bukkit.feature.filter.ItemStackFilter;

public class FilterRegister {
    public static void register() {
        ItemStackFilter.addFilter(new CEWeaponFilter());
    }
}
