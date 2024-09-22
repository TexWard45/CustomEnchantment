package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CELoreFormatStorage extends CEItemStorage<CELoreFormat> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CELoreFormat getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CELoreFormat) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.LORE_FORMAT).get(name);
	}

}
