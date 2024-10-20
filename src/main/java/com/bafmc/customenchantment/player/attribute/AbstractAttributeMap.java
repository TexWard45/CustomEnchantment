package com.bafmc.customenchantment.player.attribute;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.template.singleton.AbstractSingleton;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.google.common.collect.Multimap;

public abstract class AbstractAttributeMap extends AbstractSingleton {
    public abstract Multimap<CustomAttributeType, NMSAttribute> loadAttributeMap(AttributeMapData data);
}
