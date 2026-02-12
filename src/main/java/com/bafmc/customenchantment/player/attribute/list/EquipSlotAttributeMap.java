package com.bafmc.customenchantment.player.attribute.list;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeSlot;
import com.bafmc.bukkit.bafframework.utils.EquipSlotUtils;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.WeaponAttribute;
import com.bafmc.customenchantment.player.attribute.AbstractAttributeMap;
import com.bafmc.customenchantment.player.attribute.AttributeMapData;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Map;

public class EquipSlotAttributeMap extends AbstractAttributeMap {
    public String getType() {
        return "EQUIP_SLOT";
    }

    public Multimap<CustomAttributeType, NMSAttribute> loadAttributeMap(AttributeMapData data) {
        Multimap<CustomAttributeType, NMSAttribute> attributeMap = LinkedHashMultimap.create();
        Map<EquipSlot, CEWeaponAbstract> slotMap = data.getSlotMap();

        for (EquipSlot equipSlot : slotMap.keySet()) {
            CEWeaponAbstract weapon = slotMap.get(equipSlot);
            if (weapon == null) {
                continue;
            }

            WeaponAttribute weaponAttribute = weapon.getWeaponAttribute();
            List<NMSAttribute> list = weaponAttribute.getAttributeList();

            for (NMSAttribute nmsAttribute : list) {
                if (!(nmsAttribute.getAttributeType() instanceof CustomAttributeType type)) {
                    continue;
                }

                String slot = nmsAttribute.getSlot();

                // Handle null slot - null means attribute applies to all slots
                NMSAttributeSlot nmsSlot = (slot != null) ? NMSAttributeSlot.getByName(slot) : null;
                if (nmsSlot == null || EquipSlotUtils.isSameSlot(equipSlot, nmsSlot)) {
                    attributeMap.put(type, nmsAttribute);
                }
            }
        }

        return attributeMap;
    }
}
