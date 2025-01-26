package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerAbility;

public class EffectDeactiveAbility extends EffectHook {
	private PlayerAbility.Type type;
	private String name;
	private long duration;

	public String getIdentify() {
		return "DEACTIVE_ABILITY";
	}

	public void setup(String[] args) {
		this.type = PlayerAbility.Type.valueOf(args[0]);
		this.name = args[1];
		if (args.length > 2) {
			this.duration = Long.parseLong(args[2]);
		}
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.getAbility().setCancel(type, name, true, duration);
	}
}
