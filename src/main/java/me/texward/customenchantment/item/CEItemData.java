package me.texward.customenchantment.item;

public abstract class CEItemData {
	private String pattern = CENBT.DEFAULT;

	public CEItemData() {
	}

	public CEItemData(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
}
