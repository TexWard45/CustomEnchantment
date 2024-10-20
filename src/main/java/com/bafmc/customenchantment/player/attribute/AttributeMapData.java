package com.bafmc.customenchantment.player.attribute;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.player.CEPlayer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
public class AttributeMapData {
    private CEPlayer cePlayer;
    private Map<EquipSlot, CEWeaponAbstract> slotMap;
}
