package com.bafmc.customenchantment.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CECallerBuilder;
import com.bafmc.customenchantment.enchant.CEType;

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
