package me.texward.customenchantment.item;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;

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
