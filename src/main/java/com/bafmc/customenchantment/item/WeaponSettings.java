package com.bafmc.customenchantment.item;

import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Builder
public class WeaponSettings {
	private static Map<String, WeaponSettings> map = new ConcurrentHashMap<String, WeaponSettings>();

	public static WeaponSettings getSettings(String name) {
		return map.containsKey(name) ? map.get(name) : map.get(CENBT.DEFAULT);
	}

	public static void setSettingsMap(Map<String, WeaponSettings> map) {
		WeaponSettings.map = map != null ? map : new ConcurrentHashMap<String, WeaponSettings>();
	}

	@Getter
    private int enchantPoint;
	@Getter
    private String vanillaEnchantLore;
	@Getter
    private String customEnchantLore;

	private SparseMap<String> enchantPointLore;
	private SparseMap<String> protectDeadLore;
	private SparseMap<String> protectDestroyLore;

	private List<String> loreStyle;
	private List<String> loreStyleWithMask;
	
	@Getter
    private List<ItemFlag> itemFlags;
	@Getter
    private Map<NMSAttributeType, String> attributeTypeMap;
	@Getter
    private Map<String, String> attributeSlotMap;
	@Getter
	private Map<MaterialList, Integer> gemPointMap;
	@Getter
	private String customGemLore;

    public String getEnchantPointLore(int index) {
		return enchantPointLore.containsKey(index) ? enchantPointLore.get(index) : enchantPointLore.get(-1);
	}

	public String getProtectDeadLore(int index) {
		return protectDeadLore.containsKey(index) ? protectDeadLore.get(index) : protectDeadLore.get(-1);
	}

	public String getProtectDestroyLore(int index) {
		return protectDestroyLore.containsKey(index) ? protectDestroyLore.get(index) : protectDestroyLore.get(-1);
	}

	public List<String> getLoreStyle() {
		return new ArrayList<String>(loreStyle);
	}

	public List<String> getLoreStyleWithMask() {
		return new ArrayList<String>(loreStyleWithMask);
	}

	public int getGemPoint(ItemStack itemStack) {
		for (MaterialList materialList : gemPointMap.keySet()) {
			if (materialList.contains(new MaterialData(itemStack))) {
				return gemPointMap.get(materialList);
			}
		}

		return 0;
	}
}
