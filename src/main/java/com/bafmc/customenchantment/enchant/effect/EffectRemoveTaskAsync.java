package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import org.bukkit.entity.Player;

public class EffectRemoveTaskAsync extends EffectHook {
	private String name;

	public String getIdentify() {
		return "REMOVE_TASK_ASYNC";
	}

	public void setup(String[] args) {
		this.name = args[0];
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		CustomEnchantment.instance().removeEffectTask(player.getName(), name);
	}
}
