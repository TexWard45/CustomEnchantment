package com.bafmc.customenchantment.api;

import com.github.sirblobman.combatlogx.CombatPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CombatLogXAPI {
	public static boolean isCombatLogXSupport() {
		return Bukkit.getPluginManager().isPluginEnabled("CombatLogX");
	}

	public static boolean isInCombat(Player player) {
		CombatPlugin plugin = (CombatPlugin) Bukkit.getPluginManager().getPlugin("CombatLogX");
		return plugin != null && plugin.getCombatManager().isInCombat(player);
	}
}