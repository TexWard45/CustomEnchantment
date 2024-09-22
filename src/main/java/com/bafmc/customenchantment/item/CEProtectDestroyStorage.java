package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CEProtectDestroyStorage extends CEItemStorage<CEProtectDestroy> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEProtectDestroy getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEProtectDestroy) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.PROTECT_DESTROY).get(name);
	}

}
