package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;

public class EffectAddFurnaceMining extends EffectHook {
	private String name;
	private double chance;

	public String getIdentify() {
		return "ADD_FURNACE_MINING";
	}

	public void setup(String[] args) {
		this.name = args[0];
		this.chance = Double.valueOf(args[1]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}
		
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.getSpecialMining().getFurnaceSpecialMine().getFurnace().addFurnaceChance(name, chance);
	}
}
