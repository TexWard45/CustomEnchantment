package me.texward.customenchantment.nms;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import net.minecraft.server.v1_16_R3.EntityLiving;

public class EntityLivingNMS {
	private Entity entity;
	private EntityLiving entityLiving;

	public EntityLivingNMS(Entity entity) {
		this.entity = entity;
		this.entityLiving = ((EntityLiving) ((CraftEntity) entity).getHandle());
	}

	public Entity getEntity() {
		return entity;
	}

	public EntityLiving getEntityLiving() {
		return entityLiving;
	}
}
