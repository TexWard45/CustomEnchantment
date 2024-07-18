package me.texward.customenchantment.item;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;

public class CENameTagStorage extends CEItemStorage<CENameTag> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CENameTag getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CENameTag) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.NAME_TAG).get(name);
	}

}
