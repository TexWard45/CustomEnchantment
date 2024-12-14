package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.api.CompareOperation;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import org.bukkit.entity.Player;

public class ConditionWorldTime extends ConditionHook {
	private CompareOperation operation;
	private long value;
	
	public String getIdentify() {
		return "WORLD_TIME";
	}

	public void setup(String[] args) {
		this.operation = CompareOperation.getOperation(args[0]);
		this.value = Long.valueOf(args[1]);
	}

	@Override
	public boolean match(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return false;
		}
		
		return CompareOperation.compare(player.getWorld().getTime(), value, operation);
	}
	
}