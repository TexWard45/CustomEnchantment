package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.bukkit.utils.RandomRange;
import com.bafmc.bukkit.utils.RandomUtils;
import com.bafmc.bukkit.utils.SoundUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.CustomEnchantmentMessage;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.attribute.AttributeCalculate;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.command.CommandDebugAll;
import com.bafmc.customenchantment.config.MainConfig;
import com.bafmc.customenchantment.enchant.*;
import com.bafmc.customenchantment.event.CEPlayerStatsModifyEvent;
import com.bafmc.customenchantment.guard.Guard;
import com.bafmc.customenchantment.guard.GuardManager;
import com.bafmc.customenchantment.guard.PlayerGuard;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.player.*;
import com.bafmc.customenchantment.player.PlayerAbility.Type;
import com.bafmc.customenchantment.task.ArrowTask;
import com.bafmc.customenchantment.utils.DamageUtils;
import io.github.magiccheese1.damageindicator.api.DamageIndicatorAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityListener implements Listener {
	private static ConcurrentHashMap<Entity, CEWeaponAbstract> arrowMap = new ConcurrentHashMap<Entity, CEWeaponAbstract>();
	private CustomEnchantment plugin;
	private GuardManager guardManager;
	private boolean damageIndicatorPluginEnabled;

	public EntityListener(CustomEnchantment plugin) {
		this.plugin = plugin;
		this.guardManager = plugin.getGuardModule().getGuardManager();
		this.damageIndicatorPluginEnabled = Bukkit.getPluginManager().isPluginEnabled("DamageIndicator");

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

    private static List<String> mainArrowShootList = new ArrayList<>();

    public static void removeMainArrowShootList(String uuid) {
        mainArrowShootList.remove(uuid);
    }

	@EventHandler
	public void onSheepDyeWool(SheepDyeWoolEvent e) {
		CEItem ceItem = CEAPI.getCEItem(e.getPlayer().getInventory().getItemInMainHand());
		if (ceItem != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityResurrect(EntityResurrectEvent e) {
		if (e.getEntity() instanceof Player player) {
			for (EquipSlot equipSlot : EquipSlot.HAND_ARRAY) {
				ItemStack itemStack = equipSlot.getItemStack(player);
				if (itemStack == null || itemStack.getType() != Material.TOTEM_OF_UNDYING) {
					continue;
				}

				CEItem ceItem = CEAPI.getCEItem(itemStack);
				if (ceItem != null ) {
					e.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onProjectile(ProjectileLaunchEvent e) {
		Projectile projectile = e.getEntity();
		if (!(projectile instanceof Arrow) || !(((Arrow) projectile).getShooter() instanceof Player)) {
			return;
		}

		// Setup variable
		Player player = (Player) projectile.getShooter();
		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();

		if (storage.isBoolean(TemporaryKey.NOW_MULTIPLE_ARROW)) {
			return;
		}

		double power = storage.getDouble(TemporaryKey.BOW_POWER, 1d);

		// Call ce
		Map<EquipSlot, CEWeaponAbstract> weaponMap = CEAPI.getCEWeaponMap(player);

        CEWeaponAbstract ceWeapon = null;
        if (weaponMap.get(EquipSlot.MAINHAND) != null && isBow(weaponMap.get(EquipSlot.MAINHAND).getDefaultItemStack().getType())) {
            ceWeapon = weaponMap.get(EquipSlot.MAINHAND);
        } else if (weaponMap.get(EquipSlot.OFFHAND) != null && isBow(weaponMap.get(EquipSlot.OFFHAND).getDefaultItemStack().getType())) {
            ceWeapon = weaponMap.get(EquipSlot.OFFHAND);
        }

        if (ceWeapon == null) {
            return;
        }

        // Put arrow with weapon to arrowMap
        putArrow(projectile, ceWeapon);

		CECallerList result = CECallerBuilder
									.build(player)
									.setCEType(CEType.BOW_SHOOT)
									.setWeaponMap(weaponMap)
									.call();
		power = AttributeCalculate.calculate(cePlayer, CustomAttributeType.OPTION_POWER, power, result.getOptionDataList());

		// Enable Multiple Arrow
		storage.set(TemporaryKey.NOW_MULTIPLE_ARROW, true);
		if (storage.isBoolean(TemporaryKey.MULTIPLE_ARROW_ENABLE)) {
			long cooldown = storage.getLong(TemporaryKey.MULTIPLE_ARROW_COOLDOWN, 0);
			long lastUse = storage.getLong(TemporaryKey.MULTIPLE_ARROW_LAST_USE, 0);

			if (System.currentTimeMillis() - lastUse > cooldown) {
				shootMultipleArrow(cePlayer, ceWeapon, projectile.getVelocity(), power, projectile.getFireTicks(), storage.getDouble(TemporaryKey.MULTIPLE_ARROW_DAMAGE_RATIO, 1d));
				storage.set(TemporaryKey.MULTIPLE_ARROW_LAST_USE, System.currentTimeMillis());
			}
		}
		storage.set(TemporaryKey.NOW_MULTIPLE_ARROW, false);

		if (power != 1d) {
			projectile.setVelocity(projectile.getVelocity().multiply(power));
		}

        mainArrowShootList.add(projectile.getUniqueId().toString());
	}

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectile(ProjectileHitEvent e) {
        Entity entity = e.getEntity();

        if (!(entity instanceof Arrow)) {
            return;
        }

        ArrowTask.addEntity(e.getEntity());
    }

    public boolean isBow(Material material) {
        return material == Material.BOW || material == Material.CROSSBOW;
    }

	public static void putArrow(Entity entity, CEWeaponAbstract weapon) {
		arrowMap.put(entity, weapon);
	}

	public void shootMultipleArrow(CEPlayer cePlayer, CEWeaponAbstract ceWeapon, Vector vector, double power,
			int fireTicks, double damageRatio) {
		Player player = cePlayer.getPlayer();
		PlayerTemporaryStorage storage = cePlayer.getTemporaryStorage();

		RandomRange amountRange = (RandomRange) storage.get(TemporaryKey.MULTIPLE_ARROW_AMOUNT, new RandomRange(0));
		RandomRange velocityMod = (RandomRange) storage.get(TemporaryKey.MULTIPLE_ARROW_MOD, new RandomRange(0));

		int amount = amountRange.getIntValue();

		for (int i = 0; i < amount; i++) {
			Arrow arrow = (Arrow) player.launchProjectile(Arrow.class,
					vector.setX(vector.getX() + velocityMod.getValue()).setY(vector.getY() + velocityMod.getValue())
							.setZ(vector.getZ() + velocityMod.getValue()).multiply(power));
            arrow.setMetadata("ce_multi_arrow_damage_ratio", new FixedMetadataValue(plugin, damageRatio));
			arrow.setFireTicks(fireTicks);
			putArrow(arrow, ceWeapon);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAttack(EntityDamageEvent e) {
		Entity entity = e.getEntity();

		if (!(entity instanceof Player)) {
			return;
		}

		double damage = e.getDamage();
		Player pDefenser = (Player) entity;

		CEFunctionData data = new CEFunctionData(pDefenser);
		data.setDamageCause(e.getCause());

		CECallerList result = CECallerBuilder
				.build(pDefenser)
				.setCEType(CEType.UNKNOWN_DEFENSE)
				.setCEFunctionData(data)
				.call();

		if (!result.getOptionDataList().isEmpty()) {
			e.setDamage(AttributeCalculate.calculate(CEAPI.getCEPlayer(pDefenser), CustomAttributeType.OPTION_DEFENSE, damage,
					result.getOptionDataList()));
		}

//		for (Player player : Bukkit.getOnlinePlayers()) {
//			player.sendMessage(
//					"Unknown (" + e.getCause().name() + "): Before " + new DecimalFormat("#.##").format(damage)
//							+ " | After " + new DecimalFormat("#.##").format(e.getDamage())	+ " | Final "
//									+ new DecimalFormat("#.##").format(e.getFinalDamage()));
//		}
	}

	private static Map<String, Integer> combatWarnTime = new HashMap<>();
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent e) {
		if (e.getCause() == DamageCause.THORNS) {
			return;
		}

		Entity attacker = getRealEntity(e.getDamager());
		Entity defender = getRealEntity(e.getEntity());

		if (attacker instanceof Player) {
			CEPlayer cePlayer = (CEPlayer) CEAPI.getCEPlayer(((Player) attacker));
			PlayerAbility ability = cePlayer.getAbility();
			if (ability.isCancel(Type.ATTACK)) {
				e.setCancelled(true);
				return;
			}

			CEWeaponAbstract ceWeapon = cePlayer.getEquipment().getSlot(EquipSlot.MAINHAND);
			if (ceWeapon == null) {
				MainConfig mainConfig = plugin.getMainConfig();
				if (mainConfig.isCombatSettingsRequireWeapon()) {
					if (combatWarnTime.getOrDefault(attacker.getName(), 0) % 5 == 0) {
						CustomEnchantmentMessage.send(attacker, "combat.require-weapon");
					}

					combatWarnTime.put(attacker.getName(), combatWarnTime.getOrDefault(attacker.getName(), 0) + 1);
					e.setCancelled(true);
					return;
				}
			}
		}

		if (e.getCause() == DamageCause.ENTITY_ATTACK && defender instanceof Player) {
			Player player = null;
			if (attacker instanceof Player) {
				player = (Player) attacker;
			} else if (attacker instanceof Arrow && ((Arrow) attacker).getShooter() instanceof Player) {
				player = (Player) ((Arrow) attacker).getShooter();
			}
			if (isDodged((Player) defender, player)) {
				e.setCancelled(true);
				return;
			}
		}

		if (attacker instanceof Player) {
			PlayerGuard playerGuard = guardManager.getPlayerGuard((Player) attacker);

			if (isLivingEntity(defender) && !isSameTeam(playerGuard, defender)) {
				playerGuard.setTarget(defender);
			}
		}

		Guard defenserGuard = guardManager.getGuard(defender);
		if (defenserGuard != null) {
			if (isLivingEntity(attacker) && !isSameTeam(defenserGuard, attacker)) {
				defenserGuard.setLastEnemy(attacker);
			}
			return;
		}

		Guard attackerGuard = guardManager.getGuard(attacker);
		if (attackerGuard != null) {
			if (attackerGuard.getDamage() >= 0) {
				e.setDamage(attackerGuard.getDamage());
			}

			if (attackerGuard.isSuicide()) {
				attacker.remove();
			}
		}

		if (defender instanceof Player) {
			PlayerGuard playerGuard = guardManager.getPlayerGuard((Player) defender);

			if (isLivingEntity(attacker) && !isSameTeam(playerGuard, attacker)) {
				playerGuard.setLastEnemy(attacker);
			}
		}

		attacker = e.getDamager();
		defender = e.getEntity();
		double defaultDamage = e.getDamage();
		double criticalDamage = handleCritical(attacker, defaultDamage);
		boolean isCritical = criticalDamage != defaultDamage;
		double currentDamage = criticalDamage;

		currentDamage = onPlayerVsMob(attacker, defender, currentDamage, e);
		currentDamage = onMobVsPlayer(attacker, defender, currentDamage, e);
		currentDamage = onPlayerVsPlayer(attacker, defender, currentDamage, e);
		currentDamage = onArrowPlayerVsPlayer(attacker, defender, currentDamage, e);
		currentDamage = onArrowPlayerVsMob(attacker, defender, currentDamage, e);

		if (attacker instanceof Player) {
			CEPlayer cePlayer = CEAPI.getCEPlayer((Player) attacker);
			PlayerCustomAttribute attribute = cePlayer.getCustomAttribute();

			double vulnerability = attribute.getValue(CustomAttributeType.VULNERABILITY);
			if (vulnerability > 0) {
				currentDamage = currentDamage * (1 + vulnerability / 100);
			}
		}

		if (currentDamage != defaultDamage)
			e.setDamage(currentDamage);

		double finalDamage = e.getFinalDamage();

		if (attacker instanceof Player) {
			handleLifeSteal((Player) attacker, finalDamage);
		}

		if (damageIndicatorPluginEnabled) {
			LivingEntity defenderLivingEntity = getRealLivingEntity(defender);
			LivingEntity attackerLivingEntity = getRealLivingEntity(attacker);

			if (attackerLivingEntity instanceof Player playerAttacker && defenderLivingEntity instanceof LivingEntity) {
				if (isCritical) {
					DamageIndicatorAPI.spawnIndicator(defenderLivingEntity, playerAttacker, DamageIndicatorAPI.getCriticalDamageFormat(), finalDamage);
				} else {
					DamageIndicatorAPI.spawnIndicator(defenderLivingEntity, playerAttacker, DamageIndicatorAPI.getDamageFormat(), finalDamage);
				}
			}
		}

		if (isCritical) {
			SoundUtils.play(attacker, Sound.ENTITY_PLAYER_ATTACK_CRIT);
		}

		if (attacker instanceof Player) {
			Player player = (Player) attacker;
			CEPlayer cePlayer = CEAPI.getCEPlayer(player);

			if (cePlayer.isDebugMode()) {
				System.out.println("Before " + new DecimalFormat("#.##").format(defaultDamage) + " | After "
						+ new DecimalFormat("#.##").format(currentDamage) + " | Final "
						+ new DecimalFormat("#.##").format(e.getFinalDamage()));
			}

			if (CommandDebugAll.isDebugMode()) {
				String attackerName = player.getName();
				String defenderName = defender instanceof Player ? ((Player) defender).getName() : defender.getType().name();

				System.out.println("Attacker " + attackerName + " Defender " + defenderName + " Before " + new DecimalFormat("#.##").format(defaultDamage) + " | After "
						+ new DecimalFormat("#.##").format(currentDamage) + " | Final "
						+ new DecimalFormat("#.##").format(e.getFinalDamage()));
			}
		}

		if (attacker instanceof Arrow && ((Arrow) attacker).getShooter() instanceof Player) {
			Player player = ((Player) ((Arrow) attacker).getShooter());
			CEPlayer cePlayer = CEAPI.getCEPlayer(player);

			if (cePlayer.isDebugMode()) {
				String attackerName = ((Player) ((Arrow) attacker).getShooter()).getName();
				String defenderName = defender instanceof Player ? ((Player) defender).getName() : defender.getType().name();

				System.out.println("Attacker " + attackerName + " Defender " + defenderName + " Before " + new DecimalFormat("#.##").format(defaultDamage) + " | After "
						+ new DecimalFormat("#.##").format(currentDamage) + " | Final "
						+ new DecimalFormat("#.##").format(e.getFinalDamage()));
			}

			if (CommandDebugAll.isDebugMode()) {
				String attackerName = ((Player) ((Arrow) attacker).getShooter()).getName();
				String defenderName = defender instanceof Player ? ((Player) defender).getName() : defender.getType().name();

				System.out.println("Attacker " + attackerName + " Defender " + defenderName + " Before " + new DecimalFormat("#.##").format(defaultDamage) + " | After "
						+ new DecimalFormat("#.##").format(currentDamage) + " | Final "
						+ new DecimalFormat("#.##").format(e.getFinalDamage()));
			}
		}

//		System.out.println("START");
//		for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
//			System.out.println(modifier.name() + " " + e.getDamage(modifier));
//		}
//		System.out.println("FINAL " + e.getFinalDamage());
//		System.out.println("END");
	}

	public boolean isLivingEntity(Entity entity) {
		if (entity instanceof Arrow && ((Arrow) entity).getShooter() instanceof Entity) {
			entity = (Entity) ((Arrow) entity).getShooter();
		}
		return entity instanceof LivingEntity;
	}

	public boolean isSameTeam(PlayerGuard playerGuard, Entity entity) {
		if (!(entity instanceof LivingEntity)) {
			return false;
		}

		if (entity instanceof Arrow) {
			entity = (Entity) ((Arrow) entity).getShooter();
		}

		Guard guard = guardManager.getGuard(entity);
		if (guard != null && playerGuard.getName().equals(guard.getTeam())) {
			return true;
		}
		return false;
	}

	public boolean isSameTeam(Guard guard, Entity entity) {
		if (!(entity instanceof LivingEntity)) {
			return false;
		}

		if (entity instanceof Arrow) {
			entity = (Entity) ((Arrow) entity).getShooter();
		}

		Guard targetGuard = guardManager.getGuard(entity);
		if (targetGuard != null && targetGuard.getTeam().equals(guard.getTeam())) {
			return true;
		}

		if (entity instanceof Player && guard.getTeam().equals(entity.getName())) {
			return true;
		}
		return false;
	}

	public Entity getRealEntity(Entity entity) {
		if (entity instanceof Arrow && ((Arrow) entity).getShooter() instanceof Entity) {
			entity = (Entity) ((Arrow) entity).getShooter();
		}
		return entity;
	}

	public LivingEntity getRealLivingEntity(Entity entity) {
		if (entity instanceof Arrow && ((Arrow) entity).getShooter() instanceof LivingEntity) {
			return (LivingEntity) ((Arrow) entity).getShooter();
		}
		if (entity instanceof LivingEntity) {
			return (LivingEntity) entity;
		}
		return null;
	}

	public double onPlayerVsMob(Entity attacker, Entity defenser, double damage, EntityDamageByEntityEvent e) {
		if (!(attacker instanceof Player) || !(defenser instanceof LivingEntity) || defenser instanceof Player) {
			return damage;
		}

		Player pAttacker = (Player) attacker;
		CEPlayer cePlayer = CEAPI.getCEPlayer(pAttacker);
		cePlayer.getTemporaryStorage().set(TemporaryKey.LAST_COMBAT_TIME, System.currentTimeMillis());
		LivingEntity eDefenser = (LivingEntity) defenser;

		CEFunctionData data;
		CECallerList result;
		if (e.getCause() != DamageCause.ENTITY_SWEEP_ATTACK) {
			data = new CEFunctionData(pAttacker);
			data.setEnemyLivingEntity(eDefenser);
			data.set("damage", damage);
			data.setDamageCause(e.getCause());

			result = CECallerBuilder
					.build(pAttacker)
					.setCEType(CEType.ATTACK)
					.setCEFunctionData(data)
					.call();

			damage = AttributeCalculate.calculate(cePlayer, CustomAttributeType.OPTION_ATTACK, damage,
					result.getOptionDataList());
		}

		List<NMSAttribute> attributes = new ArrayList<>();
		if (e.getCause() != DamageCause.ENTITY_SWEEP_ATTACK) {
			data = new CEFunctionData(pAttacker);
			data.setEnemyLivingEntity(eDefenser);
			data.set("damage", damage);
			data.setDamageCause(e.getCause());

			result = CECallerBuilder
					.build(pAttacker)
					.setCEType(CEType.FINAL_ATTACK)
					.setCEFunctionData(data)
					.call();

			damage = AttributeCalculate.calculate(cePlayer, CustomAttributeType.OPTION_ATTACK, damage,
					result.getOptionDataList());

			attributes = result.getOptionDataList();
		}

		if (handleArmorPenetration(pAttacker, defenser, e, attributes)) {
			e.setDamage(damage);
		}

		return damage;
	}

	public double onMobVsPlayer(Entity attacker, Entity defenser, double damage, EntityDamageByEntityEvent e) {
		if (!(attacker instanceof LivingEntity) || attacker instanceof Player || !(defenser instanceof Player)) {
			return damage;
		}

		LivingEntity eAttacker = (LivingEntity) attacker;
		Player pDefenser = (Player) defenser;
		CEPlayer cePlayer = CEAPI.getCEPlayer(pDefenser);
		cePlayer.getTemporaryStorage().set(TemporaryKey.LAST_COMBAT_TIME, System.currentTimeMillis());

		CEFunctionData data = new CEFunctionData(pDefenser);
		data.setEnemyLivingEntity(eAttacker);
		data.set("damage", damage);
		data.setDamageCause(e.getCause());

		CECallerList result = CECallerBuilder
				.build(pDefenser)
				.setCEType(CEType.DEFENSE)
				.setCEFunctionData(data)
				.call();
		
		damage = AttributeCalculate.calculate(cePlayer, CustomAttributeType.OPTION_DEFENSE, damage,
				result.getOptionDataList());

		boolean handleDamageReduction = handleDamageReduction((LivingEntity) defenser, damage, e);
		if (handleDamageReduction) {
			e.setDamage(damage);
		}

		return damage;
	}

	public double onPlayerVsPlayer(Entity attacker, Entity defenser, double damage, EntityDamageByEntityEvent e) {
		if (!(attacker instanceof Player) || !(defenser instanceof Player)) {
			return damage;
		}

		Player pAttacker = (Player) attacker;
		Player pDefenser = (Player) defenser;
		CEPlayer cePlayerAttacker = CEAPI.getCEPlayer(pAttacker);
		CEPlayer cePlayerDefenser = CEAPI.getCEPlayer(pDefenser);
		cePlayerAttacker.getTemporaryStorage().set(TemporaryKey.LAST_COMBAT_TIME, System.currentTimeMillis());
		cePlayerDefenser.getTemporaryStorage().set(TemporaryKey.LAST_COMBAT_TIME, System.currentTimeMillis());

		CEFunctionData pData;
		CECallerList pResult;
		if (e.getCause() != DamageCause.ENTITY_SWEEP_ATTACK) {
			pData = new CEFunctionData(pAttacker);
			pData.setEnemyPlayer(pDefenser);
			pData.set("damage", damage);
			pData.setDamageCause(e.getCause());

			pResult = CECallerBuilder
					.build(pAttacker)
					.setCEType(CEType.ATTACK)
					.setCEFunctionData(pData)
					.call();
			damage = AttributeCalculate.calculate(CEAPI.getCEPlayer(pAttacker), CustomAttributeType.OPTION_ATTACK, damage,
					pResult.getOptionDataList());
		}

		CEFunctionData eData = new CEFunctionData(pDefenser);
		eData.setEnemyPlayer(pAttacker);
		eData.set("damage", damage);
		eData.setDamageCause(e.getCause());

		CECallerList eResult = CECallerBuilder
				.build(pDefenser)
				.setCEType(CEType.DEFENSE)
				.setCEFunctionData(eData)
				.call();
		damage = AttributeCalculate.calculate(CEAPI.getCEPlayer(pDefenser), CustomAttributeType.OPTION_DEFENSE, damage,
				eResult.getOptionDataList());
		boolean handleDamageReduction = handleDamageReduction((LivingEntity) defenser, damage, e);

		List<NMSAttribute> attributes = new ArrayList<>();
		if (e.getCause() != DamageCause.ENTITY_SWEEP_ATTACK) {
			pData = new CEFunctionData(pAttacker);
			pData.setEnemyPlayer(pDefenser);
			pData.set("damage", damage);
			pData.setDamageCause(e.getCause());

			pResult = CECallerBuilder
					.build(pAttacker)
					.setCEType(CEType.FINAL_ATTACK)
					.setCEFunctionData(pData)
					.call();

			damage = AttributeCalculate.calculate(CEAPI.getCEPlayer(pAttacker), CustomAttributeType.OPTION_ATTACK, damage,
					pResult.getOptionDataList());

			attributes = pResult.getOptionDataList();
		}

		boolean handleArmorPenetration = handleArmorPenetration(pAttacker, pDefenser, e, attributes);
		if (handleDamageReduction || handleArmorPenetration) {
			e.setDamage(damage);
		}

		return damage;
	}

	public double onArrowPlayerVsPlayer(Entity attacker, Entity defender, double damage, EntityDamageByEntityEvent e) {
		if (!(attacker instanceof Arrow) || !(((Arrow) attacker).getShooter() instanceof Player)
				|| !(defender instanceof Player)) {
			return damage;
		}

		CEWeaponAbstract weapon = arrowMap.get(attacker);
		if (weapon == null) {
			return damage;
		}

		Player pAttacker = (Player) ((Arrow) attacker).getShooter();
		Player pDefenser = (Player) defender;
		CEPlayer cePlayerAttacker = CEAPI.getCEPlayer(pAttacker);
		CEPlayer cePlayerDefenser = CEAPI.getCEPlayer(pDefenser);
		cePlayerAttacker.getTemporaryStorage().set(TemporaryKey.LAST_COMBAT_TIME, System.currentTimeMillis());
		cePlayerDefenser.getTemporaryStorage().set(TemporaryKey.LAST_COMBAT_TIME, System.currentTimeMillis());

        boolean isFakeArrow = !mainArrowShootList.contains(attacker.getUniqueId().toString());

		CEFunctionData pData = new CEFunctionData(pAttacker);
		pData.setEnemyPlayer(pDefenser);
		pData.set("damage", damage);
        pData.setFakeSource(isFakeArrow);
		pData.setDamageCause(e.getCause());

		Map<EquipSlot, CEWeaponAbstract> weaponMap = CEAPI.getCEWeaponMap(pAttacker);

		CECallerList pResult = CECallerBuilder
				.build(pAttacker)
				.setCEType(CEType.ARROW_HIT)
				.setWeaponMap(weaponMap)
				.setCEFunctionData(pData)
                .setByPassCooldown(isFakeArrow)
				.call();

		damage = AttributeCalculate.calculate(CEAPI.getCEPlayer(pAttacker), CustomAttributeType.OPTION_ATTACK, damage,
				pResult.getOptionDataList());

		CEFunctionData eData = new CEFunctionData(pDefenser);
		eData.setEnemyPlayer(pAttacker);
		eData.set("damage", damage);
		eData.setDamageCause(e.getCause());

        CECallerList eResult = CECallerBuilder
                .build(pDefenser)
                .setCEType(CEType.ARROW_DEFENSE)
                .setCEFunctionData(eData)
                .call();

		double finalDamage = AttributeCalculate.calculate(CEAPI.getCEPlayer(pDefenser), CustomAttributeType.OPTION_DEFENSE, damage,
                eResult.getOptionDataList());
		boolean handleDamageReduction = handleDamageReduction((LivingEntity) defender, finalDamage, e);

        if (attacker.hasMetadata("ce_multi_arrow_damage_ratio")) {
            finalDamage *= attacker.getMetadata("ce_multi_arrow_damage_ratio").get(0).asDouble();
        }

		boolean handleArmorPenetration = handleArmorPenetration(pAttacker, pDefenser, e, pResult.getOptionDataList());
		if (handleDamageReduction || handleArmorPenetration) {
			e.setDamage(finalDamage);
		}

		return finalDamage;
	}

	public double onArrowPlayerVsMob(Entity attacker, Entity defender, double damage, EntityDamageByEntityEvent e) {
		if (!(attacker instanceof Arrow) || !(((Arrow) attacker).getShooter() instanceof Player)
				|| !(defender instanceof LivingEntity) || defender instanceof Player) {
			return damage;
		}

		CEWeaponAbstract weapon = arrowMap.get(attacker);
		if (weapon == null) {
			return damage;
		}

		Player pAttacker = (Player) ((Arrow) attacker).getShooter();
		CEPlayer cePlayer = CEAPI.getCEPlayer(pAttacker);
		cePlayer.getTemporaryStorage().set(TemporaryKey.LAST_COMBAT_TIME, System.currentTimeMillis());
		LivingEntity eDefenser = (LivingEntity) defender;

		CEFunctionData data = new CEFunctionData(pAttacker);
		data.setEnemyLivingEntity(eDefenser);
		data.set("damage", damage);
		data.setDamageCause(e.getCause());

		Map<EquipSlot, CEWeaponAbstract> weaponMap = CEAPI.getCEWeaponMap(pAttacker);

		CECallerList result = CECallerBuilder
				.build(pAttacker)
				.setCEType(CEType.ARROW_HIT)
				.setWeaponMap(weaponMap)
				.setCEFunctionData(data)
				.call();
		
		damage = AttributeCalculate.calculate(CEAPI.getCEPlayer(pAttacker), CustomAttributeType.OPTION_ATTACK, damage,
				result.getOptionDataList());

		if (handleArmorPenetration(pAttacker, eDefenser, e, result.getOptionDataList())) {
			e.setDamage(damage);
		}

		return damage;
	}

	public Player getPlayer(Entity entity) {
		if (entity instanceof Player) {
			return (Player) entity;
		}
		if (entity instanceof Arrow && ((Arrow) entity).getShooter() instanceof Player) {
			return (Player) ((Arrow) entity).getShooter();
		}
		return null;
	}

	public double handleCritical(Entity entity, double damage) {
		if (damage <= 0) {
			return damage;
		}

		Player player = getPlayer(entity);
		if (player == null) {
			return damage;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		PlayerCustomAttribute attribute = cePlayer.getCustomAttribute();

		int chance = (int) attribute.getValue(CustomAttributeType.CRITICAL_CHANCE);
		if (!RandomUtils.randomChance(chance)) {
			return damage;
		}

		CustomEnchantmentMessage.send(player, "attribute.critical.success");
		return damage * attribute.getValue(CustomAttributeType.CRITICAL_DAMAGE);
	}

	public boolean handleDamageReduction(LivingEntity entity, double damage, EntityDamageByEntityEvent e) {
		if (damage <= 0) {
			return false;
		}

		Player player = getPlayer(entity);
		if (player == null) {
			return false;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		PlayerCustomAttribute attribute = cePlayer.getCustomAttribute();

		float resistanceBonus = (float) (attribute.getValue(CustomAttributeType.DAMAGE_REDUCTION) / 100);
		e.setOverriddenFunction(EntityDamageEvent.DamageModifier.RESISTANCE, DamageUtils.getResistanceDamageModifier(entity, e.getDamageSource(), resistanceBonus));
		return true;
	}

	public boolean isDodged(Player defender, Player attacker) {
		CEPlayer cePlayer = CEAPI.getCEPlayer(defender);
		PlayerCustomAttribute attribute = cePlayer.getCustomAttribute();

		int chance = (int) attribute.getValue(CustomAttributeType.DODGE_CHANCE);

		if (attacker != null) {
			CEPlayer ceAttacker = CEAPI.getCEPlayer(attacker);
			chance -= (int) ceAttacker.getCustomAttribute().getValue(CustomAttributeType.ACCURACY_CHANCE);
		}

		if (chance <= 0) {
			return false;
		}

		if (RandomUtils.randomChance(chance)) {
			CustomEnchantmentMessage.send(defender, "attribute.dodge.success");
			return true;
		}
		return false;
	}

	public void handleLifeSteal(Player player, double damage) {
		if (damage <= 0) {
			return;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer(player);
		PlayerCustomAttribute attribute = cePlayer.getCustomAttribute();

		double lifeSteal = attribute.getValue(CustomAttributeType.LIFE_STEAL);
		if (lifeSteal <= 0) {
			return;
		}

		double heal = damage * lifeSteal / 100;

		double defaultValue = player.getHealth();
		double currentValue = player.getHealth() + heal;

		CEPlayerStatsModifyEvent event = new CEPlayerStatsModifyEvent(cePlayer, CustomAttributeType.STAT_HEALTH, ModifyType.ADD,
				defaultValue, currentValue, false);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		player.setHealth(Math.max(Math.min(event.getChangeValue(), player.getMaxHealth()), 0));
	}

	public boolean handleArmorPenetration(Entity attacker, Entity defender, EntityDamageByEntityEvent e, List<NMSAttribute> list) {
		if (!(attacker instanceof LivingEntity) || !(defender instanceof LivingEntity)) {
			return false;
		}

		if (!(attacker instanceof Player)) {
			return false;
		}

		CEPlayer cePlayer = CEAPI.getCEPlayer((Player) attacker);
		PlayerCustomAttribute attribute = cePlayer.getCustomAttribute();

		double armorPenetration = attribute.getValue(CustomAttributeType.ARMOR_PENETRATION);
		armorPenetration = AttributeCalculate.calculate(cePlayer, CustomAttributeType.OPTION_ARMOR_PENETRATION, armorPenetration, list);

		if (armorPenetration <= 0) {
			return false;
		}

		e.setOverriddenFunction(EntityDamageEvent.DamageModifier.ARMOR, DamageUtils.getArmorDamageModifier((LivingEntity) defender, (float) e.getDamage(), e.getDamageSource(), (int) armorPenetration));
		return true;
	}
}
