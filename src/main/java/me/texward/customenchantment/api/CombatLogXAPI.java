package me.texward.customenchantment.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.CombatLogX;

public class CombatLogXAPI {
	public static boolean isCombatLogXSupport() {
		return Bukkit.getPluginManager().isPluginEnabled("CombatLogX");
	}

	public static boolean isInCombat(Player player) {
		CombatLogX plugin = (CombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
		return plugin != null && plugin.getCombatManager().isInCombat(player);
	}
}