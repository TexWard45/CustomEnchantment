package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.attribute.AttributeCalculate;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.attribute.RangeAttribute;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.player.attribute.AbstractAttributeMap;
import com.bafmc.customenchantment.player.attribute.AttributeMapData;
import com.bafmc.customenchantment.player.attribute.AttributeMapRegister;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerCustomAttribute extends CEPlayerExpansion {
	private Map<String, NMSAttribute> attributeMap = new ConcurrentHashMap<>();
	private Map<NMSAttributeType, Double> valueMap = new ConcurrentHashMap<>();
	private Multimap<CustomAttributeType, NMSAttribute> recalculateAttributeMap = LinkedHashMultimap.create();

	public PlayerCustomAttribute(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
	}

	public void onQuit() {
	}

	public double getValue(CustomAttributeType type, double value) {
		return AttributeCalculate.calculate(type, value, getAttributeList());
	}

	public double getValue(CustomAttributeType type, double value, List<NMSAttribute> otherList) {
		List<NMSAttribute> list = getAttributeList();
		list.addAll(otherList);

		return AttributeCalculate.calculate(type, value, list);
	}

	public double getValue(CustomAttributeType type) {
		return valueMap.getOrDefault(type, 0.0);
	}

	public void addCustomAttribute(String name, RangeAttribute attributeData) {
		attributeMap.put(name, attributeData);
	}

	public void removeCustomAttribute(String name) {
		attributeMap.remove(name);
	}

	public void recalculateAttribute() {
		CEPlayer cePlayer = getCEPlayer();

		Map<EquipSlot, CEWeaponAbstract> slotMap = cePlayer.getEquipment().getSlotMap();
		AttributeMapData data = AttributeMapData.builder()
				.cePlayer(cePlayer)
				.slotMap(slotMap)
				.build();

		Multimap<CustomAttributeType, NMSAttribute> attributeMap = LinkedHashMultimap.create();

		for (AbstractAttributeMap map : AttributeMapRegister.instance().getSingletonList()) {
			attributeMap.putAll(map.loadAttributeMap(data));
		}

		recalculateAttributeMap = attributeMap;

		for (NMSAttributeType type : NMSAttributeType.values()) {
			if (!(type instanceof CustomAttributeType customType)) {
				continue;
			}

			if (attributeMap.containsKey(customType)) {
				continue;
			}

			valueMap.put(customType, 0.0);
		}

		for (CustomAttributeType type : attributeMap.keySet()) {
			valueMap.put(type, AttributeCalculate.calculate(type, type.getBaseValue(), new ArrayList<>(attributeMap.get(type))));
		}
	}

	public List<NMSAttribute> getAttributeList() {
		return new ArrayList<>(attributeMap.values());
	}

	public Map<NMSAttributeType, Double> getValueMap() {
		return new LinkedHashMap<>(valueMap);
	}

	public Multimap<CustomAttributeType, NMSAttribute> getRecalculateAttributeMap() {
		return LinkedHashMultimap.create(recalculateAttributeMap);
	}
}