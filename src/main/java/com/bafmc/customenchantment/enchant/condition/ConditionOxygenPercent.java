package com.bafmc.customenchantment.enchant.condition;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CompareOperation;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;

public class ConditionOxygenPercent extends ConditionHook {
	private CompareOperation operation;
	private double value;
	
	public String getIdentify() {
		return "OXYGEN_PERCENT";
	}

	public void setup(String[] args) {
		this.operation = CompareOperation.getOperation(args[0]);
		this.value = Double.valueOf(args[1]);
	}

	@Override
	public boolean match(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return false;
		}
		
		return CompareOperation.compare(player.getFoodLevel() / 20d * 100d, value, operation);
	}
	
}