package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CERandomBookStorage extends CEItemStorage<CERandomBook> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CERandomBook getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CERandomBook) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.RANDOM_BOOK).get(name);
	}

}
