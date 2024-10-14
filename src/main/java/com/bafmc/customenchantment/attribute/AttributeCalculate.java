package com.bafmc.customenchantment.attribute;

import com.bafmc.customenchantment.attribute.AttributeData.Operation;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.attribute.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

public class AttributeCalculate {
	public static double calculate(CEPlayer cePlayer, CustomAttributeType type, double amount, List<AttributeData> additionalList) {
		additionalList.addAll(cePlayer.getCustomAttribute().getAttributeList());
		return calculate(type, amount, additionalList);
	}
	
	public static double calculate(CustomAttributeType type, double amount, List<AttributeData> list) {
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

	public static double calculateAttributeModifier(double amount, List<AttributeModifier> list){
		for (AttributeModifier data : list) {
			if (data.getOperation() == AttributeModifier.Operation.ADD_NUMBER) {
				amount += data.getAmount();
			}
		}

		double p = 1;
		for (AttributeModifier data : list) {
			if (data.getOperation() == AttributeModifier.Operation.ADD_SCALAR) {
				p += data.getAmount();
			}
		}
		amount *= p;

//		double newAmount = 0;
//		boolean has = false;
//		for (AttributeModifier data : list) {
//			if (data.getOperation() == Operation.SET_NUMBER) {
//				if (!data.hasChance() || data.getChance().work()) {
//					newAmount = Math.max(newAmount, data.getAmount());
//					has = true;
//				}
//			}
//		}
//		if (has) {
//			amount = newAmount;
//		}

		for (AttributeModifier data : list) {
			if (data.getOperation() == AttributeModifier.Operation.MULTIPLY_SCALAR_1) {
				amount *= (1 + data.getAmount());
			}
		}
		return amount;
	}
}
