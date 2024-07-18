package me.texward.customenchantment.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.texwardlib.util.InventoryUtils;

public class McMMOListener implements Listener {
	private CustomEnchantment plugin;

	public McMMOListener(CustomEnchantment plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMcMMO(McMMOItemSpawnEvent e) {
		Player player = e.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		if (cePlayer.getSpecialMining().getTelepathySpecialMine().isWork(true)) {
			InventoryUtils.addItem(player, e.getItemStack());
			e.setCancelled(true);
		}
	}
}
