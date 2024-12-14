package com.bafmc.customenchantment.item.randombook;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CERandomBookStorage extends CEItemStorage<CERandomBook> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CERandomBook getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CERandomBook) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.RANDOM_BOOK).get(name);
	}

}
