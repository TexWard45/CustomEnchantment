package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CEEraseEnchantStorage extends CEItemStorage<CEEraseEnchant> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEEraseEnchant getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEEraseEnchant) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.EARSE_ENCHANT).get(name);
	}

}
