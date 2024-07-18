package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.CEPlaceholder;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.texwardlib.util.MessageUtils;

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
