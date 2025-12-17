package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EffectPacketCircleRedstoneParticle extends EffectPacketRedstoneParticle {
	public int amount;
	public double radius;

	public String getIdentify() {
		return "PACKET_CIRCLE_REDSTONE_PARTICLE";
	}

	public void setup(String[] args) {
		super.setup(args);

		this.radius = Double.valueOf(args[9]);
		this.amount = Integer.valueOf(args[10]);
	}

	public List<Location> getLocations(Player player, Location centerLocation) {
		List<Location> locations = new ArrayList<>();
		for (int i = 0; i < this.amount; i++) {
			double angle = 6.283185307179586D * i / this.amount;
			double x = centerLocation.getX() + this.radius * Math.cos(angle);
			double z = centerLocation.getZ() + this.radius * Math.sin(angle);
			locations.add(new Location(centerLocation.getWorld(), x, centerLocation.getY(), z));
		}
		return locations;
	}
}
