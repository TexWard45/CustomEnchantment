package com.bafmc.customenchantment.nms;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class EntityLivingNMS {
	private Entity entity;
	private LivingEntity entityLiving;

	public EntityLivingNMS(Entity entity) {
		this.entity = entity;
		this.entityLiving = ((LivingEntity) ((CraftEntity) entity).getHandle());
	}

	public Entity getEntity() {
		return entity;
	}

	public LivingEntity getEntityLiving() {
		return entityLiving;
	}
}
