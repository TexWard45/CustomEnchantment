package me.texward.customenchantment.nms;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.PathEntity;

public class EntityInsentientNMS {
	private Entity entity;
	private EntityInsentient entityInsentient;
	private double speed;

	public EntityInsentientNMS(Entity entity, double speed) {
		this.entity = entity;
		this.entityInsentient = ((EntityInsentient) ((CraftEntity) entity).getHandle());
		this.speed = speed;
	}

	public Entity getEntity() {
		return entity;
	}

	public EntityInsentient getEntityInsentient() {
		return entityInsentient;
	}

	public void moveTo(Location location) {
		PathEntity path = entityInsentient.getNavigation().calculateDestination(location.getX(), location.getY(), location.getZ());
		entityInsentient.getNavigation().a(path, speed);
	}

	public void setGoalTarget(EntityInsentientNMS entity) {
		entityInsentient.setGoalTarget(entity.getEntityInsentient(), TargetReason.CUSTOM, false);
	}

	public void setGoalTarget(EntityLivingNMS entity) {
		entityInsentient.setGoalTarget(entity.getEntityLiving(), TargetReason.CUSTOM, false);
	}

	public void setGoalTarget(Entity entity) {
		entityInsentient.setGoalTarget(((EntityLiving) ((CraftEntity) entity).getHandle()));
	}
}
