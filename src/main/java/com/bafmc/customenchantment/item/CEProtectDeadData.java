package com.bafmc.customenchantment.item;

public class CEProtectDeadData extends CEItemData {

	private int extraPoint;
	private int maxPoint;
	
	public CEProtectDeadData() {
	}

	public CEProtectDeadData(String pattern, int extraPoint, int maxPoint) {
		super(pattern);
		this.extraPoint = extraPoint;
		this.maxPoint = maxPoint;
	}

	public int getExtraPoint() {
		return extraPoint;
	}

	public void setExtraPoint(int extraPoint) {
		this.extraPoint = extraPoint;
	}

	public int getMaxPoint() {
		return maxPoint;
	}

	public void setMaxPoint(int maxPoint) {
		this.maxPoint = maxPoint;
	}

}
