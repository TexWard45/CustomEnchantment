package me.texward.customenchantment.enchant.condition;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;
import me.texward.texwardlib.api.FactionAPI;

public class ConditionHasNearbyEnemy extends ConditionHook {
	private double distance;

	public String getIdentify() {
		return "HAS_NEARBY_ENEMY";
	}

	public void setup(String[] args) {
		this.distance = Double.valueOf(args[0]);
	}

	@Override
	public boolean match(CEFunctionData data) {
		Player player = data.getPlayer();

		Location playerLocation = player.getLocation();
		World playerWorld = player.getWorld();

		boolean factionSupport = FactionAPI.isFactionSupport();
		
		for (Player target : Bukkit.getOnlinePlayers()) {
			if (target == player) {
				continue;
			}
			
			if (target.isOp()) {
				continue;
			}

			if (target.getWorld() != playerWorld) {
				continue;
			}

			double distance = target.getLocation().distance(playerLocation);
			if (distance > this.distance) {
				continue;
			}
			
			if (!factionSupport || !FactionAPI.isSameFaction(player, target)) {
				return true;
			}
		}

		return false;
	}

}
