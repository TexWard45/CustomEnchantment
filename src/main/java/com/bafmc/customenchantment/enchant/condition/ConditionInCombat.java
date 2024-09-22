package com.bafmc.customenchantment.enchant.condition;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CombatLogXAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;

public class ConditionInCombat extends ConditionHook {
	public String getIdentify() {
		return "IN_COMBAT";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		Player player = data.getPlayer();

		return CombatLogXAPI.isCombatLogXSupport() && CombatLogXAPI.isInCombat(player);
	}
}