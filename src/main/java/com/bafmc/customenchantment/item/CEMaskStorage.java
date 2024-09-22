package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CEMaskStorage extends CEItemStorage<CEMask> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEMask getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEMask) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.MASK).get(name);
	}

}
