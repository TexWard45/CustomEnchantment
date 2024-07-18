package me.texward.customenchantment.enchant.effect;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.CEPlaceholder;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.enchant.ModifyType;
import me.texward.customenchantment.event.CEPlayerStatsModifyEvent;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.StatsType;
import me.texward.texwardlib.util.RandomRange;

public class EffectFood extends EffectHook {
	private ModifyType modifyType;
	private String format;

	public String getIdentify() {
		return "FOOD";
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
		map.put("%player_value%", String.valueOf(player.getFoodLevel()));
		map.put("%enemy_value%",
				data.getEnemyPlayer() != null ? String.valueOf(data.getEnemyPlayer().getFoodLevel()) : "0");

		format = CEPlaceholder.setPlaceholder(format, map);

		double defaultValue = player.getFoodLevel();
		double currentValue = new RandomRange(format).getValue();

		CEPlayerStatsModifyEvent event = new CEPlayerStatsModifyEvent(cePlayer, StatsType.FOOD, modifyType,
				defaultValue, currentValue);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		defaultValue = event.getDefaultValue();
		currentValue = event.getCurrentValue();
		modifyType = event.getModifyType();

		switch (modifyType) {
		case ADD:
			player.setFoodLevel((int) defaultValue + (int) currentValue);
			break;
		case REMOVE:
			player.setFoodLevel((int) defaultValue - (int) currentValue);
			break;
		case SET:
			int intValue = (int) currentValue;

			if (currentValue == -1) {
				if (defaultValue < 20) {
					player.setFoodLevel(20);
				}
			} else
				player.setFoodLevel(intValue >= 0 ? intValue : 0);
			break;
		default:
			break;
		}
	}
}
