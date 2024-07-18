package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Player;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;

public class EffectSetFlight extends EffectHook {
	private boolean allow;

	public String getIdentify() {
		return "SET_FLIGHT";
	}

	public boolean isAsync() {
		return false;
	}
	
	public void setup(String[] args) {
		this.allow = Boolean.valueOf(args[0]);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();
		if (player.getAllowFlight() != allow) {
			player.setAllowFlight(allow);
		}
	}
}
