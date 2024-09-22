package com.bafmc.customenchantment.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.inventory.ItemFlag;

import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;

public class WeaponSettings {
	private static Map<String, WeaponSettings> map = new ConcurrentHashMap<String, WeaponSettings>();

	public static WeaponSettings getSettings(String name) {
		return map.containsKey(name) ? map.get(name) : map.get(CENBT.DEFAULT);
	}

	public static void setSettingsMap(Map<String, WeaponSettings> map) {
		WeaponSettings.map = map != null ? map : new ConcurrentHashMap<String, WeaponSettings>();
	}

	
	private int enchantPoint;
	private String vanillaEnchantLore;
	private String customEnchantLore;

	private SparseMap<String> enchantPointLore;
	private SparseMap<String> protectDeadLore;
	private SparseMap<String> protectDestroyLore;

	private List<String> loreStyle;
	private List<String> loreStyleWithMask;
	
	private List<ItemFlag> itemFlags;
	private Map<NMSAttributeType, String> attributeTypeMap;
	private Map<String, String> attributeSlotMap;

	public WeaponSettings(int enchantPoint, String vanillaEnchantLore, String customEnchantLore, SparseMap<String> enchantPointLore,
			SparseMap<String> protectDeadLore, SparseMap<String> protectDestroyLore, List<String> loreStyle,
			List<String> loreStyleWithMask, List<ItemFlag> itemFlags, Map<NMSAttributeType, String> attributeTypeMap, Map<String, String> attributeSlotMap) {
		this.enchantPoint = enchantPoint;
		this.vanillaEnchantLore = vanillaEnchantLore;
		this.customEnchantLore = customEnchantLore;
		this.enchantPointLore = enchantPointLore;
		this.protectDeadLore = protectDeadLore;
		this.protectDestroyLore = protectDestroyLore;
		this.loreStyle = loreStyle;
		this.loreStyleWithMask = loreStyleWithMask;
		this.itemFlags = itemFlags;
		this.attributeTypeMap = attributeTypeMap;
		this.attributeSlotMap = attributeSlotMap;
	}

	public int getEnchantPoint() {
		return enchantPoint;
	}

	public String getVanillaEnchantLore() {
		return vanillaEnchantLore;
	}

	public String getCustomEnchantLore() {
		return customEnchantLore;
	}

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

	public List<ItemFlag> getItemFlags() {
		return itemFlags;
	}

	public Map<NMSAttributeType, String> getAttributeTypeMap() {
		return attributeTypeMap;
	}
	
	public Map<String, String> getAttributeSlotMap() {
		return attributeSlotMap;
	}
}
