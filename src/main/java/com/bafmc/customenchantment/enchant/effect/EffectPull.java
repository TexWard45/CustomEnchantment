package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.bafmc.customenchantment.api.VectorFormat;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;

public class EffectPull extends EffectHook {
	private String format;

	public String getIdentify() {
		return "PULL";
	}

	public void setup(String[] args) {
		this.format = args[0];
	}

	public void execute(CEFunctionData data) {
		LivingEntity player = null;
		LivingEntity enemy = null;
		if (data.getLivingEntity() != null)
			player = data.getLivingEntity();
		if (data.getEnemyLivingEntity() != null)
			enemy = data.getEnemyLivingEntity();

		Vector vector = new VectorFormat(this.format).getVector(player, enemy);
		if (player != null && vector != null) {
			player.setVelocity(vector);
		}
	}
}
