package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import org.bukkit.entity.Player;

public class EffectDeactiveDash extends EffectHook {
	public String getIdentify() {
		return "DEACTIVE_DASH";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
	}

	public void execute(CEFunctionData data) {
        Player player = data.getPlayer();
        CEPlayer cePlayer = CEAPI.getCEPlayer(player);
        PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
        storage.set(TemporaryKey.DASH_ENABLE, false);
	}
}
