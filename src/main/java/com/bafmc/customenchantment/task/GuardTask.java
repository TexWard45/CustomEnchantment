package com.bafmc.customenchantment.task;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.guard.GuardManager;
import com.bafmc.customenchantment.guard.PlayerGuard;
import org.bukkit.scheduler.BukkitRunnable;

public class GuardTask extends BukkitRunnable {
	private CustomEnchantment plugin;
	private GuardManager guardManager;

	public GuardTask(CustomEnchantment plugin, GuardManager guardManager) {
		this.plugin = plugin;
		this.guardManager = guardManager;
	}

	public void run() {
		for (PlayerGuard playerGuard : guardManager.getPlayerGuards()) {
			playerGuard.tickGuards();
		}
	}
}