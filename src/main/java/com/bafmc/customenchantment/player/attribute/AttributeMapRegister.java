package com.bafmc.customenchantment.player.attribute;

import com.bafmc.bukkit.template.singleton.SingletonRegister;
import com.bafmc.customenchantment.player.attribute.list.DefaultAttributeMap;
import com.bafmc.customenchantment.player.attribute.list.EquipSlotAttributeMap;

public class AttributeMapRegister extends SingletonRegister<AbstractAttributeMap> {
    private static final AttributeMapRegister instance = new AttributeMapRegister();

    public static AttributeMapRegister instance() {
        return instance;
    }

    public static void init() {
        instance.registerSingleton(new DefaultAttributeMap());
        instance.registerSingleton(new EquipSlotAttributeMap());
    }
}
