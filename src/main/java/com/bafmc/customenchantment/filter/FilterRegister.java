package com.bafmc.customenchantment.filter;

import com.bafmc.bukkit.feature.filter.ItemStackFilter;

public class FilterRegister {
    public static void register() {
        ItemStackFilter.addFilter(new CEWeaponFilter());
    }
}
