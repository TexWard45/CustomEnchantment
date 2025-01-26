package com.bafmc.customenchantment.attribute;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeOperation;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.attribute.AttributeModifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AttributeCalculate {
	public static double calculate(CEPlayer cePlayer, CustomAttributeType type, double amount, List<NMSAttribute> additionalList) {
		additionalList.addAll(cePlayer.getCustomAttribute().getAttributeList());
		return calculate(type, amount, additionalList);
	}
	
	public static double calculate(CustomAttributeType type, double amount, List<NMSAttribute> list) {
		List<NMSAttribute> newList = new ArrayList<>();

		for (NMSAttribute data : list) {
			NMSAttributeType dataType = data.getAttributeType();
			if (dataType == null || !dataType.equals(type)) {
				continue;
			}
			newList.add(data);
		}

		return calculate(amount, newList);
	}

	public static double calculate(double amount, List<NMSAttribute> list) {
		for (NMSAttribute data : list) {
			if (data.getOperation() == NMSAttributeOperation.ADD_NUMBER) {
				if (data instanceof RangeAttribute rangeAttribute) {
					if (!rangeAttribute.hasChance() || rangeAttribute.getChance().work()) {
						amount += rangeAttribute.getAmount();
					}
				}else {
					amount += data.getAmount();
				}
			}
		}

		double p = 1;
		for (NMSAttribute data : list) {
			if (data.getOperation() == NMSAttributeOperation.MULTIPLY_PERCENTAGE) {
				if (data instanceof RangeAttribute rangeAttribute) {
					if (!rangeAttribute.hasChance() || rangeAttribute.getChance().work()) {
						p += rangeAttribute.getAmount();
					}
				}else {
					p += data.getAmount();
				}
			}
		}
		amount *= p;

		double newAmount = 0;
		boolean has = false;
		for (NMSAttribute data : list) {
			if (data.getOperation() == NMSAttributeOperation.SET_NUMBER) {
				if (data instanceof RangeAttribute rangeAttribute) {
					if (!rangeAttribute.hasChance() || rangeAttribute.getChance().work()) {
						newAmount = Math.max(newAmount, rangeAttribute.getAmount());
						has = true;
					}
				}else {
					newAmount = Math.max(newAmount, data.getAmount());
					has = true;
				}
			}
		}
		if (has) {
			amount = newAmount;
		}

		for (NMSAttribute data : list) {
			if (data.getOperation() == NMSAttributeOperation.ADD_PERCENTAGE) {
				if (data instanceof RangeAttribute rangeAttribute) {
					if (!rangeAttribute.hasChance() || rangeAttribute.getChance().work()) {
						amount *= (1 + rangeAttribute.getAmount());
					}
				}else {
					amount *= (1 + data.getAmount());
				}
			}
		}

		return amount;
	}

	public static double calculateAttributeModifier(double amount, Collection<AttributeModifier> list){
		return calculateAttributeModifier(amount, list, false);
	}

	public static double calculateAttributeModifier(double amount, Collection<AttributeModifier> list, boolean noNegative) {
		for (AttributeModifier data : list) {
			if (data.getAmount() < 0 && noNegative) {
				continue;
			}

			if (data.getOperation() == AttributeModifier.Operation.ADD_NUMBER) {
				amount += data.getAmount();
			}
		}

		double p = 1;
		for (AttributeModifier data : list) {
			if (data.getAmount() < 0 && noNegative) {
				continue;
			}

			if (data.getOperation() == AttributeModifier.Operation.ADD_SCALAR) {
				p += data.getAmount();
			}
		}
		amount *= p;

		for (AttributeModifier data : list) {
			if (data.getAmount() < 0 && noNegative) {
				continue;
			}

			if (data.getOperation() == AttributeModifier.Operation.MULTIPLY_SCALAR_1) {
				amount *= (1 + data.getAmount());
			}
		}
		return amount;
	}
}
