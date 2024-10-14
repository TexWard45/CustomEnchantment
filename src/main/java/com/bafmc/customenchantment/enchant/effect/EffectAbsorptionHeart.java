package com.bafmc.customenchantment.enchant.effect;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.ModifyType;
import com.bafmc.customenchantment.event.CEPlayerStatsModifyEvent;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.StatType;
import com.bafmc.bukkit.utils.RandomRange;

public class EffectAbsorptionHeart extends EffectHook {
	private ModifyType modifyType;
	private String format;
	private Float limit;

	public String getIdentify() {
		return "ABSORPTION_HEART";
	}

	public void setup(String[] args) {
		this.modifyType = ModifyType.valueOf(args[0]);
		this.format = args[1];
		this.limit = args.length > 2 ? Float.valueOf(args[2]) : null;
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		net.minecraft.world.entity.player.Player entityPlayer = ((CraftPlayer) player.getPlayer()).getHandle();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		String format = this.format;
		Map<String, String> map = CEPlaceholder.getCEFunctionDataPlaceholder(format, data);
		map.put("%player_value%", String.valueOf(entityPlayer.getAbsorptionAmount()));
		map.put("%enemy_value%",
				data.getEnemyPlayer() != null ? String.valueOf(
						(((CraftPlayer) data.getEnemyPlayer()).getHandle()).getAbsorptionAmount())
						: "0");

		format = CEPlaceholder.setPlaceholder(format, map);

		double defaultValue = entityPlayer.getAbsorptionAmount();
		double currentValue = new RandomRange(format).getValue();

		CEPlayerStatsModifyEvent event = new CEPlayerStatsModifyEvent(cePlayer, StatType.STAT_ABSORPTION_HEART, modifyType,
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
			currentValue = defaultValue + (float) currentValue;

			if (currentValue <= limit) {
				entityPlayer.setAbsorptionAmount((float) currentValue);
			}
			break;
		case REMOVE:
			if (limit == null || defaultValue <= limit) {
				entityPlayer.setAbsorptionAmount((float) defaultValue - (float) currentValue);
			}
			break;
		case SET:
			entityPlayer.setAbsorptionAmount((float) currentValue);
			break;
		default:
			break;
		}
	}
}
