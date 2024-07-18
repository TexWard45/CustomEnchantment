package me.texward.customenchantment.enchant.effect;

import java.util.List;

import org.bukkit.entity.Player;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;
import me.texward.customenchantment.guard.PlayerGuard;
import me.texward.texwardlib.util.StringUtils;

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

		PlayerGuard playerGuard = CustomEnchantment.instance().getGuardManager().getPlayerGuard(player);

		for (String name : nameList) {
			String guardName = name.replace("%player%", player.getName());
			playerGuard.removeGuardByName(guardName);
		}
	}
}
