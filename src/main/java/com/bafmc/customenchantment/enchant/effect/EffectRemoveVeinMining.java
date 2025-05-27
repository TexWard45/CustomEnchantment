package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;

public class EffectRemoveVeinMining extends EffectHook {
	private String name;

	public String getIdentify() {
		return "REMOVE_VEIN_MINING";
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
		cePlayer.getSpecialMining().getVeinSpecialMine().getVein().removeVein(name);
	}
}
