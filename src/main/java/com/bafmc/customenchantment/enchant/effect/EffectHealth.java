package com.bafmc.customenchantment.enchant.effect;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.ModifyType;
import com.bafmc.customenchantment.event.CEPlayerStatsModifyEvent;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.StatType;
import com.bafmc.bukkit.utils.RandomRange;

public class EffectHealth extends EffectHook {
	private ModifyType modifyType;
	private String format;

	public String getIdentify() {
		return "HEALTH";
	}

	public void setup(String[] args) {
		this.modifyType = ModifyType.valueOf(args[0]);
		this.format = args[1];
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		String format = this.format;
		Map<String, String> map = CEPlaceholder.getCEFunctionDataPlaceholder(format, data);
		map.put("%player_value%", String.valueOf(player.getHealth()));
		map.put("%enemy_value%",
				data.getEnemyLivingEntity() != null ? String.valueOf(data.getEnemyLivingEntity().getHealth()) : "0");
		map.put("%player_max_value%", String.valueOf(player.getMaxHealth()));
		map.put("%enemy_max_value%", data.getEnemyLivingEntity() != null ? String.valueOf(data.getEnemyLivingEntity().getMaxHealth()) : "0");

		format = CEPlaceholder.setPlaceholder(format, map);

		double defaultValue = player.getHealth();
		double currentValue = new RandomRange(format).getValue();

		CEPlayerStatsModifyEvent event = new CEPlayerStatsModifyEvent(cePlayer, StatType.STAT_HEALTH, modifyType,
				defaultValue, currentValue);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		defaultValue = event.getDefaultValue();
		currentValue = event.getCurrentValue();
		modifyType = event.getModifyType();

		double newValue = 0;

		switch (modifyType) {
		case ADD:
			newValue = defaultValue + currentValue;
			break;
		case REMOVE:
			newValue = defaultValue - currentValue;
			if (newValue < 0) {
				newValue = 0;
			}
			break;
		case SET:
			newValue = currentValue;
			break;
		}

		if (cePlayer.getDeathTime() != data.getDeathTime() || player.isDead()) {
			return;
		}
		
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
