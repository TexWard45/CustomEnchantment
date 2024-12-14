package com.bafmc.customenchantment.item.eraseenchant;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CEEraseEnchantStorage extends CEItemStorage<CEEraseEnchant> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEEraseEnchant getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEEraseEnchant) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.EARSE_ENCHANT).get(name);
	}

}
