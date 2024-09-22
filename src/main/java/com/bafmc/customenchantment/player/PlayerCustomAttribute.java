package com.bafmc.customenchantment.player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.bafmc.customenchantment.attribute.AttributeCalculate;
import com.bafmc.customenchantment.attribute.AttributeData;

/**
 * Handle attribute from player. Only handle attribute modifier which name
 * starts with specific prefix in variable PlayerAttribute.PREFIX.
 * 
 * @author nhata
 *
 */
public class PlayerCustomAttribute extends CEPlayerExpansion {
	private ConcurrentHashMap<String, AttributeData> map = new ConcurrentHashMap<String, AttributeData>();

	public PlayerCustomAttribute(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
	}

	public void onQuit() {
	}

	public double getAttributeValue(String type, double value) {
		return AttributeCalculate.calculate(type, value, getAttributeList());
	}

	public double getAttributeValue(String type, double value, List<AttributeData> otherList) {
		List<AttributeData> list = getAttributeList();
		list.addAll(otherList);

		return AttributeCalculate.calculate(type, value, list);
	}

	public void addCustomAttribute(String name, AttributeData attributeData) {
		map.put(name, attributeData);
	}

	public void removeCustomAttribute(String name) {
		map.remove(name);
	}

	public List<AttributeData> getAttributeList() {
		return new ArrayList<AttributeData>(map.values());
	}
}