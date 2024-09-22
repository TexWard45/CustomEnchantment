package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.mining.ExplosionSpecialMine.Explosion;

public class EffectAddExplosionMining extends EffectHook {
	private String name;
	private Explosion explosion;

	public String getIdentify() {
		return "ADD_EXPLOSION_MINING";
	}

	public void setup(String[] args) {
		this.name = args[0];
		this.explosion = new Explosion(Integer.valueOf(args[1]), Double.valueOf(args[2]), Boolean.valueOf(args[3]));
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.getSpecialMining().getExplosionSpecialMine().getExplosion().addExplosion(name, explosion);
	}
}
