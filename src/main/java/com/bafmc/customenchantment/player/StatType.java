package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.attribute.CustomAttributeType;

public class StatType {
	public static final CustomAttributeType STAT_EXP = new CustomAttributeType("STAT_EXP", 0).register();
	public static final CustomAttributeType STAT_FOOD = new CustomAttributeType("STAT_FOOD", 0).register();
	public static final CustomAttributeType STAT_HEALTH = new CustomAttributeType("STAT_HEALTH", 0).register();
	public static final CustomAttributeType STAT_OXYGEN = new CustomAttributeType("STAT_OXYGEN", 0).register();
	public static final CustomAttributeType STAT_ABSORPTION_HEART = new CustomAttributeType("STAT_ABSORPTION_HEART", 0).register();

	public static void init() {

	}
}