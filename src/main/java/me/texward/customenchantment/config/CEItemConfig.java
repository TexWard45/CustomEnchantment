package me.texward.customenchantment.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import me.texward.customenchantment.CustomEnchantment;
import me.texward.customenchantment.api.MaterialList;
import me.texward.customenchantment.enchant.CESimple;
import me.texward.customenchantment.item.CEBanner;
import me.texward.customenchantment.item.CEBannerData;
import me.texward.customenchantment.item.CEBannerStorage;
import me.texward.customenchantment.item.CEBook;
import me.texward.customenchantment.item.CEBookStorage;
import me.texward.customenchantment.item.CEEnchantPoint;
import me.texward.customenchantment.item.CEEnchantPointData;
import me.texward.customenchantment.item.CEEnchantPointStorage;
import me.texward.customenchantment.item.CEEraseEnchant;
import me.texward.customenchantment.item.CEEraseEnchantData;
import me.texward.customenchantment.item.CEEraseEnchantStorage;
import me.texward.customenchantment.item.CEIncreaseRateBook;
import me.texward.customenchantment.item.CEIncreaseRateBookData;
import me.texward.customenchantment.item.CEIncreaseRateBookStorage;
import me.texward.customenchantment.item.CEItemType;
import me.texward.customenchantment.item.CEMask;
import me.texward.customenchantment.item.CEMaskData;
import me.texward.customenchantment.item.CEMaskStorage;
import me.texward.customenchantment.item.CENameTag;
import me.texward.customenchantment.item.CENameTagData;
import me.texward.customenchantment.item.CENameTagStorage;
import me.texward.customenchantment.item.CEProtectDead;
import me.texward.customenchantment.item.CEProtectDeadData;
import me.texward.customenchantment.item.CEProtectDeadStorage;
import me.texward.customenchantment.item.CEProtectDestroy;
import me.texward.customenchantment.item.CEProtectDestroyData;
import me.texward.customenchantment.item.CEProtectDestroyStorage;
import me.texward.customenchantment.item.CERandomBook;
import me.texward.customenchantment.item.CERandomBookData;
import me.texward.customenchantment.item.CERandomBookFilter;
import me.texward.customenchantment.item.CERandomBookStorage;
import me.texward.customenchantment.item.CERemoveEnchant;
import me.texward.customenchantment.item.CERemoveEnchantData;
import me.texward.customenchantment.item.CERemoveEnchantStorage;
import me.texward.customenchantment.item.CERemoveProtectDead;
import me.texward.customenchantment.item.CERemoveProtectDeadData;
import me.texward.customenchantment.item.CERemoveProtectDeadStorage;
import me.texward.customenchantment.item.CEWeapon;
import me.texward.customenchantment.item.CEWeaponData;
import me.texward.customenchantment.item.CEWeaponStorage;
import me.texward.customenchantment.item.WeaponSettings;
import me.texward.texwardlib.configuration.AbstractConfig;
import me.texward.texwardlib.configuration.AdvancedConfigurationSection;
import me.texward.texwardlib.util.EnumUtils;
import me.texward.texwardlib.util.SparseMap;
import me.texward.texwardlib.util.StringUtils;
import me.texward.texwardlib.util.nms.NMSAttributeType;

public class CEItemConfig extends AbstractConfig {

	protected void loadConfig() {
		loadWeaponSettingsMap();
		loadCEBookStorage();
		loadCEProtectDeadStorage();
		loadCERemoveProtectDeadStorage();
		loadCEProtectDestroyStorage();
		loadCENameTagStorage();
		loadCEEnchantPointStorage();
		loadCEIncreaseRateBookStorage();
		loadCERandomBookStorage();
		loadCERemoveEnchantStorage();
		loadCEEraseEnchantStorage();
		loadCEMaskStorage();
		loadCEWeaponStorage();
		loadCEBannerStorage();
	}

	public void loadWeaponSettingsMap() {
		WeaponSettings
				.setSettingsMap(loadWeaponSettingsSection(config.getAdvancedConfigurationSection("weapon-settings")));
	}

	public Map<String, WeaponSettings> loadWeaponSettingsSection(AdvancedConfigurationSection config) {
		Map<String, WeaponSettings> map = new ConcurrentHashMap<String, WeaponSettings>();

		for (String key : config.getKeys(false)) {
			map.put(key, loadWeaponSettings(config.getAdvancedConfigurationSection(key)));
		}

		return map;
	}

	public WeaponSettings loadWeaponSettings(AdvancedConfigurationSection config) {
		int enchantPoint = config.getInt("custom-enchant.default-point");
		String vanillaEnchantLore = config.getStringColor("vanilla-enchant.default-lore");
		String customEnchantLore = config.getStringColor("custom-enchant.default-lore");
		SparseMap<String> enchantPointLore = getWeaponExtensionLore(
				config.getAdvancedConfigurationSection("custom-enchant.extra-lore"));
		SparseMap<String> protectDeadLore = getWeaponExtensionLore(
				config.getAdvancedConfigurationSection("protect-dead.extra-lore"));
		SparseMap<String> protectDestroyLore = getWeaponExtensionLore(
				config.getAdvancedConfigurationSection("protect-destroy.extra-lore"));
		;
		List<String> loreStyle = config.getStringColorList("lore-style");
		List<String> loreStyleWithMask = config.getStringColorList("lore-style-with-mask");
		List<ItemFlag> itemFlags = EnumUtils.getEnumListByStringList(ItemFlag.class, config.getStringList("item-flag"));
		Map<NMSAttributeType, String> attributeTypeMap = getAttributeLoreMap(
				config.getAdvancedConfigurationSection("attribute-lore.type"));
		Map<String, String> attributeSlotMap = getAttributeSlotMap(
				config.getAdvancedConfigurationSection("attribute-lore.slot"));

		return new WeaponSettings(enchantPoint, vanillaEnchantLore, customEnchantLore, enchantPointLore,
				protectDeadLore, protectDestroyLore, loreStyle, loreStyleWithMask, itemFlags, attributeTypeMap,
				attributeSlotMap);
	}

	public Map<NMSAttributeType, String> getAttributeLoreMap(AdvancedConfigurationSection config) {
		LinkedHashMap<NMSAttributeType, String> map = new LinkedHashMap<NMSAttributeType, String>();

		for (String key : config.getKeys(false)) {
			NMSAttributeType attributeType = NMSAttributeType.valueOf(key);

			if (attributeType == null) {
				continue;
			}

			map.put(attributeType, config.getStringColor(key));
		}
		return map;
	}

	public Map<String, String> getAttributeSlotMap(AdvancedConfigurationSection config) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (String key : config.getKeys(false)) {
			String attributeType = String.valueOf(key);

			if (attributeType == null) {
				continue;
			}

			map.put(attributeType, config.getStringColor(key));
		}
		return map;
	}

	public SparseMap<String> getWeaponExtensionLore(AdvancedConfigurationSection config) {
		SparseMap<String> list = new SparseMap<String>();

		for (String key : config.getKeys(false)) {
			try {
				list.put(Integer.valueOf(key), config.getStringColor(key));
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		return list;
	}

	public void loadCEBookStorage() {
		CEBookStorage storage = new CEBookStorage();

		for (String groupName : config.getKeySection("default-book", false)) {
			for (String levelStr : config.getKeySection("default-book." + groupName, false)) {
				String path = "default-book." + groupName + "." + levelStr;

				ItemStack itemStack = config.getItemStack(path + ".item");
				CEBook ceBook = new CEBook(itemStack);

				storage.put(groupName + "-" + levelStr, ceBook);
			}
		}

		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.BOOK, storage);
	}

	public void loadCEProtectDeadStorage() {
		CEProtectDeadStorage storage = new CEProtectDeadStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.PROTECT_DEAD, storage);

		for (String pattern : config.getKeySection("protect-dead", false)) {
			String path = "protect-dead." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CEProtectDead ceItem = new CEProtectDead(itemStack);
			CEProtectDeadData data = new CEProtectDeadData();
			data.setPattern(pattern);
			data.setExtraPoint(config.getInt(path + ".extra-point"));
			data.setMaxPoint(config.getInt(path + ".max-point"));
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}
	
	public void loadCERemoveProtectDeadStorage() {
		CERemoveProtectDeadStorage storage = new CERemoveProtectDeadStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.REMOVE_PROTECT_DEAD, storage);

		for (String pattern : config.getKeySection("remove-protect-dead", false)) {
			String path = "remove-protect-dead." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CERemoveProtectDead ceItem = new CERemoveProtectDead(itemStack);
			CERemoveProtectDeadData data = new CERemoveProtectDeadData();
			data.setPattern(pattern);
			data.setProtectDeadType(config.getString(path + ".protect-dead-type"));
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public void loadCEProtectDestroyStorage() {
		CEProtectDestroyStorage storage = new CEProtectDestroyStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.PROTECT_DESTROY, storage);

		for (String pattern : config.getKeySection("protect-destroy", false)) {
			String path = "protect-destroy." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CEProtectDestroy ceItem = new CEProtectDestroy(itemStack);
			CEProtectDestroyData data = new CEProtectDestroyData();
			data.setPattern(pattern);
			data.setExtraPoint(config.getInt(path + ".extra-point"));
			data.setMaxPoint(config.getInt(path + ".max-point"));
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public void loadCENameTagStorage() {
		CENameTagStorage storage = new CENameTagStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.NAME_TAG, storage);

		for (String pattern : config.getKeySection("name-tag", false)) {
			String path = "name-tag." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CENameTag ceItem = new CENameTag(itemStack);
			CENameTagData data = new CENameTagData();
			data.setPattern(pattern);

			List<Character> colorCharacterEnableList = new ArrayList<Character>();
			for (String s : StringUtils.split(config.getString(path + ".color-character-enable"), ",", 0)) {
				colorCharacterEnableList.add(s.charAt(0));
			}
			data.setColorCharacterEnableList(colorCharacterEnableList);
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public void loadCEEnchantPointStorage() {
		CEEnchantPointStorage storage = new CEEnchantPointStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.ENCHANT_POINT, storage);

		for (String pattern : config.getKeySection("enchant-point", false)) {
			String path = "enchant-point." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CEEnchantPoint ceItem = new CEEnchantPoint(itemStack);
			CEEnchantPointData data = new CEEnchantPointData();
			data.setPattern(pattern);
			data.setExtraPoint(config.getInt(path + ".extra-point"));
			data.setMaxPoint(config.getInt(path + ".max-point"));
			data.setApplies(MaterialList.getMaterialList(config.getStringList(path + ".applies")));
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public void loadCEIncreaseRateBookStorage() {
		CEIncreaseRateBookStorage storage = new CEIncreaseRateBookStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.INCREASE_RATE_BOOK, storage);

		for (String pattern : config.getKeySection("increase-rate-book", false)) {
			String path = "increase-rate-book." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CEIncreaseRateBook ceItem = new CEIncreaseRateBook(itemStack);
			CEIncreaseRateBookData data = new CEIncreaseRateBookData();
			data.setPattern(pattern);
			data.setGroups(config.getStringList(path + ".groups"));
			data.setSuccess(config.getInt(path + ".success", 100));
			data.setDestroy(config.getInt(path + ".destroy", 0));
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public void loadCERandomBookStorage() {
		CERandomBookStorage storage = new CERandomBookStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.RANDOM_BOOK, storage);

		for (String pattern : config.getKeySection("random-book", false)) {
			String path = "random-book." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CERandomBook ceItem = new CERandomBook(itemStack);
			CERandomBookData data = new CERandomBookData();
			data.setPattern(pattern);

			CERandomBookFilter filter = new CERandomBookFilter();
			filter.parse(config.getStringList(path + ".expressions"));
			data.setFilter(filter);
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public void loadCERemoveEnchantStorage() {
		CERemoveEnchantStorage storage = new CERemoveEnchantStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.REMOVE_ENCHANT, storage);

		for (String pattern : config.getKeySection("remove-enchant", false)) {
			String path = "remove-enchant." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CERemoveEnchant ceItem = new CERemoveEnchant(itemStack);
			CERemoveEnchantData data = new CERemoveEnchantData();
			data.setPattern(pattern);
			data.setGroups(config.getStringList(path + ".groups"));
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}
	
	public void loadCEEraseEnchantStorage() {
		CEEraseEnchantStorage storage = new CEEraseEnchantStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.EARSE_ENCHANT, storage);

		for (String pattern : config.getKeySection("erase-enchant", false)) {
			String path = "erase-enchant." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CEEraseEnchant ceItem = new CEEraseEnchant(itemStack);
			CEEraseEnchantData data = new CEEraseEnchantData();
			data.setPattern(pattern);
			data.setBlacklistEnchantments(config.getStringList(path + ".blacklist-enchants"));
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public void loadCEMaskStorage() {
		CEMaskStorage storage = new CEMaskStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.MASK, storage);

		for (String pattern : config.getKeySection("mask", false)) {
			String path = "mask." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item", true);
			CEMask ceItem = new CEMask(itemStack);
			for (String enchantFormat : config.getStringList(path + ".enchants")) {
				String enchantName = null;
				int level = 1;

				int spaceIndex = enchantFormat.indexOf(" ");
				if (spaceIndex != -1) {
					enchantName = enchantFormat.substring(0, spaceIndex);
					level = Integer.parseInt(enchantFormat.substring(spaceIndex + 1, enchantFormat.length()));
				} else {
					enchantName = enchantFormat;
				}

				ceItem.getWeaponEnchant().addCESimple(new CESimple(enchantName, level));
			}

			String normalDisplay = config.getStringColor(path + ".display.normal");
			String boldDisplay = config.getStringColor(path + ".display.bold");

			CEMaskData data = new CEMaskData();
			data.setNormalDisplay(normalDisplay);
			data.setBoldDisplay(boldDisplay);
			data.setPattern(pattern);
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}
	
	public void loadCEWeaponStorage() {
		CEWeaponStorage storage = new CEWeaponStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.WEAPON, storage);

		for (String pattern : config.getKeySection("weapon", false)) {
			String path = "weapon." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item", true);
			CEWeapon ceItem = new CEWeapon(itemStack);
			for (String enchantFormat : config.getStringList(path + ".enchants")) {
				String enchantName = null;
				int level = 1;

				int spaceIndex = enchantFormat.indexOf(" ");
				if (spaceIndex != -1) {
					enchantName = enchantFormat.substring(0, spaceIndex);
					level = Integer.parseInt(enchantFormat.substring(spaceIndex + 1, enchantFormat.length()));
				} else {
					enchantName = enchantFormat;
				}

				ceItem.getWeaponEnchant().addCESimple(new CESimple(enchantName, level));
			}
			
			for (String attributeFormat : config.getStringList(path + ".attributes")) {
				ceItem.getWeaponAttribute().addAttribute(attributeFormat);
			}

			CEWeaponData data = new CEWeaponData();
			data.setPattern(pattern);
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public void loadCEBannerStorage() {
		CEBannerStorage storage = new CEBannerStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.BANNER, storage);

		for (String pattern : config.getKeySection("banner", false)) {
			String path = "banner." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CEBanner ceItem = new CEBanner(itemStack);
			for (String enchantFormat : config.getStringList(path + ".enchants")) {
				String enchantName = null;
				int level = 1;

				int spaceIndex = enchantFormat.indexOf(" ");
				if (spaceIndex != -1) {
					enchantName = enchantFormat.substring(0, spaceIndex);
					level = Integer.parseInt(enchantFormat.substring(spaceIndex + 1, enchantFormat.length()));
				} else {
					enchantName = enchantFormat;
				}

				ceItem.getWeaponEnchant().addCESimple(new CESimple(enchantName, level));
			}

			String normalDisplay = config.getStringColor(path + ".display.normal");
			String boldDisplay = config.getStringColor(path + ".display.bold");

			CEBannerData data = new CEBannerData();
			data.setNormalDisplay(normalDisplay);
			data.setBoldDisplay(boldDisplay);
			data.setPattern(pattern);
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}
}
