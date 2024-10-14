package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.attribute.CustomAttributeType;

public class OptionType {
	public static CustomAttributeType OPTION_ATTACK = new CustomAttributeType("OPTION_ATTACK", 0).register();
	public static CustomAttributeType OPTION_DEFENSE = new CustomAttributeType("OPTION_DEFENSE", 0).register();
	public static CustomAttributeType OPTION_POWER = new CustomAttributeType("OPTION_POWER", 0).register();

	public static void init() {
	}
}
