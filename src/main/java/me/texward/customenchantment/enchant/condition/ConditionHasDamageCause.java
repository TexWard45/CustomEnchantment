package me.texward.customenchantment.enchant.condition;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;

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
