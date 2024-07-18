package me.texward.customenchantment.item;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;

public class CEEraseEnchantStorage extends CEItemStorage<CEEraseEnchant> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEEraseEnchant getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEEraseEnchant) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.EARSE_ENCHANT).get(name);
	}

}
