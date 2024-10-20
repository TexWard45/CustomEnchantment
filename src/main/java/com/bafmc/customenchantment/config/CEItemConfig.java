package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.enchant.CESimple;
import com.bafmc.customenchantment.item.*;
import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CEItemConfig extends AbstractConfig {

	protected void loadConfig() {
		loadWeaponSettingsMap();
		loadGemSettings();
		loadCEBookStorage();
		loadCEProtectDeadStorage();
		loadCERemoveProtectDeadStorage();
        loadCERemoveEnchantPointStorage();
		loadCEProtectDestroyStorage();
		loadCENameTagStorage();
		loadCEEnchantPointStorage();
		loadCEIncreaseRateBookStorage();
		loadCERandomBookStorage();
		loadCERemoveEnchantStorage();
		loadCEEraseEnchantStorage();
		loadCEMaskStorage();
		loadCEWeaponStorage();
		loadCEArtifactStorage();
		loadCEBannerStorage();
        loadCELoreFormatStorage();
		loadCEGemStorage();
	}

	public void loadWeaponSettingsMap() {
		WeaponSettings
				.setSettingsMap(loadWeaponSettingsSection(config.getAdvancedConfigurationSection("weapon-settings")));
	}

	public Map<String, WeaponSettings> loadWeaponSettingsSection(AdvancedConfigurationSection config) {
		Map<String, WeaponSettings> map = new ConcurrentHashMap<String, WeaponSettings>();

		for (String key : config.getKeys(false)) {
			try {
				String template = config.getString(key + ".template");
				Validate.notNull(template, "Please set the template for the weapon settings: " + key);

				map.put(key, loadWeaponSettings(config.getAdvancedConfigurationSection(key), config.getAdvancedConfigurationSection(template)));
			}catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		return map;
	}

	public WeaponSettings loadWeaponSettings(AdvancedConfigurationSection config, AdvancedConfigurationSection alternativeConfig) {
		int enchantPoint = config.getInt("custom-enchant.default-point", alternativeConfig.getInt("custom-enchant.default-point"));
		Map<MaterialList, Integer> gemPointMap = loadGemPointMap(config.getAdvancedConfigurationSection("gem.default-point"), alternativeConfig.getAdvancedConfigurationSection("gem.default-point"));
		String vanillaEnchantLore = config.getStringColor("vanilla-enchant.default-lore", alternativeConfig.getString("vanilla-enchant.default-lore"));
		String customEnchantLore = config.getStringColor("custom-enchant.default-lore", alternativeConfig.getString("custom-enchant.default-lore"));
		SparseMap<String> enchantPointLore = getWeaponExtensionLore(
				config.getAdvancedConfigurationSection("custom-enchant.extra-lore"),
				alternativeConfig.getAdvancedConfigurationSection("custom-enchant.extra-lore"));
		SparseMap<String> protectDeadLore = getWeaponExtensionLore(
				config.getAdvancedConfigurationSection("protect-dead.extra-lore"),
				alternativeConfig.getAdvancedConfigurationSection("protect-dead.extra-lore"));
		SparseMap<String> protectDestroyLore = getWeaponExtensionLore(
				config.getAdvancedConfigurationSection("protect-destroy.extra-lore"),
				alternativeConfig.getAdvancedConfigurationSection("protect-destroy.extra-lore"));

		List<String> loreStyle = config.getStringColorList("lore-style", alternativeConfig.getStringList("lore-style"));
		List<String> loreStyleWithMask = config.getStringColorList("lore-style-with-mask", alternativeConfig.getStringList("lore-style-with-mask"));
		List<ItemFlag> itemFlags = EnumUtils.getEnumListByStringList(ItemFlag.class, config.getStringList("item-flag", alternativeConfig.getStringList("item-flag")));
		Map<NMSAttributeType, String> attributeTypeMap = getAttributeLoreMap(
				config.getAdvancedConfigurationSection("attribute-lore.type"),
				alternativeConfig.getAdvancedConfigurationSection("attribute-lore.type"));
		Map<String, String> attributeSlotMap = getAttributeSlotMap(
				config.getAdvancedConfigurationSection("attribute-lore.slot"),
				alternativeConfig.getAdvancedConfigurationSection("attribute-lore.slot"));

		return WeaponSettings.builder()
				.enchantPoint(enchantPoint)
				.vanillaEnchantLore(vanillaEnchantLore)
				.customEnchantLore(customEnchantLore)
				.enchantPointLore(enchantPointLore)
				.protectDeadLore(protectDeadLore)
				.protectDestroyLore(protectDestroyLore)
				.loreStyle(loreStyle)
				.loreStyleWithMask(loreStyleWithMask)
				.itemFlags(itemFlags)
				.attributeTypeMap(attributeTypeMap)
				.attributeSlotMap(attributeSlotMap)
				.gemPointMap(gemPointMap)
				.build();
	}

	public Map<MaterialList, Integer> loadGemPointMap(AdvancedConfigurationSection config, AdvancedConfigurationSection alternativeConfig) {
		Map<MaterialList, Integer> map = new HashMap<>();

		Set<String> keys = config.getKeys(false);
		if (keys.isEmpty()) {
			keys = alternativeConfig.getKeys(false);
		}

		for (String key : keys) {
			try {
				MaterialList materialList = MaterialList.getMaterialList(key);
				int point = config.getInt(key, alternativeConfig.getInt(key));

				map.put(materialList, point);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		return map;
	}

	public Map<NMSAttributeType, String> getAttributeLoreMap(AdvancedConfigurationSection config, AdvancedConfigurationSection alternativeConfig) {
		LinkedHashMap<NMSAttributeType, String> map = new LinkedHashMap<NMSAttributeType, String>();

		Set<String> keys = config.getKeys(false);
		if (keys.isEmpty()) {
			keys = alternativeConfig.getKeys(false);
		}

		for (String key : keys) {
			NMSAttributeType attributeType = NMSAttributeType.valueOf(key);

			if (attributeType == null) {
				continue;
			}

			map.put(attributeType, config.getStringColor(key, alternativeConfig.getStringColor(key)));
		}
		return map;
	}

	public Map<String, String> getAttributeSlotMap(AdvancedConfigurationSection config, AdvancedConfigurationSection alternativeConfig) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();

		Set<String> keys = config.getKeys(false);
		if (keys.isEmpty()) {
			keys = alternativeConfig.getKeys(false);
		}

		for (String key : keys) {
			String attributeType = String.valueOf(key);

			if (attributeType == null) {
				continue;
			}

			map.put(attributeType, config.getStringColor(key, alternativeConfig.getStringColor(key)));
		}
		return map;
	}

	public SparseMap<String> getWeaponExtensionLore(AdvancedConfigurationSection config, AdvancedConfigurationSection alternativeConfig) {
		SparseMap<String> list = new SparseMap<String>();

		Set<String> keys = config.getKeys(false);
		if (keys.isEmpty()) {
			keys = alternativeConfig.getKeys(false);
		}

		for (String key : keys) {
			try {
				list.put(Integer.valueOf(key), config.getStringColor(key, alternativeConfig.getStringColor(key)));
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

    public void loadCELoreFormatStorage() {
        CELoreFormatStorage storage = new CELoreFormatStorage();
        CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.LORE_FORMAT, storage);

        for (String pattern : config.getKeySection("lore-format", false)) {
            String path = "lore-format." + pattern;

            ItemStack itemStack = config.getItemStack(path + ".item");
            CELoreFormat ceItem = new CELoreFormat(itemStack);
            CELoreFormatData data = new CELoreFormatData();
            data.setPattern(pattern);
            data.setType(config.getString(path + ".lore-format-type"));
            ceItem.setData(data);

            storage.put(pattern, ceItem);
        }
    }

    public void loadCERemoveEnchantPointStorage() {
        CERemoveEnchantPointStorage storage = new CERemoveEnchantPointStorage();
        CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.REMOVE_ENCHANT_POINT, storage);

        for (String pattern : config.getKeySection("remove-enchant-point", false)) {
            String path = "remove-enchant-point." + pattern;

            ItemStack itemStack = config.getItemStack(path + ".item");

            CERemoveEnchantPoint ceItem = new CERemoveEnchantPoint(itemStack);
            CERemoveEnchantPointData data = new CERemoveEnchantPointData();

            List<CERemoveEnchantPointData.Data> dataList = new ArrayList<>();
            data.setPattern(pattern);

            for (String key : config.getKeySection(path + ".list", false)) {
                String keyPath = path + ".list." + key;

                CERemoveEnchantPointData.Data listData = new CERemoveEnchantPointData.Data();
                listData.setEnchantPointType(config.getString(keyPath + ".enchant-point-type"));
                listData.setAppliesMaterialList(MaterialList.getMaterialList(config.getStringList(keyPath + ".applies")));
                listData.setExtraPointRequired(config.getInt(keyPath + ".extra-point-required"));

                dataList.add(listData);
            }

            data.setDataList(dataList);

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

			ItemStack itemStack = config.getItemStack(path + ".item", true, true);
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

			ItemStack itemStack = config.getItemStack(path + ".item", true, true);
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

	public void loadCEArtifactStorage() {
		CEArtifactStorage storage = new CEArtifactStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.ARTIFACT, storage);

		for (String pattern : config.getKeySection("artifact", false)) {
			String path = "artifact." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item", true, true);
			CEArtifact ceItem = new CEArtifact(itemStack);
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

			CEArtifactData data = new CEArtifactData();
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

	public void loadGemSettings() {
		CEGemSettings.setSettings(loadGemSettings(config.getAdvancedConfigurationSection("gem-settings")));
	}

	public CEGemSettings loadGemSettings(AdvancedConfigurationSection config) {
		Map<Integer, CEGemSettings.GemLevelSettings> map = new HashMap<>();

		for (String key : config.getKeySection("levels", false)) {
			try {
				int level = Integer.parseInt(key);
				String color = config.getString("levels." + key + ".color");

				CEGemSettings.GemLevelSettings settings = new CEGemSettings.GemLevelSettings(color);
				map.put(level, settings);
			} catch (Exception e) {
				e.printStackTrace();
            }
		}

		return new CEGemSettings(map);
	}

	public void loadCEGemStorage() {
		CEGemStorage storage = new CEGemStorage();
		CustomEnchantment.instance().getCEItemStorageMap().put(CEItemType.GEM, storage);

		for (String pattern : config.getKeySection("gem", false)) {
			String path = "gem." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item", false, false);
			CEGem ceItem = new CEGem(itemStack);
			CEGemData data = new CEGemData();
			data.setPattern(pattern);
			data.setDisplay(config.getString(path + ".display"));
			data.setAppliesMaterialList(MaterialList.getMaterialList(config.getStringList("applies")));
			data.setAppliesDescription(config.getStringList(path + ".applies-description"));
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}
}
