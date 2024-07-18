package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;

public class EffectRemoveCustomAttribute extends EffectHook {
	private String attributeName;

	public String getIdentify() {
		return "REMOVE_CUSTOM_ATTRIBUTE";
	}

	public void setup(String[] args) {
		this.attributeName = args[0];
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
