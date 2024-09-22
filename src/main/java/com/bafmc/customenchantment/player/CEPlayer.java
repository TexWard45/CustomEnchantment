package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.bukkit.utils.EquipSlot;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CEPlayer implements ICEPlayerEvent {
	private ConcurrentHashMap<Class<? extends CEPlayerExpansion>, CEPlayerExpansion> map = new ConcurrentHashMap<Class<? extends CEPlayerExpansion>, CEPlayerExpansion>();
	private ConcurrentHashMap<EquipSlot, CEWeaponAbstract> slotMap = new ConcurrentHashMap<EquipSlot, CEWeaponAbstract>();
	private Player player;
	private boolean register;
	private boolean adminMode;
	private boolean debugMode;
	private int deathTime;
	private boolean deathTimeBefore;
	private boolean fullChance;

	public CEPlayer(Player player) {
		this.player = player;
		CEPlayerExpansionRegister.setup(this);
	}

	/**
	 * Setup data when player join
	 * 
	 */
	public void onJoin() {
		for (CEPlayerExpansion expansion : map.values()) {
			expansion.onJoin();
		}
		this.register();
		this.updateSlot();
	}

	/**
	 * Setup data when player quit
	 * 
	 */
	public void onQuit() {
		for (CEPlayerExpansion expansion : map.values()) {
			expansion.onQuit();
		}
		this.unregister();
		this.slotMap.clear();
	}

	/**
	 * Register player
	 * 
	 */
	public void register() {
		CustomEnchantment.instance().getCEPlayerMap().registerCEPlayer(this);
		this.register = true;
	}

	/**
	 * Unregister player
	 * 
	 */
	public void unregister() {
		CustomEnchantment.instance().getCEPlayerMap().unregisterCEPlayer(this);
		this.register = false;
	}

	/**
	 * Check if player is online
	 * 
	 * @return true if online, false if not
	 */
	public boolean isOnline() {
		return player.isOnline();
	}

	/**
	 * Check if player is registered
	 * 
	 * @return true if register, false if not
	 */
	public boolean isRegister() {
		return register;
	}

	public Player getPlayer() {
		return player;
	}

	public void addExpantion(CEPlayerExpansion expansion) {
		map.put(expansion.getClass(), expansion);
	}

	public void removeExpansion(Class<? extends CEPlayerExpansion> clazz) {
		map.remove(clazz);
	}

	public CEPlayerExpansion getExpansion(Class<? extends CEPlayerExpansion> clazz) {
		return map.get(clazz);
	}

	public PlayerAbility getAbility() {
		return (PlayerAbility) getExpansion(PlayerAbility.class);
	}

	public PlayerCECooldown getCECooldown() {
		return (PlayerCECooldown) getExpansion(PlayerCECooldown.class);
	}

	public PlayerCEManager getCEManager() {
		return (PlayerCEManager) getExpansion(PlayerCEManager.class);
	}

	public PlayerCustomAttribute getCustomAttribute() {
		return (PlayerCustomAttribute) getExpansion(PlayerCustomAttribute.class);
	}

	public PlayerVanillaAttribute getVanillaAttribute() {
		return (PlayerVanillaAttribute) getExpansion(PlayerVanillaAttribute.class);
	}

	public PlayerPotion getPotion() {
		return (PlayerPotion) getExpansion(PlayerPotion.class);
	}

    public PlayerSet getSet() {
        return (PlayerSet) getExpansion(PlayerSet.class);
    }

	public PlayerStorage getStorage() {
		return (PlayerStorage) getExpansion(PlayerStorage.class);
	}

	public PlayerTemporaryStorage getTemporaryStorage() {
		return (PlayerTemporaryStorage) getExpansion(PlayerTemporaryStorage.class);
	}

	public PlayerMobBonus getMobBonus() {
		return (PlayerMobBonus) getExpansion(PlayerMobBonus.class);
	}

	public PlayerBlockBonus getBlockBonus() {
		return (PlayerBlockBonus) getExpansion(PlayerBlockBonus.class);
	}

	public PlayerSpecialMining getSpecialMining() {
		return (PlayerSpecialMining) getExpansion(PlayerSpecialMining.class);
	}
	
	public PlayerNameTag getNameTag() {
		return (PlayerNameTag) getExpansion(PlayerNameTag.class);
	}

	public boolean isAdminMode() {
		return adminMode;
	}

	public void setAdminMode(boolean adminMode) {
		this.adminMode = adminMode;
	}
	
	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public boolean isFullChance() {
		return fullChance;
	}

	public void setFullChance(boolean fullChance) {
		this.fullChance = fullChance;
	}

	public int getDeathTime() {
		return deathTime;
	}

	public void setDeathTime(int deathTime) {
		this.deathTime = deathTime;
	}

	public boolean isDeathTimeBefore() {
		return deathTimeBefore;
	}

	public void setDeathTimeBefore(boolean deathTimeBefore) {
		this.deathTimeBefore = deathTimeBefore;
	}

	public void updateSlot() {
		for (EquipSlot slot : EquipSlot.ALL_ARRAY) {
			CEWeaponAbstract weapon = CEWeapon.getCEWeapon(slot.getItemStack(player));
			if (weapon == null) {
				continue;
			}
			setSlot(slot, weapon);
		}
	}

	public CEWeaponAbstract getSlot(EquipSlot slot) {
		return slotMap.get(slot);
	}

	public void setSlot(EquipSlot slot, CEWeaponAbstract weapon) {
		if (weapon != null) {
			slotMap.put(slot, weapon);
		} else {
			slotMap.remove(slot);
		}

        getSet().onUpdate();
	}

    public Map<EquipSlot, CEWeaponAbstract> getSlotMap() {
        return new LinkedHashMap<>(slotMap);
    }
}
