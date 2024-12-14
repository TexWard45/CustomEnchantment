package com.bafmc.customenchantment.item.gem;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemStorage;
import com.bafmc.customenchantment.item.CEItemType;

public class CEGemStorage extends CEItemStorage<CEGem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEGem getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		int level = parameter.getInteger(1, 1);
		CEGem ceGem = (CEGem) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.GEM).get(name);
		ceGem.getData().setLevel(level);
		return ceGem;
	}

}
