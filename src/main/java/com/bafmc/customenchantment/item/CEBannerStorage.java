package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CEBannerStorage extends CEItemStorage<CEBanner> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEBanner getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEBanner) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.BANNER).get(name);
	}

}
