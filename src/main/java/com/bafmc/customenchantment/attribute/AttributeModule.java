package com.bafmc.customenchantment.attribute;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.player.attribute.AttributeMapRegister;

public class AttributeModule extends PluginModule<CustomEnchantment> {
    public AttributeModule(CustomEnchantment plugin) {
        super(plugin);
    }

    public void onEnable() {
        CustomAttributeType.init();
        AttributeMapRegister.init();
    }
}
