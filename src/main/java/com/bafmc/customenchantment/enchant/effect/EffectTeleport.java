package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.utils.LocationUtils;
import com.bafmc.customenchantment.api.LocationFormat;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class EffectTeleport extends EffectHook {
	private String format;

	public String getIdentify() {
		return "TELEPORT";
	}

	public void setup(String[] args) {
		this.format = args[0];
	}
	
	public boolean isAsync() {
		return false;
	}

	public void execute(CEFunctionData data) {
		Player player = null;
		LivingEntity enemy = null;
		if (data.getPlayer() != null)
			player = data.getPlayer();
		if (data.getEnemyLivingEntity() != null)
			enemy = data.getEnemyLivingEntity();

		LocationFormat locationFormat = new LocationFormat(this.format);
		Location location = locationFormat.getLocation(player, enemy);
		if (location == null) {
			return;
		}
		location = LocationUtils.getLegitLocation(player.getLocation(), location);

		if (location == null) {
			return;
		}
		player.teleport(location);
	}
}
