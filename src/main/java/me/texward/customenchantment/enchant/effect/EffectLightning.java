package me.texward.customenchantment.enchant.effect;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import me.texward.customenchantment.api.LocationFormat;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.EffectHook;

public class EffectLightning extends EffectHook {
	private String format;

	public String getIdentify() {
		return "LIGHTNING";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.format = args[0];
	}

	public void execute(CEFunctionData data) {
		LivingEntity player = null;
		LivingEntity enemy = null;
		if (data.getLivingEntity() != null)
			player = data.getLivingEntity();
		if (data.getEnemyLivingEntity() != null)
			enemy = data.getEnemyLivingEntity();

		Location location = new LocationFormat(this.format).getLocation(player, enemy);
		if (location != null) {
			location.getWorld().strikeLightning(location);
		}
	}
}
