package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.utils.RandomRange;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.api.LocationFormat;
import com.bafmc.customenchantment.api.ParticleSupport;
import com.bafmc.customenchantment.api.VectorFormat;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.*;

public class EffectPacketRedstoneParticle extends EffectHook {
	public List<ParticleOptions> particle;
	public int distance;
	public String locationFormat;
	public String offsetFormat;
	public String colorFormat;
	public float particleData;
	public RandomRange count;
	public long lastMoveTimeRequired;
	public ParticleSupport pack = new ParticleSupport();
	public Random random = new Random();

	public String getIdentify() {
		return "PACKET_REDSTONE_PARTICLE";
	}

	public void setup(String[] args) {
		this.distance = Integer.valueOf(args[0]);
		this.lastMoveTimeRequired = Long.valueOf(args[1]);
		this.locationFormat = args[2];
		this.offsetFormat = args[3];
		this.colorFormat = args[4];
		this.particleData = Float.valueOf(args[5]);
		this.count = new RandomRange(args[6]);

		String[] colorArgs = args[7].split(",");

		this.particle = new ArrayList<>();
		for (int i = 0; i < colorArgs.length; i++) {
			Particle.DustOptions data = null;
			java.awt.Color color = java.awt.Color.decode(colorArgs[i]);
			try {
				data = new DustOptions(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()),
						(int) Integer.valueOf(args[8]));
			} catch (Exception e) {

			}
			Vector3f vector = new Vector3f(data.getColor().getRed() / 255f, data.getColor().getGreen() / 255f, data.getColor().getBlue() / 255f);
			this.particle.add(new DustParticleOptions(vector, data.getSize()));
		}
	}

	public void execute(CEFunctionData data) {
		Player player = null;
		LivingEntity enemy = null;
		if (data.getPlayer() != null) {
			player = data.getPlayer();

			CEPlayer cePlayer = CEAPI.getCEPlayer(player);

			long lastMoveTime = cePlayer.getTemporaryStorage().getLong(TemporaryKey.LAST_MOVE_TIME, 0);
			if (lastMoveTimeRequired > 0 && System.currentTimeMillis() - lastMoveTime >= lastMoveTimeRequired) {
				return;
			}

			if (lastMoveTimeRequired < 0 && System.currentTimeMillis() - lastMoveTime <= -lastMoveTimeRequired) {
				return;
			}
		}

		if (data.getEnemyLivingEntity() != null)
			enemy = data.getEnemyLivingEntity();

		String lFormat = this.locationFormat;

		String oFormat = this.offsetFormat;

		String cFormat = this.colorFormat;

		Location location = new LocationFormat(lFormat).getLocation(player, enemy);

		Vector oVector = new VectorFormat(oFormat).getVector(player, enemy);
		location.setX(location.getX() + RandomRange.getRandom(random, -oVector.getX(), oVector.getX()));
		location.setY(location.getY() + RandomRange.getRandom(random, -oVector.getY(), oVector.getY()));
		location.setZ(location.getZ() + RandomRange.getRandom(random, -oVector.getZ(), oVector.getZ()));

		List<Location> locations = getLocations(player, location);

		Vector vector = new VectorFormat(cFormat).getVector(player, enemy);
		int count = this.count.getIntValue();

		// If true, particle distance increases from 256 to 65536
		boolean distance = this.distance > 255;

		String world = location.getWorld().getName();
		for (Player target : Bukkit.getOnlinePlayers()) {
			if (target.getWorld().getName().equals(world) && location.distance(target.getLocation()) <= this.distance) {
				for (Location loc : locations) {
					this.pack.send(target, getParticle(player), (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(),
							(float) vector.getX(), (float) vector.getY(), (float) vector.getZ(), count, distance);
				}
			}
		}
	}

	public List<Location> getLocations(Player player, Location centerLocation) {
		List<Location> locations = new ArrayList<>();
		locations.add(centerLocation);
		return locations;
	}

	public ParticleOptions getParticle(Player player) {
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();

		String id = getSettings().getName();

		Map<String, Integer> turnMap = (Map<String, Integer>) storage.get(TemporaryKey.PARTICLE_COLOR);
		if (turnMap == null) {
			turnMap = new HashMap<>();
			storage.set(TemporaryKey.PARTICLE_COLOR, turnMap);
		}

		int turn = (turnMap.getOrDefault(id, Integer.valueOf(0))).intValue();
		int index = turn % this.particle.size();
		turnMap.put(id, turn + 1);
		return this.particle.get(index);
	}
}
