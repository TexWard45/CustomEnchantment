package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import com.bafmc.bukkit.utils.RandomRange;

public class EffectEnableMultipleArrow extends EffectHook {
	private RandomRange amountRange;
	private RandomRange velocityMod;
    private double damageRatio;
	private long cooldown;
	
	public String getIdentify() {
		return "ENABLE_MULTIPLE_ARROW";
	}

	public void setup(String[] args) {
		this.amountRange = new RandomRange(args[0]);
		this.velocityMod = new RandomRange(args[1]);
        this.damageRatio = Double.parseDouble(args[2]);
		this.cooldown = Long.parseLong(args[3]);
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
        storage.set(TemporaryKey.MULTIPLE_ARROW_DAMAGE_RATIO, this.damageRatio);
		storage.set(TemporaryKey.MULTIPLE_ARROW_COOLDOWN, this.cooldown);
	}
}
