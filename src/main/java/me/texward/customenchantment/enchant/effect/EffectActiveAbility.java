package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.PlayerAbility;

public class EffectActiveAbility extends EffectHook {
	private PlayerAbility.Type type;
	private String name;

	public String getIdentify() {
		return "ACTIVE_ABILITY";
	}

	public void setup(String[] args) {
		this.type = PlayerAbility.Type.valueOf(args[0]);
		this.name = args[1];
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.getAbility().setCancel(type, name, false);
	}
}
