package com.bafmc.customenchantment.custommenu;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.custommenu.api.CustomMenuAPI;
import org.bukkit.Bukkit;

public class CustomMenuModule extends PluginModule<CustomEnchantment> {
    public CustomMenuModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("CustomMenu")) {
            return;
        }

        new CEBookCatalog().register();
        new CustomEnchantmentTradeItemCompare().register();
        new CustomEnchantmentItemDisplaySetup().register();

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), this::registerCMenu);
    }

    public void registerCMenu() {
        CustomMenuAPI.registerPlugin(getPlugin(), getPlugin().getMenuFolder());
    }

    public void onDisable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("CustomMenu")) {
            return;
        }

        CustomMenuAPI.unregisterPlugin(getPlugin());
    }
}
