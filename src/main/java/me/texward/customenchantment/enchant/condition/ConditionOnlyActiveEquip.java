package me.texward.customenchantment.enchant.condition;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;

public class ConditionOnlyActiveEquip extends ConditionHook {
	public String getIdentify() {
		return "ONLY_ACTIVE_EQUIP";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		return data.getEquipSlot() == data.getActiveEquipSlot();
	}

}
