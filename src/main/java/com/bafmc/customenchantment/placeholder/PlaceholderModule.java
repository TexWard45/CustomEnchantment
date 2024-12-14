package com.bafmc.customenchantment.placeholder;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import org.bukkit.Bukkit;

public class PlaceholderModule extends PluginModule<CustomEnchantment> {
    public PlaceholderModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return;
        }

        Bukkit.getScheduler().runTask(getPlugin(), this::setupPlaceholders);
    }

    public void setupPlaceholders() {
        new CustomEnchantmentPlaceholder().register();
    }
}
