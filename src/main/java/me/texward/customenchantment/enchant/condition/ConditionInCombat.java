package me.texward.customenchantment.enchant.condition;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CombatLogXAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;

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