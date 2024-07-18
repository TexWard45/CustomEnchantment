package me.texward.customenchantment.enchant.condition;

import org.bukkit.entity.Entity;

import me.texward.customenchantment.api.LocationFormat;
import me.texward.customenchantment.enchant.CEFunctionData;
import me.texward.customenchantment.enchant.ConditionHook;

public class ConditionOutOfSight extends ConditionHook {
	public String getIdentify() {
		return "OUT_OF_SIGHT";
	}

	public void setup(String[] args) {
	}

	@Override
	public boolean match(CEFunctionData data) {
		Entity entity = data.getLivingEntity();
		Entity enemy = data.getEnemyLivingEntity();

		if (entity == null || enemy == null) {
			return false;
		}

		double pYaw = LocationFormat.getExactYaw(entity.getLocation().getYaw());
		double eYaw = LocationFormat.getExactYaw(enemy.getLocation().getYaw());

		double degree = Math.abs(pYaw - eYaw);

		// 127 degree on sight, 106 degree both side
		return degree <= 141.5;
	}
}