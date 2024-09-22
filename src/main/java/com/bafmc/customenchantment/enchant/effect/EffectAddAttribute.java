package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.EffectUtil;
import com.bafmc.customenchantment.player.CEPlayer;

public class EffectAddAttribute extends EffectHook {
	private Attribute attribute;
	private String name;
	private double amount;
	private Operation operation;

	public String getIdentify() {
		return "ADD_ATTRIBUTE";
	}

	public void setup(String[] args) {
		this.attribute = EffectUtil.getAttributeType(args[0]);
		this.name = args[1];
		this.amount = Double.valueOf(args[2]);
		this.operation = EffectUtil.getByNumber(Integer.valueOf(args[3]));
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		cePlayer.getVanillaAttribute().addAttribute(attribute, name, amount, operation);
	}
	
	
}
