package com.bafmc.customenchantment.task;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CEPlayerTask extends BukkitRunnable {
	private CustomEnchantment plugin;

	public CEPlayerTask(CustomEnchantment plugin) {
		this.plugin = plugin;
	}

	public void run() {
		List<CEPlayer> list = CEAPI.getCEPlayers();

		for (CEPlayer cePlayer : list) {
			Player player = cePlayer.getPlayer();
			if (!player.isOnline()) {
				continue;
			}
			cePlayer.getPotion().updatePotion();
		}
	}
}