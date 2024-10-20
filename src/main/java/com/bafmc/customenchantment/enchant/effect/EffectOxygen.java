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

public class EffectOxygen extends EffectHook {
	private ModifyType modifyType;
	private String format;

	public String getIdentify() {
		return "OXYGEN";
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
		map.put("%player_value%", String.valueOf(player.getRemainingAir()));
		map.put("%enemy_value%",
				data.getEnemyPlayer() != null ? String.valueOf(data.getEnemyPlayer().getRemainingAir()) : "0");

		format = CEPlaceholder.setPlaceholder(format, map);
		
		double defaultValue = player.getRemainingAir();
		double currentValue = new RandomRange(format).getValue();

		CEPlayerStatsModifyEvent event = new CEPlayerStatsModifyEvent(cePlayer, CustomAttributeType.STAT_OXYGEN, modifyType,
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
			int newValue = (int) defaultValue + (int) currentValue;
			if (newValue > 20) {
				newValue = 20;
			}
			player.setRemainingAir(newValue);
			break;
		case REMOVE:
			int newValue2 = (int) defaultValue - (int) currentValue;
			if (newValue2 < 0) {
				newValue2 = 0;
			}
			player.setRemainingAir(newValue2);
			break;
		case SET:
			int newValue3 = (int) currentValue;
			if (newValue3 > 20)
				newValue3 = 20;
			if (newValue3 < 0)
				newValue3 = 0;
			player.setRemainingAir(newValue3);
			break;
		default:
			break;
		}
	}
}
