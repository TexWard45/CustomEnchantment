package com.bafmc.customenchantment.item;

public class CEUnifyData extends CEItemData {
	private String normalDisplay;
	private String boldDisplay;

	public CEUnifyData() {
	}

	public CEUnifyData(String pattern, String normalDisplay, String boldDisplay) {
		super(pattern);
		this.normalDisplay = normalDisplay;
		this.boldDisplay = boldDisplay;
	}

	public String getNormalDisplay() {
		return normalDisplay;
	}

	public String getBoldDisplay() {
		return boldDisplay;
	}

	public void setNormalDisplay(String normalDisplay) {
		this.normalDisplay = normalDisplay;
	}

	public void setBoldDisplay(String boldDisplay) {
		this.boldDisplay = boldDisplay;
	}
}
