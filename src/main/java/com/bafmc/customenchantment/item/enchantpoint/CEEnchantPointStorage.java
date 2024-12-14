package com.bafmc.customenchantment.item.enchantpoint;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CEEnchantPointStorage extends CEItemStorage<CEEnchantPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEEnchantPoint getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEEnchantPoint) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.ENCHANT_POINT).get(name);
	}

}
