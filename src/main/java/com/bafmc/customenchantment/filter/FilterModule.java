package com.bafmc.customenchantment.filter;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;

public class FilterModule extends PluginModule<CustomEnchantment> {
    public FilterModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        FilterRegister.register();
    }
}
