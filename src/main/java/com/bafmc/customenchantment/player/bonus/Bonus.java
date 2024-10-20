package com.bafmc.customenchantment.player.bonus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.bafmc.customenchantment.api.Pair;
import com.bafmc.customenchantment.attribute.RangeAttribute;

public class Bonus<K> {
	private ConcurrentHashMap<String, Pair<K, RangeAttribute>> map = new ConcurrentHashMap<String, Pair<K, RangeAttribute>>();

	public void put(String name, K key, RangeAttribute data) {
		map.put(name, new Pair<K, RangeAttribute>(key, data));
	}

	public void remove(String name) {
		map.remove(name);
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}

	public List<Pair<K, RangeAttribute>> getBonusList() {
		return new ArrayList<Pair<K, RangeAttribute>>(map.values());
	}
}
