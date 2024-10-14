package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.attribute.AttributeCalculate;
import com.bafmc.customenchantment.attribute.AttributeData;
import com.bafmc.customenchantment.attribute.CustomAttributeType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handle attribute from player. Only handle attribute modifier which name
 * starts with specific prefix in variable PlayerAttribute.PREFIX.
 * 
 * @author nhata
 *
 */
public class PlayerCustomAttribute extends CEPlayerExpansion {
	private ConcurrentHashMap<String, AttributeData> attributeMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<CustomAttributeType, Double> valueMap = new ConcurrentHashMap<>();

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

	public double getValue(CustomAttributeType type, double value, List<AttributeData> otherList) {
		List<AttributeData> list = getAttributeList();
		list.addAll(otherList);

		return AttributeCalculate.calculate(type, value, list);
	}

	public double getValue(CustomAttributeType type) {
		return valueMap.getOrDefault(type, 0.0);
	}

	public void addCustomAttribute(String name, AttributeData attributeData) {
		attributeMap.put(name, attributeData);
	}

	public void removeCustomAttribute(String name) {
		attributeMap.remove(name);
	}

	public void recalculateAttribute() {
		List<CustomAttributeType> types = new ArrayList<>(valueMap.keySet());

		for (AttributeData data : attributeMap.values()) {
			if (!types.contains(data.getType())) {
				types.add(data.getType());
			}
		}

		for (CustomAttributeType type : types) {
			recalculateAttribute(type);
		}
	}

	public void recalculateAttribute(CustomAttributeType type) {
		valueMap.put(type, AttributeCalculate.calculate(type, type.getBaseValue(), getAttributeList()));
	}

	public List<AttributeData> getAttributeList() {
		return new ArrayList<>(attributeMap.values());
	}

	public Map<CustomAttributeType, Double> getValueMap() {
		return new LinkedHashMap<>(valueMap);
	}
}