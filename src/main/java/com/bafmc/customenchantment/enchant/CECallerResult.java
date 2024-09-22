package com.bafmc.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.bafmc.customenchantment.attribute.AttributeData;

public class CECallerResult {
	private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
	private List<AttributeData> optionDataList = new ArrayList<AttributeData>();

	private CECallerResult() {
	}

	public static CECallerResult instance() {
		return new CECallerResult();
	}

	public List<AttributeData> getOptionDataList() {
		return optionDataList;
	}

	public CECallerResult setOptionDataList(List<AttributeData> optionDataList) {
		this.optionDataList = optionDataList;
		return this;
	}

	public boolean isSet(String key) {
		return map.containsKey(key);
	}

	public Object get(String key) {
		return map.get(key);
	}

	public CECallerResult put(String key, Object obj) {
		this.map.put(key, obj);
		return this;
	}
}
