package me.texward.customenchantment.enchant.condition;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;
import me.texward.texwardlib.api.FactionAPI;

public class ConditionCanAttack extends ConditionHook {
	public String getIdentify() {
		return "CAN_ATTACK";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		Player player = data.getPlayer();
		Player enemy = data.getEnemyPlayer();

		return canAttack(player, enemy);
	}

	public boolean canAttack(Player player, Player enemy) {
		return canAttackFaction(player, enemy) && canAttackGamemode(player, enemy) && canAttackOp(player, enemy);
	}

	public boolean canAttackFaction(Player player, Player enemy) {
		return FactionAPI.isFactionSupport() && FactionAPI.canDamage(player, enemy, false);
	}
	
	public boolean canAttackGamemode(Player player, Player enemy) {
		return enemy != null && enemy.getGameMode() == GameMode.SURVIVAL;
	}
	
	public boolean canAttackOp(Player player, Player enemy) {
		return enemy != null && !enemy.isOp();
	}
}