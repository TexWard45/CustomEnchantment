package com.bafmc.customenchantment.enchant.condition;

import org.bukkit.entity.LivingEntity;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;

public class ConditionOnFire extends ConditionHook {

	public String getIdentify() {
		return "ON_FIRE";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		LivingEntity entity = data.getLivingEntity();
		if (entity == null) {
			return false;
		}

		return entity.getFireTicks() > 0;
	}

}