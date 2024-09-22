package com.bafmc.customenchantment.attribute;

import java.util.ArrayList;
import java.util.List;

import com.bafmc.customenchantment.attribute.AttributeData.Operation;
import com.bafmc.customenchantment.player.CEPlayer;

public class AttributeCalculate {
	public static double calculate(CEPlayer cePlayer, String type, double amount, List<AttributeData> list) {
		list.addAll(cePlayer.getCustomAttribute().getAttributeList());
		return calculate(type, amount, list);
	}
	
	public static double calculate(String type, double amount, List<AttributeData> list) {
		type = type.toUpperCase();

		List<AttributeData> newList = new ArrayList<AttributeData>();

		for (AttributeData data : list) {
			if (!data.getType().equals(type)) {
				continue;
			}
			newList.add(data);
		}

		return calculate(amount, newList);
	}

	public static double calculate(double amount, List<AttributeData> list) {
		for (AttributeData data : list) {
			if (data.getOperation() == Operation.ADD_NUMBER) {
				if (!data.hasChance() || data.getChance().work()) {
					amount += data.getAmount();
				}
			}
		}

		double p = 1;
		for (AttributeData data : list) {
			if (data.getOperation() == Operation.MULTIPLY_PERCENTAGE) {
				if (!data.hasChance() || data.getChance().work()) {
					p += data.getAmount();
				}
			}
		}
		amount *= p;

		double newAmount = 0;
		boolean has = false;
		for (AttributeData data : list) {
			if (data.getOperation() == Operation.SET_NUMBER) {
				if (!data.hasChance() || data.getChance().work()) {
					newAmount = Math.max(newAmount, data.getAmount());
					has = true;
				}
			}
		}
		if (has) {
			amount = newAmount;
		}

		for (AttributeData data : list) {
			if (data.getOperation() == Operation.ADD_PERCENTAGE) {
				if (!data.hasChance() || data.getChance().work()) {
					amount *= (1 + data.getAmount());
				}
			}
		}
		return amount;
	}
}
