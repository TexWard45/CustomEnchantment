package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.EffectUtil;
import com.bafmc.customenchantment.player.CEPlayer;

public class EffectRemoveAttribute extends EffectHook {
	private Attribute attribute;
	private String name;

	public String getIdentify() {
		return "REMOVE_ATTRIBUTE";
	}

    public boolean isForceEffectOnEnemyDead() {
        return true;
    }

	public void setup(String[] args) {
		this.attribute = EffectUtil.getAttributeType(args[0]);
		this.name = args[1];
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		cePlayer.getVanillaAttribute().removeAttribute(attribute, name);
	}
}
