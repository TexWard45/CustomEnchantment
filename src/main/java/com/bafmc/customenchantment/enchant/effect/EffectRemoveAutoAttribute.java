package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.enchant.EffectUtil;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.task.SlowResistanceTask;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class EffectRemoveAutoAttribute extends EffectHook {
	private Attribute attribute;
	private String name;
	private int index;

	public String getIdentify() {
		return "REMOVE_AUTO_ATTRIBUTE";
	}

    public boolean isForceEffectOnEnemyDead() {
        return true;
    }

	public void setup(String[] args) {
		this.attribute = EffectUtil.getAttributeType(args[0]);
		this.name = args[1];
		this.index = Integer.valueOf(args[2]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player == null) {
			return;
		}
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);

		String name = data.getNextPrefix(this.name, this.index);
		cePlayer.getVanillaAttribute().removeAttribute(attribute, name);

		if (attribute == Attribute.GENERIC_MOVEMENT_SPEED) {
			 SlowResistanceTask.update(player);
		}
	}

	public boolean isAsync() {
		return false;
	}
}
