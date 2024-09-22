package com.bafmc.customenchantment.enchant.condition;

import org.bukkit.entity.LivingEntity;

import com.bafmc.customenchantment.api.CompareOperation;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;

public class ConditionHealth extends ConditionHook {
	private CompareOperation operation;
	private double value;

	public String getIdentify() {
		return "HEALTH";
	}

	public void setup(String[] args) {
		this.operation = CompareOperation.getOperation(args[0]);
		this.value = Double.valueOf(args[1]);
	}

	@Override
	public boolean match(CEFunctionData data) {
		LivingEntity livingEntity = data.getLivingEntity();
		if (livingEntity == null) {
			return false;
		}

		return CompareOperation.compare(livingEntity.getHealth(), value, operation);
	}

}