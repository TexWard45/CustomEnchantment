package com.bafmc.customenchantment.item.gemdrill;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CEGemDrillStorage extends CEItemStorage<CEGemDrill> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEGemDrill getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEGemDrill) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.GEM_DRILL).get(name);
	}

}
