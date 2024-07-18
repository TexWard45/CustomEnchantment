package me.texward.customenchantment.item;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;

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
