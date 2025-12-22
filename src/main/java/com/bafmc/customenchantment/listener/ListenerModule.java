package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import org.bukkit.Bukkit;

public class ListenerModule extends PluginModule<CustomEnchantment> {
    public ListenerModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        new PlayerListener(getPlugin());
        new InventoryListener(getPlugin());
        new EntityListener(getPlugin());
        new BlockListener(getPlugin());
        new CEProtectDeadListener(getPlugin());
        new GuardListener(getPlugin());
        new BannerListener(getPlugin());
        new OutfitListener(getPlugin());

        if (Bukkit.getPluginManager().isPluginEnabled("StackMob")) {
            new MobStackDeathListener(getPlugin());
        } else {
            new MobDeathListener(getPlugin());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("CustomMenu")) {
            new CMenuListener(getPlugin());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
            new McMMOListener(getPlugin());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("CustomFarm")) {
            new CustomFarmListener(getPlugin());
        }
    }
}
