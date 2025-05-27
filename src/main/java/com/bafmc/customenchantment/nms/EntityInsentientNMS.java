package com.bafmc.customenchantment.nms;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Path;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class EntityInsentientNMS {
	private Entity entity;
	private Mob entityInsentient;
	private double speed;

	public EntityInsentientNMS(Entity entity, double speed) {
		this.entity = entity;
		this.entityInsentient = ((Mob) ((CraftEntity) entity).getHandle());
		this.speed = speed;
	}

	public Entity getEntity() {
		return entity;
	}

	public Mob getEntityInsentient() {
		return entityInsentient;
	}

	// Need to fix this method
	public void moveTo(Location location) {
//		try {
//			Path path = entityInsentient.getNavigation().createPath(BlockPos.containing(location.getX(), location.getY(), location.getZ()), 0);
//			entityInsentient.getNavigation().moveTo(path, speed);
//		}catch (Exception e) {
//		}
	}

	public void setGoalTarget(EntityInsentientNMS entity) {
		entityInsentient.setTarget(entity.getEntityInsentient(), TargetReason.CUSTOM, false);
	}

	public void setGoalTarget(EntityLivingNMS entity) {
		entityInsentient.setTarget(entity.getEntityLiving(), TargetReason.CUSTOM, false);
	}

	public void setGoalTarget(Entity entity) {
		entityInsentient.setTarget(((LivingEntity) ((CraftEntity) entity).getHandle()));
	}
}
