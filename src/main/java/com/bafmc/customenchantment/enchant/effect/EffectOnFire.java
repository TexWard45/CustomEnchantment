package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.LivingEntity;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.bukkit.utils.RandomRange;

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
