package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;

public class EffectRemoveBlockBonus extends EffectHook {
	// EXP, MONEY
	private String type;
	private String name;

	public String getIdentify() {
		return "REMOVE_BLOCK_BONUS";
	}

	public void setup(String[] args) {
		this.type = args[0];
		this.name = args[1];
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		if (type.equals("EXP")) {
			cePlayer.getBlockBonus().getExpBonus().remove(name);
		} else if (type.equals("MONEY")) {
			cePlayer.getBlockBonus().getMoneyBonus().remove(name);
		}
	}
}
