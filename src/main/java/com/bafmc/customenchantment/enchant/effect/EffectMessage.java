package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.bukkit.utils.MessageUtils;

public class EffectMessage extends EffectHook {
	private String message;

	public String getIdentify() {
		return "MESSAGE";
	}

	public void setup(String[] args) {
		this.message = get(args, 0);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		String message = CEPlaceholder.setPlaceholder(this.message,
				CEPlaceholder.getCEFunctionDataPlaceholder(this.message, data));
		MessageUtils.send(player, message);
	}
}
