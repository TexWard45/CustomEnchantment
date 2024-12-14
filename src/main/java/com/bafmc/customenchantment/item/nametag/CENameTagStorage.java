package com.bafmc.customenchantment.item.nametag;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CENameTagStorage extends CEItemStorage<CENameTag> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CENameTag getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CENameTag) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.NAME_TAG).get(name);
	}

}
