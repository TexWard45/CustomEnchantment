package com.bafmc.customenchantment.player.attribute.list;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.player.attribute.AbstractAttributeMap;
import com.bafmc.customenchantment.player.attribute.AttributeMapData;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

public class DefaultAttributeMap extends AbstractAttributeMap {
    public String getType() {
        return "DEFAULT";
    }

    public Multimap<CustomAttributeType, NMSAttribute> loadAttributeMap(AttributeMapData data) {
        Multimap<CustomAttributeType, NMSAttribute> attributeMap = LinkedHashMultimap.create();

        List<NMSAttribute> list = data.getCePlayer().getCustomAttribute().getAttributeList();;
        for (NMSAttribute nmsAttribute : list) {
            if (!(nmsAttribute.getAttributeType() instanceof CustomAttributeType type)) {
                continue;
            }

            attributeMap.put(type, nmsAttribute);
        }

        return attributeMap;
    }
}
