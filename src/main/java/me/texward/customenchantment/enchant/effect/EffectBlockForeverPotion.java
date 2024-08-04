package me.texward.customenchantment.enchant.effect;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class EffectBlockForeverPotion extends EffectHook {
	private String name;
	private PotionEffectType potionEffectType;

	public String getIdentify() {
		return "BLOCK_FOREVER_POTION";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.name = args[0];
		this.potionEffectType = PotionEffectType.getByName(args[1]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		cePlayer.getPotion().blockPotionType(name, potionEffectType);
	}
}
