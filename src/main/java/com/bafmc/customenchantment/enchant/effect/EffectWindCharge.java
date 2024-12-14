package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.utils.RandomRange;
import com.bafmc.customenchantment.api.LocationFormat;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.listener.EntityListener;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftBreezeWindCharge;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class EffectWindCharge extends EffectHook {
	private String spawnLocationFormat;
	private String destinationLocationFormat;
	private RandomRange speed;
	private RandomRange spread;
	private boolean activeCE;

	public boolean isAsync() {
		return false;
	}

	public String getIdentify() {
		return "WIND_CHARGE";
	}

	public void setup(String[] args) {
		this.spawnLocationFormat = args[0];
		this.destinationLocationFormat = args[1];
		this.speed = new RandomRange(args[2]);
		this.spread = new RandomRange(args[3]);
//		this.activeCE = Boolean.valueOf(args[4]);
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

		net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge breezeWindCharge = net.minecraft.world.entity.EntityType.BREEZE_WIND_CHARGE.create(((CraftWorld) player.getWorld()).getHandle());

		breezeWindCharge.moveTo(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), spawnLocation.getYaw(), spawnLocation.getPitch());
		breezeWindCharge.shoot(vector.getX(), vector.getY(), vector.getZ(), (float) speed.getValue(), (float) spread.getValue());
		((CraftWorld) player.getWorld()).getHandle().addFreshEntity(breezeWindCharge);
//		if (activeCE) {
//			EntityListener.putArrow(arrow, data.getWeaponAbstract());
//		}
	}
}
