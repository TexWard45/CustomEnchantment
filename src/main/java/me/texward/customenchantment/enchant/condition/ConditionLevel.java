package me.texward.customenchantment.enchant.condition;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CompareOperation;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;

public class ConditionLevel extends ConditionHook {
	private CompareOperation operation;
	private double value;
	
	public String getIdentify() {
		return "LEVEL";
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
		
		return CompareOperation.compare(player.getLevel(), value, operation);
	}
	
}