package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CEEnchantPointStorage extends CEItemStorage<CEEnchantPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEEnchantPoint getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEEnchantPoint) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.ENCHANT_POINT).get(name);
	}

}
