package com.bafmc.customenchantment.enchant;

import java.util.List;

public class ConditionOR {
	private List<ConditionHook> list;

	public ConditionOR(List<ConditionHook> list) {
		this.list = list;
	}

	public List<ConditionHook> getConditionHooks() {
		return list;
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}
