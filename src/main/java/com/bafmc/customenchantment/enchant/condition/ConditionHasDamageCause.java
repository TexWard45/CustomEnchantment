package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;

public class ConditionHasDamageCause extends ConditionHook {
	public String getIdentify() {
		return "HAS_DAMAGE_CAUSE";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		return data.getDamageCause() != null;
	}

}
