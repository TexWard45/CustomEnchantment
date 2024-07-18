package me.texward.customenchantment.item;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;

public class CEEnchantPointStorage extends CEItemStorage<CEEnchantPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEEnchantPoint getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEEnchantPoint) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.ENCHANT_POINT).get(name);
	}

}
