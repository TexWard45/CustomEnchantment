package me.texward.customenchantment.enchant.effect;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
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

public class EffectPacketParticle extends EffectHook {
	private List<Particle> particles;
	private int distance;
	private String locationFormat;
	private String offsetFormat;
	private float particleData;
	private RandomRange count;
	private long lastMoveTimeRequired;
	private ParticleSupport pack = new ParticleSupport();

	public String getIdentify() {
		return "PACKET_PARTICLE";
	}

	public void setup(String[] args) {
		this.particles = pack.getParticleList(args[0]);
		this.distance = Integer.valueOf(args[1]);
		this.lastMoveTimeRequired = Long.valueOf(args[2]);
		this.locationFormat = args[3];
		this.offsetFormat = args[4];
		this.particleData = Float.valueOf(args[5]);
		this.count = new RandomRange(args[6]);
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

		Location location = new LocationFormat(lFormat).getLocation(player, enemy);
		Vector vector = new VectorFormat(oFormat).getVector(player, enemy);
		if (location != null) {
			int count = this.count.getIntValue();

			// If true, particle distance increases from 256 to 65536
			boolean distance = this.distance > 255;

			String world = location.getWorld().getName();
			for (Player target : Bukkit.getOnlinePlayers()) {
				if (target.getWorld().getName().equals(world)
						&& location.distance(target.getLocation()) <= this.distance) {
					for (Particle particle : particles) {
						this.pack.send(target, particle, distance, (float) location.getX(), (float) location.getY(),
								(float) location.getZ(), (float) vector.getX(), (float) vector.getY(),
								(float) vector.getZ(), particleData, count);
					}
				}
			}
		}
	}
}
