package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.PlayerTemporaryStorage;
import me.texward.customenchantment.player.TemporaryKey;

public class EffectDisableAutoSell extends EffectHook {
	
	public String getIdentify() {
		return "DISABLE_AUTO_SELL";
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
		storage.set(TemporaryKey.AUTO_SELL_ENABLE, false);
	}
}
