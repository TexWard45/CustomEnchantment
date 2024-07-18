package me.texward.customenchantment.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.EntityType;

import me.texward.texwardlib.util.EnumUtils;

public class EntityTypeList extends ArrayList<EntityType> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ConcurrentHashMap<String, EntityTypeList> DEFINE_MAP = new ConcurrentHashMap<String, EntityTypeList>();

	public static void defineEntityTypeList(String key, EntityTypeList list) {
		if (EnumUtils.valueOf(EntityType.class, key, false) != null) {
			return;
		}
		DEFINE_MAP.put(key, list);
	}

	public static EntityTypeList getEntityTypeList(String key) {
		return DEFINE_MAP.get(key);
	}

	public static ConcurrentHashMap<String, EntityTypeList> getMap() {
		return DEFINE_MAP;
	}

	public static EntityTypeList getEntityTypeList(List<String> list) {
		EntityTypeList entityTypeList = new EntityTypeList();

		for (String line : list) {
			EntityTypeList availableEntityTypeList = getEntityTypeList(line);

			if (availableEntityTypeList != null) {
				entityTypeList.addAll(availableEntityTypeList);
			} else {
				EntityType type = EnumUtils.valueOf(EntityType.class, line);

				if (type == null) {
					System.out.println("Cannot find EntityType." + line);
					return null;
				}

				entityTypeList.add(EntityType.valueOf(line));
			}
		}

		return entityTypeList;
	}

	public EntityTypeList() {
	}

	public EntityTypeList(List<EntityType> list) {
		this.addAll(list);
	}

	public boolean add(EntityType entityType) {
		if (this.contains(entityType)) {
			return false;
		} else {
			return super.add(entityType);
		}
	}

	public boolean addAll(List<EntityType> list) {
		for (EntityType entityType : list) {
			add(entityType);
		}
		return true;
	}
}
