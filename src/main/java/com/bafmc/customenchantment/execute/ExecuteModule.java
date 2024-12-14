package com.bafmc.customenchantment.execute;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;

public class ExecuteModule extends PluginModule<CustomEnchantment> {
    public ExecuteModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        new GiveItemExecute().register();
        new UseItemExecute().register();
    }
}
