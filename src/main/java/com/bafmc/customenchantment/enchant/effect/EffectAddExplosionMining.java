package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.mining.ExplosionSpecialMine.Explosion;
import org.bukkit.entity.Player;

public class EffectAddExplosionMining extends EffectHook {
	private String name;
	private Explosion explosion;

	public String getIdentify() {
		return "ADD_EXPLOSION_MINING";
	}

	public void setup(String[] args) {
		this.name = args[0];

		if (args.length == 4) {
			this.explosion = new Explosion(Integer.parseInt(args[1]), Double.parseDouble(args[2]), Boolean.parseBoolean(args[3]), false, null);
		}else if (args.length == 5) {
			this.explosion = new Explosion(Integer.parseInt(args[1]), Double.parseDouble(args[2]), Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]), null);
		}else if (args.length == 6) {
			this.explosion = new Explosion(Integer.parseInt(args[1]), Double.parseDouble(args[2]), Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]), MaterialList.getMaterialList(StringUtils.split(args[5], ",", 0)));
		}
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
