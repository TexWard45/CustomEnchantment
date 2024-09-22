package com.bafmc.customenchantment.task;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.player.CEPlayer;

public class CEPlayerTask extends BukkitRunnable {
	private CustomEnchantment plugin;

	public CEPlayerTask(CustomEnchantment plugin) {
		this.plugin = plugin;
	}

	public void run() {
		List<CEPlayer> list = plugin.getCEPlayerMap().getCEPlayers();

		for (CEPlayer cePlayer : list) {
			Player player = cePlayer.getPlayer();
			if (!player.isOnline()) {
				continue;
			}
			cePlayer.getPotion().updatePotion();
		}
	}
}