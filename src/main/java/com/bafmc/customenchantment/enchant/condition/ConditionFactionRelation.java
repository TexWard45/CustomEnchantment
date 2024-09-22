package com.bafmc.customenchantment.enchant.condition;

import java.util.List;

import org.bukkit.entity.Player;

import com.massivecraft.factions.perms.Relation;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import com.bafmc.bukkit.api.FactionAPI;
import com.bafmc.bukkit.utils.EnumUtils;

public class ConditionFactionRelation extends ConditionHook {
	private List<Relation> list;

	public String getIdentify() {
		return "FACTION_RELATION";
	}

	public void setup(String[] args) {
		this.list = EnumUtils.getEnumListByString(Relation.class, args[0]);
	}

	@Override
	public boolean match(CEFunctionData data) {
		Player player = data.getPlayer();
		Player enemy = data.getEnemyPlayer();

		return FactionAPI.isFactionSupport() && list.contains(FactionAPI.getRelationBetweenPlayer(player, enemy));
	}
}