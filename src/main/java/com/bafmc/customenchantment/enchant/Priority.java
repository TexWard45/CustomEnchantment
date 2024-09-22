package com.bafmc.customenchantment.enchant;

public enum Priority {
	LOWEST, LOW, NORMAL, HIGH, HIGHEST, MORNITOR;

	public static final Priority[] HIGH_TO_LOW = new Priority[] { MORNITOR, HIGHEST, HIGH, NORMAL, LOW, LOWEST };
	public static final Priority[] LOW_TO_HIGH = new Priority[] { LOWEST, LOW, NORMAL, HIGH, HIGHEST, MORNITOR };
}
