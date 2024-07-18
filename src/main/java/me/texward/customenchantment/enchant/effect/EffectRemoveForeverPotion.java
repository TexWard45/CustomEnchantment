package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;

public class EffectRemoveForeverPotion extends EffectHook {
	private String name;

	public String getIdentify() {
		return "REMOVE_FOREVER_POTION";
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
