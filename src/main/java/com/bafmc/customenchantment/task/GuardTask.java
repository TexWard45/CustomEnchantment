package com.bafmc.customenchantment.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.guard.GuardManager;
import com.bafmc.customenchantment.guard.PlayerGuard;

public class GuardTask extends BukkitRunnable {
	private CustomEnchantment plugin;
	private GuardManager guardManager;

	public GuardTask(CustomEnchantment plugin) {
		this.plugin = plugin;
		this.guardManager = plugin.getGuardManager();
	}

	public void run() {
		for (PlayerGuard playerGuard : guardManager.getPlayerGuards()) {
			playerGuard.tickGuards();
		}
	}
}