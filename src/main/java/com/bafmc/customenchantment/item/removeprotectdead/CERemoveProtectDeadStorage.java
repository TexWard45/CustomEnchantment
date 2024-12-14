package com.bafmc.customenchantment.item.removeprotectdead;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CERemoveProtectDeadStorage extends CEItemStorage<CERemoveProtectDead> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CERemoveProtectDead getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CERemoveProtectDead) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.REMOVE_PROTECT_DEAD).get(name);
	}

}
