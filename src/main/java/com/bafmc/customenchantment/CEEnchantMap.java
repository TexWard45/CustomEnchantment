package com.bafmc.customenchantment;

import com.bafmc.bukkit.utils.StorageMap;
import com.bafmc.customenchantment.enchant.CEEnchant;

import java.util.ArrayList;
import java.util.List;

public class CEEnchantMap extends StorageMap<CEEnchant> {
	public List<String> getKeys() {
		return new ArrayList<String>(keySet());
	}
}
