package me.texward.customenchantment.item;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;

public class CEProtectDeadStorage extends CEItemStorage<CEProtectDead> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEProtectDead getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEProtectDead) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.PROTECT_DEAD).get(name);
	}

}
