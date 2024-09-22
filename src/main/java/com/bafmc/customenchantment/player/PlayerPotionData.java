package com.bafmc.customenchantment.player;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerPotionData {
	private PotionEffect effect;
	private PotionEffectType type;
	private int amplifier;

	public PlayerPotionData(PotionEffectType type, int amplifier) {
		this.type = type;
		this.amplifier = amplifier;
		this.effect = new PotionEffect(type, Integer.MAX_VALUE, amplifier);
	}

	public PotionEffectType getType() {
		return type;
	}

	public void setType(PotionEffectType type) {
		this.type = type;
	}

	public int getAmplifier() {
		return amplifier;
	}

	public void setAmplifier(int amplifier) {
		this.amplifier = amplifier;
	}
	
	public PotionEffect getPotionEffect() {
		return effect;
	}
}
