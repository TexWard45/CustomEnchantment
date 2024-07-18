package me.texward.customenchantment.item;

import me.texward.customenchantment.api.MaterialList;

public class CEEnchantPointData extends CEItemData {

	private int extraPoint;
	private int maxPoint;
	private MaterialList applies;

	public CEEnchantPointData() {
	}

	public CEEnchantPointData(String pattern, int extraPoint, int maxPoint, MaterialList applies) {
		super(pattern);
		this.extraPoint = extraPoint;
		this.maxPoint = maxPoint;
		this.applies = applies;
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

	public MaterialList getApplies() {
		return applies;
	}

	public void setApplies(MaterialList applies) {
		this.applies = applies;
	}

}
