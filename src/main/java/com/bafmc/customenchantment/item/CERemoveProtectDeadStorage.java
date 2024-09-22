package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CERemoveProtectDeadStorage extends CEItemStorage<CERemoveProtectDead> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CERemoveProtectDead getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CERemoveProtectDead) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.REMOVE_PROTECT_DEAD).get(name);
	}

}
