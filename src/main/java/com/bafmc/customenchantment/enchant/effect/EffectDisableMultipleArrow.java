package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;

public class EffectDisableMultipleArrow extends EffectHook {
	
	public String getIdentify() {
		return "DISABLE_MULTIPLE_ARROW";
	}

	public void setup(String[] args) {
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		PlayerTemporaryStorage storage=  cePlayer.getTemporaryStorage();
		storage.set(TemporaryKey.MULTIPLE_ARROW_ENABLE, false);
	}
}
