package com.bafmc.customenchantment.enchant.effect;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.bukkit.utils.RandomRange;
import com.bafmc.bukkit.utils.StringUtils;

public class EffectRemoveBlockDropBonusMining extends EffectHook {
	public String name;

	public String getIdentify() {
		return "REMOVE_BLOCK_DROP_BONUS_MINING";
	}

	public void setup(String[] args) {
		this.name = args[0];
	}

	public HashMap<Integer, RandomRange> convert(List<String> list) {
		HashMap<Integer, RandomRange> map = new HashMap<Integer, RandomRange>();
		for (String s : list) {
			List<String> list2 = StringUtils.split(s, " ", 0);
			map.put(Integer.valueOf(list2.get(0)), new RandomRange(list2.get(1)));
		}
		return map;
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.getSpecialMining().getBlockDropBonusSpecialMine().getBlockDropBonus().removeBonus(name);
	}
}
