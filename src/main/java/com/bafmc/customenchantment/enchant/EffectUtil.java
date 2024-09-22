package com.bafmc.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.potion.PotionEffectType;

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
		switch (type) {
		case "ATTACK_DAMAGE":
			return Attribute.GENERIC_ATTACK_DAMAGE;
		case "ATTACK_SPEED":
			return Attribute.GENERIC_ATTACK_SPEED;
		case "FOLLOW_RANGE":
			return Attribute.GENERIC_FOLLOW_RANGE;
		case "KNOCKBACK_RESISTANCE":
			return Attribute.GENERIC_KNOCKBACK_RESISTANCE;
		case "LUCK":
			return Attribute.GENERIC_LUCK;
		case "MAX_HEALTH":
			return Attribute.GENERIC_MAX_HEALTH;
		case "ARMOR":
			return Attribute.GENERIC_ARMOR;
		case "ARMOR_TOUGHNESS":
			return Attribute.GENERIC_ARMOR_TOUGHNESS;
		case "MOVEMENT_SPEED":
			return Attribute.GENERIC_MOVEMENT_SPEED;
		case "FLYING_SPEED":
			return Attribute.GENERIC_FLYING_SPEED;
		}
		return null;
	}
}
