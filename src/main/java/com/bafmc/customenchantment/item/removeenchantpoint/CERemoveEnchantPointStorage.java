package com.bafmc.customenchantment.item.removeenchantpoint;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CERemoveEnchantPointStorage extends CEItemStorage<CERemoveEnchantPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CERemoveEnchantPoint getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CERemoveEnchantPoint) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.REMOVE_ENCHANT_POINT).get(name);
	}

}
