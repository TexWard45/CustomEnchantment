package me.texward.customenchantment.enchant.condition;

import org.bukkit.entity.Player;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;

public class ConditionAllowFlight extends ConditionHook {
	public String getIdentify() {
		return "ALLOW_FLIGHT";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		Player player = data.getPlayer();

		return player.getAllowFlight();
	}
}