package me.texward.customenchantment.enchant.condition;

import org.bukkit.entity.Player;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;
import me.texward.texwardlib.api.FactionAPI;

public class ConditionInFactionTerriority extends ConditionHook {
	public String getIdentify() {
		return "IN_FACTION_TERRIORITY";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		Player player = data.getPlayer();

		return FactionAPI.isFactionSupport() && FactionAPI.isInTerriority(player);
	}
}