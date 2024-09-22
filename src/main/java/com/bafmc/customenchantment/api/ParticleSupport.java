package com.bafmc.customenchantment.api;

import net.minecraft.core.particles.ParticleOptions;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ParticleSupport {
	public Particle particle;

	public void send(Player target, Particle particle, float x, float y, float z, float offsetX,
			float offsetY, float offsetZ, float particleData, int count, boolean force) {
		ParticleOptions particleOptions = CraftParticle.createParticleParam(particle, particleData);
		send(target, particleOptions, x, y, z, offsetX, offsetY, offsetZ, count, force);
	}

	public void send(Player target, ParticleOptions e, float x, float y, float z, float offsetX,
			float offsetY, float offsetZ, int count, boolean force) {
		((CraftWorld) target.getWorld()).getHandle().sendParticles(
				((CraftWorld) target.getWorld()).getHandle().players(),
				null, // Sender // Paper - Particle API
				e, // Particle
				x, y, z, // Position
				count,  // Count
				offsetX, offsetY, offsetZ, // Random offset
				0, // Speed?
				force // Long distance
		);
	}

	public List<Particle> getParticleList(String format) {
		List<Particle> l = new ArrayList<Particle>();

		for (String s : format.split(",")) {
			try {
				l.add(Particle.valueOf(s));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return l;
	}
}
