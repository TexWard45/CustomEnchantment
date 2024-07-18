package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import me.texward.customenchantment.api.VectorFormat;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;

public class EffectFixedPull extends EffectHook {
	private String format;

	public String getIdentify() {
		return "FIXED_PULL";
	}

	public void setup(String[] args) {
		this.format = args[0];
	}

	public void execute(CEFunctionData data) {
		LivingEntity player = null;
		if (data.getLivingEntity() != null)
			player = data.getLivingEntity();

		Vector vector = new VectorFormat(this.format).getVector(data.getOldLocation(), data.getOldEnemyLocation());
		if (player != null && vector != null) {
			player.setVelocity(vector);
		}
	}
}
