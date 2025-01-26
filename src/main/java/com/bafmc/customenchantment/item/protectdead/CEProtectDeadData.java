package com.bafmc.customenchantment.item.protectdead;

import com.bafmc.customenchantment.item.CEItemData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CEProtectDeadData extends CEItemData {
	private int extraPoint;
	private int maxPoint;

	// Protect all armor and main/off hand slot
	private boolean advancedMode;
	
	public CEProtectDeadData() {
	}

	public CEProtectDeadData(String pattern, int extraPoint, int maxPoint, boolean advancedMode) {
		super(pattern);
		this.extraPoint = extraPoint;
		this.maxPoint = maxPoint;
		this.advancedMode = advancedMode;
	}
}
