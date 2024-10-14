package com.bafmc.customenchantment.guard;

import com.bafmc.bukkit.bafframework.utils.EntityUtils;
import com.bafmc.bukkit.utils.LocationUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.listener.GuardListener;
import com.bafmc.customenchantment.nms.EntityInsentientNMS;
import com.bafmc.customenchantment.nms.EntityLivingNMS;
import com.bafmc.custommobdrop.CustomMobDropTag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

public class Guard {
	private PlayerGuard playerGuard;
	private String name;
	private String team;
	private EntityInsentientNMS entityInsentient;
	private EntityType entityType;
	private double playerRange;
	private double attackRange;
	private long spawnTime;
	private long aliveTime;
	private boolean nowTarget;
	private EntityLivingNMS lastEnemy;
	private double damage;
	private boolean suicide;

	public Guard(PlayerGuard playerGuard, String name, String team, EntityType entityType, double playerRange,
			double attackRange, long aliveTime) {
		this.playerGuard = playerGuard;
		this.name = name;
		this.team = team;
		this.entityType = entityType;
		this.playerRange = playerRange;
		this.attackRange = attackRange;
		this.aliveTime = aliveTime;
	}

	public Entity summon(Location location, double speed) {
		GuardListener.guardSpawning = true;
		Entity entity = location.getWorld().spawnEntity(location, entityType);
		GuardListener.guardSpawning = false;

		if (Bukkit.getPluginManager().isPluginEnabled("CustomMobDrop")) {
			entity.setMetadata(CustomMobDropTag.DISABLE, new FixedMetadataValue(CustomEnchantment.instance(), true));
		}

		entity.setCustomName(playerGuard.getName() + "'s " + EntityUtils.getDisplayName(entity.getType()));
		entity.setCustomNameVisible(true);

		this.entityInsentient = new EntityInsentientNMS(entity, speed);
		this.spawnTime = System.currentTimeMillis();
		return entity;
	}

	public void remove() {
		if (entityInsentient.getEntity().isDead()) {
			return;
		}
		entityInsentient.getEntity().remove();
	}

	public void tick() {
		if (!tickAlive()) {
			return;
		}

		if (!tickAttack()) {
			tickMove();
		}
	}

	public boolean tickAlive() {
		Entity entity = getEntityInsentient().getEntity();
		if (entity.isDead()) {
			playerGuard.removeGuard(this);
			return false;
		}

		if (!playerGuard.isPlayerOnline() || playerGuard.getPlayer().isDead() || isExpireAliveTime()) {
			entity.remove();
			playerGuard.removeGuard(this);
			return false;
		}
		return true;
	}

	public boolean tickMove() {
		Location entityLocation = entityInsentient.getEntity().getLocation();
		Location playerLocation = playerGuard.getPlayer().getLocation();
		if (LocationUtils.distance(entityLocation, playerLocation) > 2) {
			entityInsentient.moveTo(playerGuard.getPlayer().getLocation());
			return true;
		}
		return false;
	}

	public boolean tickAttack() {
		Location entityLocation = entityInsentient.getEntity().getLocation();
		Location playerLocation = playerGuard.getPlayer().getLocation();
		if (LocationUtils.distance(entityLocation, playerLocation) > playerRange) {
			entityInsentient.getEntity().teleport(playerLocation);
			return false;
		}

		if (attack(getLastEnemy())) {
			return true;
		}

		if (attack(playerGuard.getLastTarget())) {
			return true;
		}

		if (attack(playerGuard.getLastEnemy())) {
			return true;
		}
		return false;
	}

	public boolean attack(EntityLivingNMS enemyInsentientNMS) {
		if (enemyInsentientNMS != null && canAttack(enemyInsentientNMS)) {
			Location entityLocation = entityInsentient.getEntity().getLocation();
			Location playerLocation = playerGuard.getPlayer().getLocation();
			Location enemyLocation = enemyInsentientNMS.getEntity().getLocation();
			
			setNowTarget(true);
			entityInsentient.setGoalTarget(enemyInsentientNMS);
			setNowTarget(false);
			
			if (enemyLocation != null && LocationUtils.distance(playerLocation, enemyLocation) <= playerRange
					&& LocationUtils.distance(entityLocation, enemyLocation) > attackRange) {
				entityInsentient.getEntity().teleport(enemyLocation);
			}
			return true;
		}
		return false;
	}

	public boolean canAttack(EntityLivingNMS enemyInsentientNMS) {
		Entity enemy = enemyInsentientNMS.getEntity();

		if (enemy.isDead()) {
			return false;
		}

		Entity entity = entityInsentient.getEntity();
		if (!enemy.getWorld().equals(entity.getWorld())) {
			return false;
		}

		return true;
	}

	public PlayerGuard getPlayer() {
		return playerGuard;
	}

	public EntityInsentientNMS getEntityInsentient() {
		return entityInsentient;
	}

	public boolean isExpireAliveTime() {
		return System.currentTimeMillis() - spawnTime > aliveTime;
	}

	public void setAliveTime(long aliveTime) {
		this.aliveTime = aliveTime;
	}

	public String getName() {
		return name;
	}

	public boolean isNowTarget() {
		return nowTarget;
	}

	public void setNowTarget(boolean nowTarget) {
		this.nowTarget = nowTarget;
	}

	public EntityLivingNMS getLastEnemy() {
		return lastEnemy;
	}

	public void setLastEnemy(Entity entity) {
		this.lastEnemy = new EntityLivingNMS(entity);
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public boolean isSuicide() {
		return suicide;
	}

	public void setSuicide(boolean suicide) {
		this.suicide = suicide;
	}
}
