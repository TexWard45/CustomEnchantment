package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customfarm.ore.event.OreBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomFarmListener implements Listener {
	private CustomEnchantment plugin;

	public CustomFarmListener(CustomEnchantment plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onOreBreak(OreBreakEvent e) {
		Player player = e.getPlayer();
		if (player == null) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		double miningPower = cePlayer.getCustomAttribute().getValue(CustomAttributeType.MINING_POWER);

		if (miningPower <= 0) {
			e.setCancelled(true);
			e.setCancelledMessages(CustomEnchantmentMessage.getMessageConfig("ore.no-mining-power"));
		}

		e.setDamage(miningPower);
	}
}
