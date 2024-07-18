package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.mining.ExplosionSpecialMine.Explosion;

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
