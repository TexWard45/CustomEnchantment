package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import org.bukkit.entity.Player;

public class ConditionInCombat extends ConditionHook {
	public String getIdentify() {
		return "IN_COMBAT";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		Player player = data.getPlayer();
		return CEAPI.isInCombat(player);
	}
}