package com.bafmc.customenchantment.enchant.effect;

import java.util.Map;

import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.CEPlaceholder;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.bukkit.utils.MessageUtils;

public class EffectAdvancedMessage extends EffectHook {
	private String message;

	public String getIdentify() {
		return "ADVANCED_MESSAGE";
	}

	public void setup(String[] args) {
		this.message = get(args, 0);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}

		Map<String, String> placeholder = CEPlaceholder.getCEFunctionDataPlaceholder(this.message, data);
		placeholder.putAll(CEPlaceholder.getTemporaryStoragePlaceholder(CEAPI.getCEPlayer(player).getTemporaryStorage()));

		String message = CEPlaceholder.setPlaceholder(this.message, placeholder);
		MessageUtils.send(player, message);
	}
}
