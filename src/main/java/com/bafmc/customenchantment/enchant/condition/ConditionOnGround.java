package com.bafmc.customenchantment.enchant.condition;

import org.bukkit.entity.LivingEntity;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;

public class ConditionOnGround extends ConditionHook {

	public String getIdentify() {
		return "ON_GROUND";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		LivingEntity entity = data.getLivingEntity();
		if (entity == null) {
			return false;
		}

		return entity.isOnGround();
	}

}