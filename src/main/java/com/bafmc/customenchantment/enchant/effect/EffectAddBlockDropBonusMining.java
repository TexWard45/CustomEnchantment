package com.bafmc.customenchantment.enchant.effect;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.mining.BlockDropBonusSpecialMine.MiningBlockDropBonus.BonusType;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.RandomRange;
import com.bafmc.bukkit.utils.StringUtils;

public class EffectAddBlockDropBonusMining extends EffectHook {
	public String name;
	public BonusType type;
	public MaterialList require;
	public MaterialData reward;
	public HashMap<Integer, RandomRange> amount;
	public boolean removeItem;
	private Chance chance;

	public String getIdentify() {
		return "ADD_BLOCK_DROP_BONUS_MINING";
	}

	public void setup(String[] args) {
		this.name = args[0];
		this.type = BonusType.valueOf(args[1]);
		this.require = MaterialList.getMaterialList(StringUtils.split(args[2], ",", 0));
		this.reward = MaterialData.getMaterialNMSByString(args[3]);
		this.amount = convert(StringUtils.split(args[4], ",", 0));
		this.removeItem = Boolean.valueOf(args[5]);
		this.chance = new Chance(args.length > 6 ? args[6] : "100");
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
		cePlayer.getSpecialMining().getBlockDropBonusSpecialMine().getBlockDropBonus().addBonus(name, type, require, reward, amount, removeItem, chance);
	}
}
