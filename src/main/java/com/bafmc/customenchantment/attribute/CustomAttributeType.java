package com.bafmc.customenchantment.attribute;

import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CustomAttributeType {
	private static final ConcurrentHashMap<String, CustomAttributeType> map = new ConcurrentHashMap<String, CustomAttributeType>();
	// To dodge an attack, the player must have a dodge chance greater than the random number between 0 and 100.
	public static final CustomAttributeType DODGE_CHANCE = new CustomAttributeType("DODGE_CHANCE", 0).register();
	public static final CustomAttributeType CRITICAL_CHANCE = new CustomAttributeType("CRITICAL_CHANCE", 0).register();
	// Critical damage is calculated by multiplying the base damage by the critical damage. For example, if the base damage is 10 and the critical damage is 2, the critical damage will be 20.
	public static final CustomAttributeType CRITICAL_DAMAGE = new CustomAttributeType("CRITICAL_DAMAGE", 2).register();
	// Player will regenerate health every second.
	public static final CustomAttributeType HEALTH_REGENERATION = new CustomAttributeType("HEALTH_REGENERATION", 0).register();
	// The player will take less damage from attacks by a certain percentage between 0 and 100.
	public static final CustomAttributeType DAMAGE_REDUCTION = new CustomAttributeType("DAMAGE_REDUCTION", 0).register();
	// Life steal is a percentage of the damage dealt to the enemy that is returned to the player as health by a certain percentage between 0 and 100.
	public static final CustomAttributeType LIFE_STEAL_PERCENT = new CustomAttributeType("LIFE_STEAL", 0).register();
	private String type;
	private int baseValue;

	public CustomAttributeType(String type, int baseValue) {
		this.type = type.toUpperCase();
		this.baseValue = baseValue;
	}

	public static void init() {
	}

	public CustomAttributeType register() {
		if (map.containsKey(this.type)) {
			throw new IllegalArgumentException("Already exist this type!");
		}
		map.put(this.type, this);
		return this;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CustomAttributeType) {
			return ((CustomAttributeType) obj).getType().equals(getType());
		}
		return false;
	}

	public static CustomAttributeType valueOf(String name) {
		return map.get(name);
	}

	public static CustomAttributeType[] values() {
		return map.values().toArray(new CustomAttributeType[map.size()]);
	}

	public String toString() {
		return type;
	}
}