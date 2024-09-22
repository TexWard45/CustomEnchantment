package com.bafmc.customenchantment.guard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GuardManager {
	private ConcurrentHashMap<UUID, Guard> guardMap = new ConcurrentHashMap<UUID, Guard>();
	private ConcurrentHashMap<UUID, PlayerGuard> playerGuardMap = new ConcurrentHashMap<UUID, PlayerGuard>();

	public PlayerGuard getPlayerGuard(Player player) {
		PlayerGuard playerGuard = playerGuardMap.get(player.getUniqueId());

		if (playerGuard == null) {
			playerGuard = new PlayerGuard(player);
			playerGuardMap.put(player.getUniqueId(), playerGuard);
		}

		return playerGuard;
	}
	
	public void removePlayerGuard(Player player) {
		playerGuardMap.remove(player.getUniqueId());
	}

	public boolean isGuard(Entity entity) {
		return guardMap.containsKey(entity.getUniqueId());
	}

	public Guard getGuard(Entity entity) {
		return guardMap.get(entity.getUniqueId());
	}

	public void addEntityGuard(Guard guard) {
		guardMap.put(guard.getEntityInsentient().getEntity().getUniqueId(), guard);
	}

	public void removeEntityGuard(Guard guard) {
		guardMap.remove(guard.getEntityInsentient().getEntity().getUniqueId());
	}

	public List<PlayerGuard> getPlayerGuards() {
		return new ArrayList<PlayerGuard>(playerGuardMap.values());
	}

	public List<Guard> getGuards() {
		return new ArrayList<Guard>(guardMap.values());
	}
}
