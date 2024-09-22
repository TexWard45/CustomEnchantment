package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.LocationFormat;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.guard.Guard;
import com.bafmc.customenchantment.guard.PlayerGuard;

public class EffectSummonGuard extends EffectHook {
	protected String name;
	protected EntityType entityType;
	protected String locationFormat;
	protected double speed;
	protected double playerRange;
	protected double attackRange;
	protected long aliveTime;

	public String getIdentify() {
		return "SUMMON_GUARD";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.name = args[0];
		this.entityType = EntityType.valueOf(args[1]);
		this.locationFormat = args[2];
		this.speed = Double.parseDouble(args[3]);
		this.playerRange = Double.parseDouble(args[4]);
		this.attackRange = Double.parseDouble(args[5]);
		this.aliveTime = Long.parseLong(args[6]);
	}

	public void execute(CEFunctionData data) {
		summon(data);
	}

	public Entity summon(CEFunctionData data) {
		Player player = data.getPlayer();

		if (player == null) {
			return null;
		}

		PlayerGuard playerGuard = CustomEnchantment.instance().getGuardManager().getPlayerGuard(player);

		String name = this.name.replace("%player%", player.getName());

		if (playerGuard.containsGuardName(name)) {
			return null;
		}

		Guard guard = new Guard(playerGuard, name, player.getName(), entityType, playerRange, attackRange, aliveTime);

		LocationFormat locationFormat = new LocationFormat(this.locationFormat);
		
		Location location = locationFormat.getLocation(player, null);
		location = locationFormat.getLegitLocation(player.getLocation(), location);
		
		Entity entity = guard.summon(location, speed);
		
		playerGuard.addGuard(guard);
		return entity;
	}
}
