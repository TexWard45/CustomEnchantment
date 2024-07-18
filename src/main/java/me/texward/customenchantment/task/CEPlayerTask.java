package me.texward.customenchantment.task;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.player.CEPlayer;

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