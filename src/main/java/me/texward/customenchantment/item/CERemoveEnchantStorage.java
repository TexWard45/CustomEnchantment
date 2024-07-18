package me.texward.customenchantment.item;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;

public class CERemoveEnchantStorage extends CEItemStorage<CERemoveEnchant> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CERemoveEnchant getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CERemoveEnchant) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.REMOVE_ENCHANT).get(name);
	}

}
