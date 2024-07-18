package me.texward.customenchantment.enchant.condition;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;

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
