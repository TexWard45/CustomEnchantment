package com.bafmc.customenchantment.item.protectdestroy;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CEProtectDestroyStorage extends CEItemStorage<CEProtectDestroy> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEProtectDestroy getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEProtectDestroy) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.PROTECT_DESTROY).get(name);
	}

}
