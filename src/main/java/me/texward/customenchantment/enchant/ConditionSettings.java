package me.texward.customenchantment.enchant;

public class ConditionSettings {
	private Target target;
	private boolean negative;

	public ConditionSettings() {
	}

	public ConditionSettings(Target target, boolean negative) {
		this.target = target;
		this.negative = negative;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}

}
