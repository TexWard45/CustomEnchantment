package com.bafmc.customenchantment.enchant.effect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.LocationFormat;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.EffectHook;

public class EffectSetBlock extends EffectHook {
	private Material material;
	private long duration;
	private String locationFormat;

	public String getIdentify() {
		return "SET_BLOCK";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.material = Material.valueOf(args[0]);
		this.duration = Long.parseLong(args[1]);
		this.locationFormat = args[2];
	}

	public void execute(CEFunctionData data) {
		LivingEntity player = null;
		LivingEntity enemy = null;
		if (data.getLivingEntity() != null)
			player = data.getLivingEntity();
		if (data.getEnemyLivingEntity() != null)
			enemy = data.getEnemyLivingEntity();

		LocationFormat locationFormat = new LocationFormat(this.locationFormat);
		Location location = locationFormat.getLocation(player, enemy);

		CustomEnchantment.instance().getBlockTask().setBlock(location, material, duration);
	}
}
