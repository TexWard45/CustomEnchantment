package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.PlayerTemporaryStorage;
import me.texward.customenchantment.player.TemporaryKey;
import me.texward.texwardlib.util.RandomRange;

public class EffectEnableMultipleArrow extends EffectHook {
	private RandomRange amountRange;
	private RandomRange velocityMod;
	
	public String getIdentify() {
		return "ENABLE_MULTIPLE_ARROW";
	}

	public void setup(String[] args) {
		this.amountRange = new RandomRange(args[0]);
		this.velocityMod = new RandomRange(args[1]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		PlayerTemporaryStorage storage=  cePlayer.getTemporaryStorage();
		storage.set(TemporaryKey.MULTIPLE_ARROW_ENABLE, true);
		storage.set(TemporaryKey.MULTIPLE_ARROW_AMOUNT, this.amountRange.clone());
		storage.set(TemporaryKey.MULTIPLE_ARROW_MOD, this.velocityMod.clone());
	}
}
