package me.texward.customenchantment;

import java.util.ArrayList;
import java.util.List;

import me.texward.customenchantment.enchant.CEEnchant;
import me.texward.texwardlib.util.StorageMap;

public class CEEnchantMap extends StorageMap<CEEnchant> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<String> getKeys() {
		return new ArrayList<String>(keySet());
	}
}
