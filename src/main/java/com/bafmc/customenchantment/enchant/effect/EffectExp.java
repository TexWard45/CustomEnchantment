package com.bafmc.customenchantment.enchant.effect;

import java.util.Map;

import com.bafmc.customenchantment.attribute.CustomAttributeType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.ModifyType;
import com.bafmc.customenchantment.event.CEPlayerStatsModifyEvent;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.bukkit.utils.RandomRange;
import com.bafmc.bukkit.utils.ExpUtils;

public class EffectExp extends EffectHook {
	private ModifyType modifyType;
	private String format;

	public String getIdentify() {
		return "EXP";
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
		map.put("%player_value%", String.valueOf(player.getTotalExperience()));
		map.put("%enemy_value%",
				data.getEnemyPlayer() != null ? String.valueOf(data.getEnemyPlayer().getTotalExperience()) : "0");

		format = CEPlaceholder.setPlaceholder(format, map);

		double defaultValue = player.getTotalExperience();
		double currentValue = new RandomRange(format).getValue();

		CEPlayerStatsModifyEvent event = new CEPlayerStatsModifyEvent(cePlayer, CustomAttributeType.STAT_EXP, modifyType,
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
			ExpUtils.setTotalExperience(player, player.getTotalExperience() + (int) currentValue);
			break;
		case REMOVE:
			ExpUtils.setTotalExperience(player, player.getTotalExperience() - (int) currentValue);
			break;
		case SET:
			if (currentValue == -1)
				ExpUtils.setTotalExperience(player, Integer.MAX_VALUE);
			else
				ExpUtils.setTotalExperience(player, (int) currentValue);
			break;
		default:
			break;
		}

	}
}
