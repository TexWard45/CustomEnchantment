package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CEFunctionData implements Cloneable {
	private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();
	private List<String> generatedPrefixList = new ArrayList<>();
	private Target target = Target.PLAYER;

	public CEFunctionData(Player player) {
		this.setPlayer(player);
	}

	public Player getOwner() {
		return (Player) get("player");
	}

	public Player getPlayer() {
		return target == Target.PLAYER ? (Player) get("player") : (Player) get("enemy");
	}

	public int getDeathTime() {
		return target == Target.PLAYER ? (int) get("death_time") : (int) get("enemy_death_time");
	}

	public String getNextPrefix(String prefix, int index) {
		if (index < generatedPrefixList.size()) {
			return prefix + generatedPrefixList.get(index);
		}

		for (int i = generatedPrefixList.size(); i <= index; i++) {
			generatedPrefixList.add(String.valueOf(System.nanoTime()));
		}

		return prefix + generatedPrefixList.get(index);
	}

	public CEFunctionData setPlayer(Player player) {
		if (player == null) {
			return this;
		}

		set("player", player);
		set("living_entity", (LivingEntity) player);
		set("living_entity_location", player.getLocation().clone());
		set("death_time", CEAPI.getCEPlayer(player).getDeathTime());
		return this;
	}

	public Player getEnemyPlayer() {
		return target == Target.PLAYER ? (Player) get("enemy") : (Player) get("player");
	}

	public int getEnemyDeathTime() {
		return target == Target.PLAYER ? (int) get("enemy_death_time") : (int) get("death_time");
	}

	public CEFunctionData setEnemyPlayer(Player player) {
		if (player == null) {
			return this;
		}

		set("enemy", player);
		set("enemy_living_entity", (LivingEntity) player);
		set("enemy_living_entity_location", player.getLocation().clone());
		set("enemy_death_time", CEAPI.getCEPlayer(player).getDeathTime());
		return this;
	}

	public LivingEntity getLivingEntity() {
		return getLivingEntityByTarget(Target.PLAYER);
	}

	public CEFunctionData setLivingEntity(LivingEntity livingEntity) {
		if (livingEntity == null) {
			return this;
		}

		set("living_entity", livingEntity);
		set("living_entity_location", livingEntity.getLocation().clone());
		return this;
	}

	public LivingEntity getEnemyLivingEntity() {
		return getLivingEntityByTarget(Target.ENEMY);
	}

	public LivingEntity getLivingEntityByTarget(Target target) {
		if (this.target == target) {
			if (!isSet("living_entity")) {
				return getPlayer();
			} else {
				return (LivingEntity) get("living_entity");
			}
		} else {
			if (!isSet("enemy_living_entity")) {
				return getEnemyPlayer();
			} else {
				return (LivingEntity) get("enemy_living_entity");
			}
		}
	}

	public CEFunctionData setEnemyLivingEntity(LivingEntity livingEntity) {
		set("enemy_living_entity", livingEntity);
		set("enemy_living_entity_location", livingEntity.getLocation().clone());
		return this;
	}

	public Target getTarget() {
		return target;
	}

	public CEFunctionData setTarget(Target target) {
		this.target = target != null ? target : Target.PLAYER;
		return this;
	}

	public Location getOldLocation() {
		return target == Target.PLAYER ? (Location) get("living_entity_location")
				: (Location) get("enemy_living_entity_location");
	}

	public Location getOldEnemyLocation() {
		return target == Target.PLAYER ? (Location) get("enemy_living_entity_location")
				: (Location) get("living_entity_location");
	}

	public EquipSlot getEquipSlot() {
		return (EquipSlot) get("equip_slot");
	}

	public CEFunctionData setEquipSlot(EquipSlot equipSlot) {
		set("equip_slot", equipSlot);
		return this;
	}

	public EquipSlot getActiveEquipSlot() {
		return (EquipSlot) get("active_equip_slot");
	}

	public CEFunctionData setActiveEquipSlot(EquipSlot activeEquipSlot) {
		set("active_equip_slot", activeEquipSlot);
		return this;
	}

	public CEWeaponAbstract getWeaponAbstract() {
		return (CEWeaponAbstract) get("weapon_abstract");
	}

	public CEFunctionData setWeaponAbstract(CEWeaponAbstract weaponAbstract) {
		if (weaponAbstract == null) {
			return this;
		}

		set("weapon_abstract", weaponAbstract);
		return this;
	}

	public DamageCause getDamageCause() {
		return (DamageCause) get("damage_cause");
	}

	public CEFunctionData setDamageCause(DamageCause damageCause) {
		set("damage_cause", damageCause);
		return this;
	}

	public ItemStack getItemConsume() {
		return (ItemStack) get("item_consume");
	}

	public CEFunctionData setItemConsume(ItemStack itemConsume) {
		set("item_consume", itemConsume);
		return this;
	}

    public boolean isFakeSource() {
        return get("fake_source") != null && (boolean) get("fake_source");
    }

    public CEFunctionData setFakeSource(boolean fakeSource) {
        set("fake_source", fakeSource);
        return this;
    }

	public CEFunctionData set(String key, Object value) {
		key = key.toLowerCase();
		if (value == null) {
			map.remove(key);
			return this;
		}

		map.put(key, value);
		return this;
	}

	public CEFunctionData remove(String key) {
		Validate.notNull(key, "key cannot be NULL!");

		key = key.toLowerCase();
		map.remove(key);
		return this;
	}

	public boolean isSet(String key) {
		Validate.notNull(key, "key cannot be NULL!");

		key = key.toLowerCase();
		return map.containsKey(key);
	}

	public Set<String> getKeys() {
		return map.keySet();
	}

	public Object get(String key) {
		Validate.notNull(key, "key cannot be NULL!");

		key = key.toLowerCase();
		return map.get(key);
	}

	public boolean checkValue(String key, Class<?> clazz) {
		Validate.notNull(key, "key cannot be NULL!");
		Validate.notNull(clazz, "clazz cannot be NULL!");

		key = key.toLowerCase();
		return clazz.isAssignableFrom(map.get(key).getClass());
	}

	@Override
	public CEFunctionData clone() {
		try {
			CEFunctionData data = (CEFunctionData) super.clone();
			data.map = new ConcurrentHashMap<String, Object>(map);
			return data;
		} catch (CloneNotSupportedException e) {
			CEFunctionData data = new CEFunctionData(getPlayer());
			data.map = new ConcurrentHashMap<String, Object>(map);
			return data;
		}
	}
}
