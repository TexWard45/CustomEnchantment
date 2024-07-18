package me.texward.customenchantment.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;

import me.texward.texwardlib.util.EnumUtils;

public class MaterialList extends ArrayList<MaterialData> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static ConcurrentHashMap<String, MaterialList> DEFINE_MAP = new ConcurrentHashMap<String, MaterialList>();

	public static void defineMaterialList(String key, MaterialList list) {
		if (EnumUtils.valueOf(Material.class, key, false) != null) {
			return;
		}
		DEFINE_MAP.put(key, list);
	}

	public static MaterialList getMaterialList(String key) {
		return DEFINE_MAP.get(key);
	}

	public static ConcurrentHashMap<String, MaterialList> getMap() {
		return DEFINE_MAP;
	}

	public static MaterialList getMaterialList(List<String> list) {
		MaterialList materialList = new MaterialList();

		for (String line : list) {
			MaterialList materialNMSList = getMaterialList(line);

			if (materialNMSList != null) {
				materialList.addAll(materialNMSList);
			} else {
				materialList.add(MaterialData.getMaterialNMSByString(line));
			}
		}

		return materialList;
	}

	public MaterialList() {
	}

	public MaterialList(List<MaterialData> list) {
		this.addAll(list);
	}

	public boolean add(MaterialData materialNMS) {
		if (this.contains(materialNMS)) {
			return false;
		} else {
			return super.add(materialNMS);
		}
	}

	public boolean addAll(List<MaterialData> list) {
		for (MaterialData materialNMS : list) {
			add(materialNMS);
		}
		return true;
	}
}
