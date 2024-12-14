package com.bafmc.customenchantment.item.loreformat;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CELoreFormatStorage extends CEItemStorage<CELoreFormat> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CELoreFormat getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CELoreFormat) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.LORE_FORMAT).get(name);
	}

}
