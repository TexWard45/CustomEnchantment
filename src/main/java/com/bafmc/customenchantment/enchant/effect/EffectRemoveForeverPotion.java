package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;

public class EffectRemoveForeverPotion extends EffectHook {
	private String name;

	public String getIdentify() {
		return "REMOVE_FOREVER_POTION";
	}

    public boolean isForceEffectOnEnemyDead() {
        return true;
    }

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.name = args[0];
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.getPotion().removePotionType(name);
	}
}
