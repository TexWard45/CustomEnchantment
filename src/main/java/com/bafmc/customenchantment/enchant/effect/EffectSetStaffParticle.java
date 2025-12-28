package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class EffectSetStaffParticle extends EffectHook {
	public List<ParticleOptions> particle;

	public String getIdentify() {
		return "SET_STAFF_PARTICLE";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		String[] colorArgs = args[0].split(",");

		this.particle = new ArrayList<>();
		for (int i = 0; i < colorArgs.length; i++) {
			Particle.DustOptions data = null;
			java.awt.Color color = java.awt.Color.decode(colorArgs[i]);
			try {
				data = new Particle.DustOptions(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()), Integer.valueOf(args[1]));
			} catch (Exception e) {

			}
			Vector3f vector = new Vector3f(data.getColor().getRed() / 255f, data.getColor().getGreen() / 255f, data.getColor().getBlue() / 255f);
			this.particle.add(new DustParticleOptions(vector, data.getSize()));
		}
	}

	public void execute(CEFunctionData data) {
		CEPlayer cePlayer = CEAPI.getCEPlayer(data.getPlayer());
		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
		storage.set(TemporaryKey.STAFF_PARTICLE, this.particle);
	}
}
