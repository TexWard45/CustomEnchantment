package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.utils.RandomRange;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class EffectTrueDamage extends EffectHook {
	private String format;

	public String getIdentify() {
		return "TRUE_DAMAGE";
	}

	public void setup(String[] args) {
		this.format = args[0];
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		if (player.isDead()) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		if (cePlayer.getDeathTime() != data.getDeathTime()) {
			return;
		}

		String format = this.format;
		Map<String, String> map = CEPlaceholder.getCEFunctionDataPlaceholder(format, data);
		map.put(CEConstants.Placeholder.PLAYER_VALUE, String.valueOf(player.getHealth()));
		map.put(CEConstants.Placeholder.ENEMY_VALUE,
				data.getEnemyLivingEntity() != null ? String.valueOf(data.getEnemyLivingEntity().getHealth()) : "0");
		map.put(CEConstants.Placeholder.PLAYER_MAX_VALUE, String.valueOf(player.getMaxHealth()));
		map.put(CEConstants.Placeholder.ENEMY_MAX_VALUE, data.getEnemyLivingEntity() != null ? String.valueOf(data.getEnemyLivingEntity().getMaxHealth()) : "0");

		format = CEPlaceholder.setPlaceholder(format, map);

		double currentValue = player.getHealth();
		double changeValue = new RandomRange(format).getValue();

		double newValue = Math.max(0, currentValue - changeValue);

		if (newValue <= 0) {
			cePlayer.setDeathTimeBefore(true);
			cePlayer.setDeathTime(cePlayer.getDeathTime() + 1);

			new BukkitRunnable() {
				public void run() {
					player.setHealth(0);
				}
			}.runTask(CustomEnchantment.instance());
		} else {
			player.setHealth(Math.min(player.getMaxHealth(), newValue));
		}
	}
}
