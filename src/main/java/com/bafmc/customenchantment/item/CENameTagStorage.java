package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CENameTagStorage extends CEItemStorage<CENameTag> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CENameTag getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CENameTag) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.NAME_TAG).get(name);
	}

}
