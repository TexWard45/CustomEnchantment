package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.CEPlayerMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;

public class CEPlayer implements ICEPlayerEvent {
	private static CEPlayerMap cePlayerMap;
	private Map<Class<? extends CEPlayerExpansion>, CEPlayerExpansion> map = new LinkedHashMap<>();
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

	public static CEPlayerMap getCePlayerMap() {
		if (cePlayerMap == null) {
			cePlayerMap = new CEPlayerMap();
		}

		return cePlayerMap;
	}

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
	}

	/**
	 * Setup data when player quit
	 * 
	 */
	public void onQuit() {
		List<CEPlayerExpansion> expansions = new ArrayList<>(map.values());
		Collections.reverse(expansions);
		for (CEPlayerExpansion expansion : expansions) {
			expansion.onQuit();
		}
		this.unregister();
	}

	/**
	 * Register player
	 * 
	 */
	public void register() {
		getCePlayerMap().registerCEPlayer(this);
		this.register = true;
	}

	/**
	 * Unregister player
	 * 
	 */
	public void unregister() {
		getCePlayerMap().unregisterCEPlayer(this);
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

    public void addExpansion(CEPlayerExpansion expansion) {
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

	public PlayerExtraSlot getArtifact() {
		return (PlayerExtraSlot) getExpansion(PlayerExtraSlot.class);
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

	public PlayerGem getGem() {
		return (PlayerGem) getExpansion(PlayerGem.class);
	}

	public PlayerEquipment getEquipment() {
		return (PlayerEquipment) getExpansion(PlayerEquipment.class);
	}
}
