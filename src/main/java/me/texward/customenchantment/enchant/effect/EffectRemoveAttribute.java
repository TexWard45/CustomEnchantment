package me.texward.customenchantment.enchant.effect;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.enchant.EffectUtil;
import me.texward.customenchantment.player.CEPlayer;

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
