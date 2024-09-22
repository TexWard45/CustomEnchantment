package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.bafframework.api.BafFrameworkAPI;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.bonus.EntityTypeBonus;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDeathListener implements Listener {
	public CustomEnchantment main;

	public MobDeathListener(CustomEnchantment main) {
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
	}

	@EventHandler
	public void onMobStackDeath(EntityDeathEvent e) {
		Player killer = e.getEntity().getKiller();
		if (killer != null) {
			CEPlayer cePlayer = CEAPI.getCEPlayer(killer);
			EntityType entityType = e.getEntityType();

			EntityTypeBonus xpBonus = cePlayer.getMobBonus().getExpBonus();
			if (xpBonus.isEmpty()) {
//				e.setDroppedExp(e.getDroppedExp() + (int) xpBonus.getBonus(entityType, 1));
			}

			EntityTypeBonus mBonus = cePlayer.getMobBonus().getMoneyBonus();
			if (mBonus.isEmpty()) {
				BafFrameworkAPI.giveMoneyLater(killer, mBonus.getBonus(e.getEntityType(), 1));
			}
		}
	}
}