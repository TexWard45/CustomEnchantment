package com.bafmc.customenchantment.enchant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.bafmc.customenchantment.attribute.RangeAttribute;

public class CECallerResult {
	private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
	private List<RangeAttribute> optionDataList = new ArrayList<RangeAttribute>();

	private CECallerResult() {
	}

	public static CECallerResult instance() {
		return new CECallerResult();
	}

	public List<RangeAttribute> getOptionDataList() {
		return optionDataList;
	}

	public CECallerResult setOptionDataList(List<RangeAttribute> optionDataList) {
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
