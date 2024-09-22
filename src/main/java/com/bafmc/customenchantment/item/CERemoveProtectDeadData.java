package com.bafmc.customenchantment.item;

public class CERemoveProtectDeadData extends CEItemData {

	private String protectDeadType;
	
	public CERemoveProtectDeadData() {
	}

	public CERemoveProtectDeadData(String pattern, String protectDeadType) {
		super(pattern);
		this.protectDeadType = protectDeadType;
	}

	public String getProtectDeadType() {
		return protectDeadType;
	}

	public void setProtectDeadType(String protectDeadType) {
		this.protectDeadType = protectDeadType;
	}

}
