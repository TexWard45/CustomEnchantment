package com.bafmc.customenchantment.enchant.condition;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import com.bafmc.bukkit.api.FactionAPI;

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