package me.texward.customenchantment.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.enchant.CECallerBuilder;
import me.texward.customenchantment.enchant.CEType;

public class CECallerTask extends BukkitRunnable {
	private CustomEnchantment plugin;

	public CECallerTask(CustomEnchantment plugin) {
		this.plugin = plugin;
	}

	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			CECallerBuilder
					.build(player)
					.setCEType(CEType.AUTO)
					.call();
		}
	}
}
