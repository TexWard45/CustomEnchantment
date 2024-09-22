package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;

public class ConditionHasEnemy extends ConditionHook {
	public String getIdentify() {
		return "HAS_ENEMY";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		return data.getEnemyLivingEntity() != null;
	}

}
