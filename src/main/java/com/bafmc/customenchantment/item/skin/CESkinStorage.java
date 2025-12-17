package com.bafmc.customenchantment.item.skin;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CESkinStorage extends CEItemStorage<CESkin> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CESkin getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CESkin) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.SKIN).get(name);
	}

}
