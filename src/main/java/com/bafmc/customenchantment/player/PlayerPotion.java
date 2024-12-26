package com.bafmc.customenchantment.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Handle potion with unlimited duration
 * 
 * @author nhata
 *
 */
public class PlayerPotion extends CEPlayerExpansion {
	private ConcurrentHashMap<String, PlayerPotionData> potionTypeActiveMap = new ConcurrentHashMap<String, PlayerPotionData>();
	private ConcurrentHashMap<String, PotionEffectType> potionTypeBlockMap = new ConcurrentHashMap<String, PotionEffectType>();
	private ConcurrentHashMap<PotionEffectType, Integer> potionTypeAmplifierMap = new ConcurrentHashMap<PotionEffectType, Integer>();

	public PlayerPotion(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {
		clearAllPotion();
	}

	public void onQuit() {
		clearAllPotion();
	}

	/**
	 * Update potion for player by active and block map.
	 * 
	 */
	public void updatePotion() {
		List<PotionEffectType> blockList = new ArrayList<PotionEffectType>();
		Iterator<PotionEffectType> blockIterator = potionTypeBlockMap.values().iterator();
		while (blockIterator.hasNext()) {
			PotionEffectType type = blockIterator.next();

			if (player.hasPotionEffect(type)) {
				player.removePotionEffect(type);
			}
			blockList.add(type);
		}

		Iterator<PlayerPotionData> activeIterator = potionTypeActiveMap.values().iterator();
		while (activeIterator.hasNext()) {
			PlayerPotionData data = activeIterator.next();
			PotionEffectType type = data.getType();
			if (blockList.contains(type)) {
				continue;
			}

			int amplifier = getHighestAmplifierByPotionType(type);

			if (amplifier < 0) {
				continue;
			}

			if (player.hasPotionEffect(type)) {
				PotionEffect currentPotion = player.getPotionEffect(type);
				int currentAmplifier = currentPotion.getAmplifier();

				if (data.getPotionEffect().getAmplifier() <= currentAmplifier || currentAmplifier >= amplifier) {
					continue;
				}
			}

			player.addPotionEffect(data.getPotionEffect(), true);
		}
	}

	/**
	 * Clear potion on player.
	 */
	public void clearAllPotion() {
		Iterator<PlayerPotionData> activeIterator = potionTypeActiveMap.values().iterator();
		while (activeIterator.hasNext()) {
			PlayerPotionData data = activeIterator.next();
			player.removePotionEffect(data.getType());
		}
	}

	/**
	 * Add potion by name and amplifier.
	 * 
	 * @param name      name of potion
	 * @param type      type of potion
	 * @param amplifier level of potion
	 */
	public void addPotionType(String name, PotionEffectType type, int amplifier) {
		if (amplifier < 0) {
			removePotionType(name);
			return;
		}

		PlayerPotionData data = new PlayerPotionData(type, amplifier);

		player.addPotionEffect(data.getPotionEffect());
		potionTypeActiveMap.put(name, data);

		updateHighestPriority(type);
	}

	/**
	 * Remove potion by name.
	 * 
	 * @param name name of potion
	 */
	public void removePotionType(String name) {
		if (!potionTypeActiveMap.containsKey(name)) {
			return;
		}

		PlayerPotionData data = potionTypeActiveMap.get(name);
		PotionEffectType type = data.getType();
		potionTypeActiveMap.remove(name);

		updateHighestPriority(type);

		int highestAmplifier = potionTypeAmplifierMap.containsKey(type) ? potionTypeAmplifierMap.get(type) : -1;
		if (highestAmplifier == -1) {
			player.removePotionEffect(type);
		} else {
			player.addPotionEffect(new PotionEffect(type, PotionEffect.INFINITE_DURATION, highestAmplifier), true);
		}
	}

	/**
	 * Update highest priority for specific type of potion
	 * 
	 * @param type type of potion
	 */
	public void updateHighestPriority(PotionEffectType type) {
		int highestAmplifier = -1;

		Iterator<PlayerPotionData> activeIterator = potionTypeActiveMap.values().iterator();
		while (activeIterator.hasNext()) {
			PlayerPotionData data = activeIterator.next();
			if (data.getType() != type) {
				continue;
			}
			
			int amplifier = data.getAmplifier();

			if (amplifier > highestAmplifier) {
				highestAmplifier = amplifier;
			}
		}

		if (highestAmplifier == -1) {
			potionTypeAmplifierMap.remove(type);
		} else {
			potionTypeAmplifierMap.put(type, highestAmplifier);
		}
	}

	/**
	 * Block potion by name and type.
	 * 
	 * @param name name of potion
	 * @param type type of potion
	 */
	public void blockPotionType(String name, PotionEffectType type) {
		player.removePotionEffect(type);
		potionTypeBlockMap.put(name, type);
	}

	/**
	 * Unblock potion by name.
	 * 
	 * @param name name of potion
	 */
	public void unblockPotionType(String name) {
		potionTypeBlockMap.remove(name);
	}

	/**
	 * Get highest amlifier by type of potion.
	 * 
	 * @param potionEffectType
	 * @return highest amplifier (-1 if not found)
	 */
	public int getHighestAmplifierByPotionType(PotionEffectType potionEffectType) {
		if (!potionTypeAmplifierMap.containsKey(potionEffectType)) {
			return -1;
		}
		return potionTypeAmplifierMap.get(potionEffectType);
	}
}
