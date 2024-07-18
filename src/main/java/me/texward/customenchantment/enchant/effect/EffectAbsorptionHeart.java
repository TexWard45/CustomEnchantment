package me.texward.customenchantment.enchant.effect;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
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
import net.minecraft.server.v1_16_R3.EntityPlayer;

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

		EntityPlayer entityPlayer = (EntityPlayer) ((CraftPlayer) player.getPlayer()).getHandle();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		String format = this.format;
		Map<String, String> map = CEPlaceholder.getCEFunctionDataPlaceholder(format, data);
		map.put("%player_value%", String.valueOf(entityPlayer.getAbsorptionHearts()));
		map.put("%enemy_value%",
				data.getEnemyPlayer() != null ? String.valueOf(
						((EntityPlayer) ((CraftPlayer) data.getEnemyPlayer()).getHandle()).getAbsorptionHearts())
						: "0");

		format = CEPlaceholder.setPlaceholder(format, map);

		double defaultValue = entityPlayer.getAbsorptionHearts();
		double currentValue = new RandomRange(format).getValue();

		CEPlayerStatsModifyEvent event = new CEPlayerStatsModifyEvent(cePlayer, StatsType.ABSORPTION_HEART, modifyType,
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
				entityPlayer.setAbsorptionHearts((float) currentValue);
			}
			break;
		case REMOVE:
			if (limit == null || defaultValue <= limit) {
				entityPlayer.setAbsorptionHearts((float) defaultValue - (float) currentValue);
			}
			break;
		case SET:
			entityPlayer.setAbsorptionHearts((float) currentValue);
			break;
		default:
			break;
		}
	}
}
