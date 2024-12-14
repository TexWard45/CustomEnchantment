package com.bafmc.customenchantment.item.removegem;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CERemoveGemStorage extends CEItemStorage<CERemoveGem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CERemoveGem getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CERemoveGem) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.REMOVE_GEM).get(name);
	}

}
