package com.bafmc.customenchantment.attribute;

import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import lombok.Getter;

@Getter
public class CustomAttributeType extends NMSAttributeType {
	public static final CustomAttributeType OPTION_ATTACK = (CustomAttributeType) (new CustomAttributeType("OPTION_ATTACK", "custom:player.option_attack", 0)).register();
	public static final CustomAttributeType OPTION_DEFENSE = (CustomAttributeType) (new CustomAttributeType("OPTION_DEFENSE", "custom:player.option_defense", 0)).register();
	public static final CustomAttributeType OPTION_POWER = (CustomAttributeType) (new CustomAttributeType("OPTION_POWER", "custom:player.option_power", 0)).register();
	public static final CustomAttributeType OPTION_ARMOR_PENETRATION = (CustomAttributeType) (new CustomAttributeType("OPTION_ARMOR_PENETRATION", "custom:player.option_armor_penetration", 0)).register();
	public static final CustomAttributeType STAT_EXP = (CustomAttributeType) (new CustomAttributeType("STAT_EXP", "custom:player.stat_exp", 0)).register();
	public static final CustomAttributeType STAT_FOOD = (CustomAttributeType) (new CustomAttributeType("STAT_FOOD", "custom:player.stat_food", 0)).register();
	public static final CustomAttributeType STAT_HEALTH = (CustomAttributeType) (new CustomAttributeType("STAT_HEALTH", "custom:player.stat_health", 0)).register();
	public static final CustomAttributeType STAT_OXYGEN = (CustomAttributeType) (new CustomAttributeType("STAT_OXYGEN", "custom:player.stat_oxygen", 0)).register();
	public static final CustomAttributeType STAT_ABSORPTION_HEART = (CustomAttributeType) (new CustomAttributeType("STAT_ABSORPTION_HEART", "custom:player.stat_absorption_heart", 0)).register();
	// To dodge an attack, the player must have a dodge chance greater than the random number between 0 and 100.
	public static final CustomAttributeType DODGE_CHANCE = (CustomAttributeType) (new CustomAttributeType("DODGE_CHANCE", "custom:player.dodge_chance", 0, true)).register();
	public static final CustomAttributeType CRITICAL_CHANCE = (CustomAttributeType) (new CustomAttributeType("CRITICAL_CHANCE", "custom:player.critical_chance", 0, true)).register();
	// Critical damage is calculated by multiplying the base damage by the critical damage. For example, if the base damage is 10 and the critical damage is 2, the critical damage will be 20.
	public static final CustomAttributeType CRITICAL_DAMAGE = (CustomAttributeType) (new CustomAttributeType("CRITICAL_DAMAGE", "custom:player.critical_damage", 2)).register();
	// Player will regenerate health every second.
	public static final CustomAttributeType HEALTH_REGENERATION = (CustomAttributeType) (new CustomAttributeType("HEALTH_REGENERATION", "custom:player.health_regeneration", 0)).register();
	// Player will regenerate health every second by a certain percentage between 0 and 100.
	public static final CustomAttributeType HEALTH_REGENERATION_PERCENT = (CustomAttributeType) (new CustomAttributeType("HEALTH_REGENERATION_PERCENT", "custom:player.health_regeneration_percent", 0, true)).register();
	// The player will take less damage from attacks by a certain percentage between 0 and 100.
	public static final CustomAttributeType DAMAGE_REDUCTION = (CustomAttributeType) (new CustomAttributeType("DAMAGE_REDUCTION", "custom:player.damage_reduction", 0, true)).register();
	// Life steal is a percentage of the damage dealt to the enemy that is returned to the player as health by a certain percentage between 0 and 100.
	public static final CustomAttributeType LIFE_STEAL = (CustomAttributeType) (new CustomAttributeType("LIFE_STEAL", "custom:player.life_steal", 0, true)).register();
	// Armor penetration is a percentage of the enemy's armor that is ignored by the player's attack by a certain percentage between 0 and 100.
	public static final CustomAttributeType ARMOR_PENETRATION = (CustomAttributeType) (new CustomAttributeType("ARMOR_PENETRATION", "custom:player.armor_penetration", 0, true)).register();
	// The player will have a chance to resist the slow effect by a certain percentage between 0 and 100.
	public static final CustomAttributeType SLOW_RESISTANCE = (CustomAttributeType) (new CustomAttributeType("SLOW_RESISTANCE", "custom:player.slow_resistance", 0, true)).register();
	// The player will have a chance to resist the stun/slow effect by a certain percentage between 0 and 100.
	public static final CustomAttributeType MAGIC_RESISTANCE = (CustomAttributeType) (new CustomAttributeType("MAGIC_RESISTANCE", "custom:player.magic_resistance", 0, true)).register();
	// Vulnerability is a bonus damage percentage taken by the player when attacked by other players.
	public static final CustomAttributeType VULNERABILITY = (CustomAttributeType) (new CustomAttributeType("VULNERABILITY", "custom:player.vulnerability", 0, true)).register();
	// Power is a custom attribute that can be used to calculate the player's overall power level, which can be used to balance the game.
	public static final CustomAttributeType POWER = (CustomAttributeType) (new CustomAttributeType("POWER", "custom:player.power", 0, false)).register();
	// Aoe range is a custom attribute that can be used to calculate the range of area of effect attacks.
	public static final CustomAttributeType AOE_RANGE = (CustomAttributeType) (new CustomAttributeType("AOE_RANGE", "custom:player.aoe_range", 1)).register();
	// Aoe damage ratio is a custom attribute that can be used to calculate the damage dealt by area of effect attacks.
	public static final CustomAttributeType AOE_DAMAGE_RATIO = (CustomAttributeType) (new CustomAttributeType("AOE_DAMAGE_RATIO", "custom:player.aoe_damage_ratio", 0, true)).register();
	// Accuracy is a counter to the target's dodge chance and determines hit success.
	public static final CustomAttributeType ACCURACY_CHANCE = (CustomAttributeType) (new CustomAttributeType("ACCURACY_CHANCE", "custom:player.accuracy_chance", 0, true)).register();
	// Mining power affects the tool's effectiveness when breaking or mining blocks.
	public static final CustomAttributeType MINING_POWER = (CustomAttributeType) (new CustomAttributeType("MINING_POWER", "custom:player.mining_power", 0)).register();

	private double baseValue;
	private boolean percent;
	@Getter
	private static CustomAttributeType[] values = new CustomAttributeType[0];

	public CustomAttributeType(String type, String minecraftId, double baseValue) {
		super(type, minecraftId);
		this.baseValue = baseValue;
	}

	public CustomAttributeType(String type, String minecraftId, double baseValue, boolean percent) {
		super(type, minecraftId);
		this.baseValue = baseValue;
		this.percent = percent;
	}

	public static void init() {
	}

	public NMSAttributeType register() {
		if (values == null) {
			values = new CustomAttributeType[0];
		}

		CustomAttributeType[] newValues = new CustomAttributeType[values.length + 1];
		System.arraycopy(values, 0, newValues, 0, values.length);
		newValues[values.length] = this;
		values = newValues;

		return super.register();
	}
}