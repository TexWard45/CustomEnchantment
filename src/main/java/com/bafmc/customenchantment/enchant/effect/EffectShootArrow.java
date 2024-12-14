package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bafmc.customenchantment.api.LocationFormat;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.listener.EntityListener;
import com.bafmc.bukkit.utils.RandomRange;

public class EffectShootArrow extends EffectHook {
	private String spawnLocationFormat;
	private String destinationLocationFormat;
	private RandomRange speed;
	private RandomRange spread;
	private boolean activeCE;

	public boolean isAsync() {
		return false;
	}

	public String getIdentify() {
		return "SHOOT_ARROW";
	}

	public void setup(String[] args) {
		this.spawnLocationFormat = args[0];
		this.destinationLocationFormat = args[1];
		this.speed = new RandomRange(args[2]);
		this.spread = new RandomRange(args[3]);
		this.activeCE = Boolean.valueOf(args[4]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		LivingEntity enemy = data.getEnemyLivingEntity();

		Location spawnLocation = new LocationFormat(spawnLocationFormat).getLocation(player, enemy);
		Location destinationLocation = new LocationFormat(destinationLocationFormat).getLocation(player, enemy);

		final int veloX = destinationLocation.getBlockX() - spawnLocation.getBlockX(),
				veloY = destinationLocation.getBlockY() - spawnLocation.getBlockY(),
				veloZ = destinationLocation.getBlockZ() - spawnLocation.getBlockZ();

		Vector vector = new Vector(veloX, veloY, veloZ).normalize();

		Arrow arrow = player.getWorld().spawnArrow(spawnLocation, vector, (float) this.speed.getValue(),
				(float) this.spread.getValue());
		if (activeCE) {
			EntityListener.putArrow(arrow, data.getWeaponAbstract());
		}
	}
}
