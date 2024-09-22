package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;

public class EffectRemoveCustomAttribute extends EffectHook {
	private String attributeName;

	public String getIdentify() {
		return "REMOVE_CUSTOM_ATTRIBUTE";
	}

	public void setup(String[] args) {
		this.attributeName = args[0];
	}

    public boolean isForceEffectOnEnemyDead() {
        return true;
    }

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		cePlayer.getCustomAttribute().removeCustomAttribute(attributeName);
	}
}
