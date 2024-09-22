package com.bafmc.customenchantment.guard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.nms.EntityLivingNMS;

public class PlayerGuard {
	private ConcurrentHashMap<UUID, Guard> guards = new ConcurrentHashMap<UUID, Guard>();
	private ConcurrentHashMap<String, Guard> guardByNameList = new ConcurrentHashMap<String, Guard>();
	private String name;
	private UUID uuid;
	private EntityLivingNMS lastTarget;
	private EntityLivingNMS lastEnemy;

	public PlayerGuard(Player player) {
		this.name = player.getName();
		this.uuid = player.getUniqueId();
	}

	public void addGuard(Guard guard) {
		if (guardByNameList.containsKey(guard.getName())) {
			return;
		}
		CustomEnchantment.instance().getGuardManager().addEntityGuard(guard);
		guards.put(guard.getEntityInsentient().getEntity().getUniqueId(), guard);
		guardByNameList.put(guard.getName(), guard);
	}

	public void removeGuard(Guard guard) {
		CustomEnchantment.instance().getGuardManager().removeEntityGuard(guard);
		guards.remove(guard.getEntityInsentient().getEntity().getUniqueId());
		guardByNameList.remove(guard.getName());
		guard.remove();
	}

	public void removeGuardByName(String name) {
		if (name.indexOf("*") != -1) {
			Iterator<String> ite = guardByNameList.keySet().iterator();
			
			int index = name.indexOf("*");
			String nameStartWith = name.substring(0, index);
			
			while(ite.hasNext()) {
				String guardName = ite.next();
				
				if (guardName.startsWith(nameStartWith)) {
					removeGuardByName(guardName);
				}
			}
		}else {
			Guard guard = getGuardByName(name);
	
			if (guard != null) {
				removeGuard(guard);
			}
		}
	}
	
	public void clearGuards() {
		for (Guard guard : getGuards()) {
			removeGuard(guard);
		}
	}

	public Guard getGuardByName(String name) {
		return guardByNameList.get(name);
	}

	public boolean containsGuardName(String name) {
		return guardByNameList.containsKey(name);
	}

	public void tickGuards() {
		for (Guard guard : guards.values()) {
			guard.tick();
		}
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public boolean isPlayerOnline() {
		return getPlayer() != null;
	}

	public Guard getGuard(Entity entity) {
		return guards.get(entity.getUniqueId());
	}

	public List<Guard> getGuards() {
		return new ArrayList<Guard>(guards.values());
	}

	public EntityLivingNMS getLastTarget() {
		return lastTarget;
	}

	public void setTarget(Entity entity) {
		this.lastTarget = new EntityLivingNMS(entity);
	}

	public EntityLivingNMS getLastEnemy() {
		return lastEnemy;
	}

	public void setLastEnemy(Entity entity) {
		this.lastEnemy = new EntityLivingNMS(entity);
	}

	public String getName() {
		return name;
	}
}
