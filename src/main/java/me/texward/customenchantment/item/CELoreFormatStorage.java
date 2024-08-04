package me.texward.customenchantment.item;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;

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
