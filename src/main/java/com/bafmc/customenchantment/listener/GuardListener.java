package com.bafmc.customenchantment.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.guard.Guard;
import com.bafmc.customenchantment.guard.GuardManager;
import com.bafmc.customenchantment.guard.PlayerGuard;

public class GuardListener implements Listener {
	private CustomEnchantment plugin;
	private GuardManager guardManager;

	public GuardListener(CustomEnchantment plugin) {
		this.plugin = plugin;
		this.guardManager = plugin.getGuardManager();

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public static boolean guardSpawning = false;
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityTarget(CreatureSpawnEvent e) {
		if (guardSpawning) {
			e.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityTarget(EntityTargetLivingEntityEvent e) {
		Entity entity = e.getEntity();

		Guard guard = guardManager.getGuard(entity);

		if (guard == null) {
			return;
		}

		if (!guard.isNowTarget()) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityTarget(EntityDeathEvent e) {
		Entity entity = e.getEntity();

		Guard guard = guardManager.getGuard(entity);

		if (guard == null) {
			return;
		}

		e.setDroppedExp(0);
		e.getDrops().clear();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTame(EntityTameEvent e) {
		Guard guard = guardManager.getGuard(e.getEntity());

		if (guard == null) {
			return;
		}
		
		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent e) {
		if (!e.getFrom().getWorld().equals(e.getTo().getWorld())) {
			PlayerGuard playerGuard = guardManager.getPlayerGuard(e.getPlayer());
			playerGuard.clearGuards();
		}
	}
}
