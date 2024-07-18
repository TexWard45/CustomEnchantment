package me.texward.customenchantment.enchant.effect;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import me.texward.customenchantment.enchant.CEFunctionData;

public class EffectSummonBabyZombieGuard extends EffectSummonGuard {

	public String getIdentify() {
		return "SUMMON_ZOMBIE_BABY_GUARD";
	}

	public boolean isAsync() {
		return false;
	}

	public void setup(String[] args) {
		this.name = args[0];
		this.entityType = EntityType.ZOMBIE;
		this.locationFormat = args[1];
		this.speed = Double.parseDouble(args[2]);
		this.playerRange = Double.parseDouble(args[3]);
		this.attackRange = Double.parseDouble(args[4]);
		this.aliveTime = Long.parseLong(args[5]);
	}

	public void execute(CEFunctionData data) {
		Entity entity = summon(data);

		if (entity instanceof Zombie) {
			((Zombie) entity).setBaby(true);
		}
	}
}
