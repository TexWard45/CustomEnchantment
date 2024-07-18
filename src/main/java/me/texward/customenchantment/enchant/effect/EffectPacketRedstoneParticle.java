package me.texward.customenchantment.enchant.effect;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.craftbukkit.v1_16_R3.CraftParticle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.api.LocationFormat;
import me.texward.customenchantment.api.ParticleSupport;
import me.texward.customenchantment.api.VectorFormat;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.TemporaryKey;
import me.texward.texwardlib.util.RandomRange;
import net.minecraft.server.v1_16_R3.ParticleParam;

public class EffectPacketRedstoneParticle extends EffectHook {
	public ParticleParam particle;
	public int distance;
	public String locationFormat;
	public String offsetFormat;
	public String colorFormat;
	public float particleData;
	public RandomRange count;
	public long lastMoveTimeRequired;
	public ParticleSupport pack = new ParticleSupport();
	public Random random = new Random();
	public Particle.DustOptions data;

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
		java.awt.Color color = java.awt.Color.decode(args[7]);
		try {
			this.data = new DustOptions(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()),
					(int) Integer.valueOf(args[8]));
		} catch (Exception e) {

		}
		this.particle = CraftParticle.toNMS(Particle.REDSTONE, data);
	}

	public static void main(String[] args) {
		System.out.println(java.awt.Color.decode("#55FFFF"));
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

		Vector vector = new VectorFormat(cFormat).getVector(player, enemy);
		if (location != null) {
			int count = this.count.getIntValue();

			// If true, particle distance increases from 256 to 65536
			boolean distance = this.distance > 255;

			String world = location.getWorld().getName();
			for (Player target : Bukkit.getOnlinePlayers()) {
				if (target.getWorld().getName().equals(world)
						&& location.distance(target.getLocation()) <= this.distance) {
					this.pack.send(target, particle, distance, (float) location.getX(), (float) location.getY(),
							(float) location.getZ(), (float) vector.getX(), (float) vector.getY(),
							(float) vector.getZ(), particleData, count);
				}
			}
		}
	}
}
