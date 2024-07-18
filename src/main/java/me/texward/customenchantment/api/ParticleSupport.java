package me.texward.customenchantment.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_16_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_16_R3.ParticleParam;

public class ParticleSupport {
	public Particle particle;

	public void send(Player target, Particle particle, boolean arg0, float x, float y, float z, float xOffset,
			float yOffset, float zOffset, float particleData, int count) {
		send(target, CraftParticle.toNMS(particle), arg0, x, y, z, xOffset, yOffset, zOffset, particleData, count);
	}
	
	public void send(Player target, Particle particle,  Object data, boolean arg0, float x, float y, float z, float xOffset,
			float yOffset, float zOffset, float particleData, int count) {
		send(target, CraftParticle.toNMS(particle, data), arg0, x, y, z, xOffset, yOffset, zOffset, particleData, count);
	}

	public void send(Player target, ParticleParam e, boolean arg0, float x, float y, float z, float xOffset,
			float yOffset, float zOffset, float particleData, int count) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(e, true, x, y, z, xOffset, yOffset,
				zOffset, particleData, count);
		((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);
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
