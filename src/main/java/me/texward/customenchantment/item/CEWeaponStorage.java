package me.texward.customenchantment.item;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.Parameter;

public class CEWeaponStorage extends CEItemStorage<CEWeapon> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CEWeapon getByParameter(Parameter parameter) {
		String name = parameter.getString(0);
		return (CEWeapon) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.WEAPON).get(name);
	}

}
