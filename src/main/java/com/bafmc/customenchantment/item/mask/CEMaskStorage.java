package com.bafmc.customenchantment.item.mask;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CEMaskStorage extends CEItemStorage<CEMask> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEMask getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEMask) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.MASK).get(name);
	}

}
