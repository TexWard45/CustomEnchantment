package com.bafmc.customenchantment.item.banner;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CEBannerStorage extends CEItemStorage<CEBanner> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEBanner getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEBanner) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.BANNER).get(name);
	}

}
