package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.LivingEntity;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.texwardlib.util.RandomRange;

public class EffectOnFire extends EffectHook {
	private RandomRange tick;

	public String getIdentify() {
		return "ON_FIRE";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.tick = new RandomRange(args[0]);
	}

	public void execute(CEFunctionData data) {
		LivingEntity livingEntity = data.getLivingEntity();
		if (livingEntity == null) {
			return;
		}

		livingEntity.setFireTicks(tick.getIntValue());
	}
}
