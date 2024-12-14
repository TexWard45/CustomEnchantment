package com.bafmc.customenchantment.item.removeenchant;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CERemoveEnchantStorage extends CEItemStorage<CERemoveEnchant> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CERemoveEnchant getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CERemoveEnchant) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.REMOVE_ENCHANT).get(name);
	}

}
