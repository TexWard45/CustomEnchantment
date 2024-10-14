package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.utils.RandomRange;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.attribute.AttributeData;
import com.bafmc.customenchantment.attribute.AttributeData.Operation;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;

public class EffectAddCustomAttribute extends EffectHook {
	private String attributeName;
	private AttributeData attributeData;

	public String getIdentify() {
		return "ADD_CUSTOM_ATTRIBUTE";
	}

	public void setup(String[] args) {
		this.attributeName = args[0];
		this.attributeData = new AttributeData(CustomAttributeType.valueOf(args[1]), new RandomRange(args[2]),
				Operation.fromId(args.length > 3 ? Integer.parseInt(args[3]) : 0));
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
