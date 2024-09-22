package com.bafmc.customenchantment;

import java.util.ArrayList;
import java.util.List;

import com.bafmc.customenchantment.enchant.CEEnchant;
import com.bafmc.bukkit.utils.StorageMap;

public class CEEnchantMap extends StorageMap<CEEnchant> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<String> getKeys() {
		return new ArrayList<String>(keySet());
	}
}
