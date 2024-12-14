package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.utils.AttributeUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class EffectUtil {
	public static List<PotionEffectType> getPotionEffectList(String format) {
		List<PotionEffectType> l = new ArrayList<PotionEffectType>();

		for (String s : format.split(",")) {
			try {
				PotionEffectType type = PotionEffectType.getByName(s);
				if (type == null) {
					continue;
				}
				l.add(PotionEffectType.getByName(s));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return l;
	}
	
	public static Operation getByNumber(int number) {
		switch (number) {
		case 0:
			return Operation.ADD_NUMBER;
		case 1:
			return Operation.ADD_SCALAR;
		case 2:
			return Operation.MULTIPLY_SCALAR_1;
		}
		return Operation.ADD_NUMBER;
	}

	public static Attribute getAttributeType(String type) {
		return AttributeUtils.getAttribute(type);
	}
}
