package me.texward.customenchantment.enchant.effect;

import java.util.Map;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.CEPlaceholder;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.texwardlib.util.MessageUtils;

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
