package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.constant.CEConstants;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;
import com.bafmc.customenchantment.guard.PlayerGuard;
import org.bukkit.entity.Player;

import java.util.List;

public class EffectRemoveGuard extends EffectHook {
	private List<String> nameList;

	public String getIdentify() {
		return "REMOVE_GUARD";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.nameList = StringUtils.split(args[0], ",", 0);
	}

	public void execute(CEFunctionData data) {
		Player player = data.getPlayer();

		if (player == null) {
			return;
		}

		PlayerGuard playerGuard = CustomEnchantment.instance().getGuardModule().getGuardManager().getPlayerGuard(player);

		for (String name : nameList) {
			String guardName = name.replace(CEConstants.Placeholder.PLAYER, player.getName());
			playerGuard.removeGuardByName(guardName);
		}
	}
}
