package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.Parameter;

public class CEWeaponStorage extends CEItemStorage<CEWeapon> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEWeapon getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEWeapon) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.WEAPON).get(name);
	}

}
