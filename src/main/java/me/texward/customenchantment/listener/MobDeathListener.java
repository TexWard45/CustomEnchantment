package me.texward.customenchantment.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.CEAPI;
import me.texward.customenchantment.player.CEPlayer;
import me.texward.customenchantment.player.bonus.EntityTypeBonus;
import me.texward.texwardlib.api.EconomyAPI;

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
				EconomyAPI.giveMoneyLater(killer, mBonus.getBonus(e.getEntityType(), 1));
			}
		}
	}
}