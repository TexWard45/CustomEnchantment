package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;

public class ConditionFakeSource extends ConditionHook {
	public String getIdentify() {
		return "FAKE_SOURCE";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		return data.isFakeSource();
	}
	
}