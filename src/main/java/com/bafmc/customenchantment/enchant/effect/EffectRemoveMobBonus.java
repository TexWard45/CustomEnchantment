package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;

public class EffectRemoveMobBonus extends EffectHook {
	// EXP, MONEY
	private String type;
	private String name;

	public String getIdentify() {
		return "REMOVE_MOB_BONUS";
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
		if (type.equals(CEConstants.RewardType.EXP)) {
			cePlayer.getMobBonus().getExpBonus().remove(name);
		} else if (type.equals(CEConstants.RewardType.MONEY)) {
			cePlayer.getMobBonus().getMoneyBonus().remove(name);
		} else if (type.equals(CEConstants.RewardType.MS_EXP)) {
			cePlayer.getMobBonus().getMobSlayerExpBonus().remove(name);
		}
	}
}
