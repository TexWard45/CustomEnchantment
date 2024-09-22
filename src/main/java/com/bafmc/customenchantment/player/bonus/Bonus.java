package com.bafmc.customenchantment.player.bonus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.bafmc.customenchantment.api.Pair;
import com.bafmc.customenchantment.attribute.AttributeData;

public class Bonus<K> {
	private ConcurrentHashMap<String, Pair<K, AttributeData>> map = new ConcurrentHashMap<String, Pair<K, AttributeData>>();

	public void put(String name, K key, AttributeData data) {
		map.put(name, new Pair<K, AttributeData>(key, data));
	}

	public void remove(String name) {
		map.remove(name);
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}

	public List<Pair<K, AttributeData>> getBonusList() {
		return new ArrayList<Pair<K, AttributeData>>(map.values());
	}
}
