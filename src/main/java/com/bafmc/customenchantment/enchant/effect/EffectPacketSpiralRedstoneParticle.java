package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectPacketSpiralRedstoneParticle extends EffectPacketRedstoneParticle {
	public double radius = 0.0D;
	public int amount = 0;
	public double startHeight = 0.0D;
	public double endHeight = 0.0D;
	public int times = 0;
	public double startAngle;
	public double plusAngle;

	public String getIdentify() {
		return "PACKET_SPIRAL_REDSTONE_PARTICLE";
	}

	public void setup(String[] args) {
		super.setup(args);

		this.radius = Double.valueOf(args[9]);
		this.amount = Integer.valueOf(args[10]);

		String[] heightSplit = args[11].split(",");
		if (heightSplit.length == 1) {
			this.endHeight = Double.valueOf(heightSplit[0]);
		}else {
			this.startHeight = Double.valueOf(heightSplit[0]);
			this.endHeight = Double.valueOf(heightSplit[1]);
		}

		this.times = Integer.valueOf(args[12]);
		this.startAngle = Double.valueOf(args[13]);
		this.plusAngle = Double.valueOf(args[14]);
	}

	public List<Location> getLocations(Player player, Location centerLocation) {
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();

		String id = getSettings().getName();

		List<Location> locations = new ArrayList<>();
		Map<String, Integer> turnMap = (Map<String, Integer>) storage.get(TemporaryKey.PARTICLE_TURN);
		if (turnMap == null) {
			turnMap = new HashMap<>();
			storage.set(TemporaryKey.PARTICLE_TURN, turnMap);
		}

		Map<String, Double> angleMap = (Map<String, Double>) storage.get(TemporaryKey.PARTICLE_ANGLE);
		if (angleMap == null) {
			angleMap = new HashMap<>();
			storage.set(TemporaryKey.PARTICLE_ANGLE, angleMap);
		}

		int turn = (turnMap.getOrDefault(id, Integer.valueOf(0))).intValue();
		double dynamicStartAngle = angleMap.getOrDefault(id, this.startAngle);
		double currentHeight = this.endHeight * turn / this.times;
		double y = centerLocation.getY() + startHeight + currentHeight;
		for (int i = 0; i < this.amount; i++) {
			double angle = 6.283185307179586D * ((double) i / this.amount + (double) turn / this.times + (dynamicStartAngle) / 360.0D);
			double x = centerLocation.getX() + this.radius * Math.cos(angle);
			double z = centerLocation.getZ() + this.radius * Math.sin(angle);
			locations.add(new Location(centerLocation.getWorld(), x, y, z));
		}

		if (currentHeight >= this.endHeight) {
			turnMap.put(id, 0);
			angleMap.put(id, (dynamicStartAngle + this.plusAngle) % 360.0D);
		} else {
			turnMap.put(id, turn + 1);
			angleMap.put(id, dynamicStartAngle); // keep current angle
		}
		return locations;
	}
}
