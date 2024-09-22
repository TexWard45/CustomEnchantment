package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;

public class EffectRemoveTask extends EffectHook {
	private String name;

	public String getIdentify() {
		return "REMOVE_TASK";
	}

	public boolean isAsync() {
		return false;
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
