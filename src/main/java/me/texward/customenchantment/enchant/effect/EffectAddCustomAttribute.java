package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.attribute.AttributeData;
import me.texward.customenchantment.attribute.AttributeData.Operation;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.texwardlib.util.RandomRange;

public class EffectAddCustomAttribute extends EffectHook {
	private String attributeName;
	private AttributeData attributeData;

	public String getIdentify() {
		return "ADD_CUSTOM_ATTRIBUTE";
	}

	public void setup(String[] args) {
		this.attributeName = args[0];
		this.attributeData = new AttributeData(args[1], new RandomRange(args[2]),
				Operation.fromId(args.length > 3 ? Integer.valueOf(args[3]) : 0));
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		cePlayer.getCustomAttribute().addCustomAttribute(attributeName, attributeData);
	}
}
