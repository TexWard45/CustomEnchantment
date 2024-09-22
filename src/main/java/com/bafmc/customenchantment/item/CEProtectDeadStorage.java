package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

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
