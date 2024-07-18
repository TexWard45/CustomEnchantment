package me.texward.customenchantment.task;

import org.bukkit.scheduler.BukkitRunnable;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.guard.GuardManager;
import me.texward.customenchantment.guard.PlayerGuard;

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