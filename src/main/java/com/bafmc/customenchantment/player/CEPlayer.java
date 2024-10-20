package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CEPlayer implements ICEPlayerEvent {
	private ConcurrentHashMap<Class<? extends CEPlayerExpansion>, CEPlayerExpansion> map = new ConcurrentHashMap<Class<? extends CEPlayerExpansion>, CEPlayerExpansion>();
	private ConcurrentHashMap<EquipSlot, CEWeaponAbstract> slotMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<EquipSlot, Boolean> disableSlotMap = new ConcurrentHashMap<>();
	@Getter
    private Player player;
    @Getter
    private boolean register;
	@Setter
    @Getter
    private boolean adminMode;
	@Getter
    @Setter
    private boolean debugMode;
	@Setter
    @Getter
    private int deathTime;
	@Setter
    @Getter
    private boolean deathTimeBefore;
	@Setter
    @Getter
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
		return getSlot(slot, true);
	}

	public CEWeaponAbstract getSlot(EquipSlot slot, boolean check) {
		if (!slotMap.containsKey(slot)) {
			return null;
		}
		if (check && disableSlotMap.containsKey(slot)) {
			return null;
		}
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

	public void setDisableSlot(EquipSlot slot, boolean disable) {
		if (disable) {
			disableSlotMap.put(slot, true);
		} else {
			disableSlotMap.remove(slot);
		}
	}

	public boolean isDisableSlot(EquipSlot slot) {
		return disableSlotMap.containsKey(slot);
	}

    public Map<EquipSlot, CEWeaponAbstract> getSlotMap() {
		return getSlotMap(true);
    }

	public Map<EquipSlot, CEWeaponAbstract> getSlotMap(boolean check) {
		if (!check) {
			return new LinkedHashMap<>(slotMap);
		}
		Map<EquipSlot, CEWeaponAbstract> map = new LinkedHashMap<>();
		for (EquipSlot slot : slotMap.keySet()) {
			if (disableSlotMap.containsKey(slot)) {
				continue;
			}
			map.put(slot, slotMap.get(slot));
		}
		return map;
	}
}
