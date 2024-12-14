package com.bafmc.customenchantment.item.protectdead;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CEProtectDeadStorage extends CEItemStorage<CEProtectDead> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEProtectDead getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEProtectDead) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.PROTECT_DEAD).get(name);
	}

}
