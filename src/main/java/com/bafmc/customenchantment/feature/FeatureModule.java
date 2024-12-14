package com.bafmc.customenchantment.feature;

import com.bafmc.bukkit.bafframework.feature.item.CustomEnchantmentItem;
import com.bafmc.bukkit.feature.item.ItemRegister;
import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;

public class FeatureModule extends PluginModule<CustomEnchantment> {
    public FeatureModule(CustomEnchantment plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        ItemRegister.instance().registerStrategy(CustomEnchantmentItem.class);
    }
}
