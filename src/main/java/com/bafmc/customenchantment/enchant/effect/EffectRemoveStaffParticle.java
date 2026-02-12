package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import net.minecraft.core.particles.ParticleOptions;
import java.util.List;

public class EffectRemoveStaffParticle extends EffectHook {
	public List<ParticleOptions> particle;

	public String getIdentify() {
		return "REMOVE_STAFF_PARTICLE";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
	}

	public void execute(CEFunctionData data) {
		CEPlayer cePlayer = CEAPI.getCEPlayer(data.getPlayer());
		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();
		storage.set(TemporaryKey.STAFF_PARTICLE, null);
	}
}
