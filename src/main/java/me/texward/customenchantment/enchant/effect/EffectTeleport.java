package me.texward.customenchantment.enchant.effect;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import me.texward.customenchantment.api.LocationFormat;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;

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
		location = locationFormat.getLegitLocation(player.getLocation(), location);

		if (location == null) {
			return;
		}
		player.teleport(location);
	}
}
