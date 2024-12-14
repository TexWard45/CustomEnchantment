package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.*;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.artifact.CEArtifactData;
import com.bafmc.customenchantment.item.artifact.CEArtifactStorage;
import com.bafmc.customenchantment.item.banner.CEBanner;
import com.bafmc.customenchantment.item.banner.CEBannerData;
import com.bafmc.customenchantment.item.banner.CEBannerStorage;
import com.bafmc.customenchantment.item.book.CEBook;
import com.bafmc.customenchantment.item.book.CEBookStorage;
import com.bafmc.customenchantment.item.enchantpoint.CEEnchantPoint;
import com.bafmc.customenchantment.item.enchantpoint.CEEnchantPointData;
import com.bafmc.customenchantment.item.enchantpoint.CEEnchantPointStorage;
import com.bafmc.customenchantment.item.eraseenchant.CEEraseEnchant;
import com.bafmc.customenchantment.item.eraseenchant.CEEraseEnchantData;
import com.bafmc.customenchantment.item.eraseenchant.CEEraseEnchantStorage;
import com.bafmc.customenchantment.item.gem.CEGem;
import com.bafmc.customenchantment.item.gem.CEGemData;
import com.bafmc.customenchantment.item.gem.CEGemSettings;
import com.bafmc.customenchantment.item.gem.CEGemStorage;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrill;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrillData;
import com.bafmc.customenchantment.item.gemdrill.CEGemDrillStorage;
import com.bafmc.customenchantment.item.increaseratebook.CEIncreaseRateBook;
import com.bafmc.customenchantment.item.increaseratebook.CEIncreaseRateBookData;
import com.bafmc.customenchantment.item.increaseratebook.CEIncreaseRateBookStorage;
import com.bafmc.customenchantment.item.loreformat.CELoreFormat;
import com.bafmc.customenchantment.item.loreformat.CELoreFormatData;
import com.bafmc.customenchantment.item.loreformat.CELoreFormatStorage;
import com.bafmc.customenchantment.item.mask.CEMask;
import com.bafmc.customenchantment.item.mask.CEMaskData;
import com.bafmc.customenchantment.item.mask.CEMaskStorage;
import com.bafmc.customenchantment.item.nametag.CENameTag;
import com.bafmc.customenchantment.item.nametag.CENameTagData;
import com.bafmc.customenchantment.item.nametag.CENameTagStorage;
import com.bafmc.customenchantment.item.protectdead.CEProtectDead;
import com.bafmc.customenchantment.item.protectdead.CEProtectDeadData;
import com.bafmc.customenchantment.item.protectdead.CEProtectDeadStorage;
import com.bafmc.customenchantment.item.protectdestroy.CEProtectDestroy;
import com.bafmc.customenchantment.item.protectdestroy.CEProtectDestroyData;
import com.bafmc.customenchantment.item.protectdestroy.CEProtectDestroyStorage;
import com.bafmc.customenchantment.item.randombook.CERandomBook;
import com.bafmc.customenchantment.item.randombook.CERandomBookData;
import com.bafmc.customenchantment.item.randombook.CERandomBookFilter;
import com.bafmc.customenchantment.item.randombook.CERandomBookStorage;
import com.bafmc.customenchantment.item.removeenchant.CERemoveEnchant;
import com.bafmc.customenchantment.item.removeenchant.CERemoveEnchantData;
import com.bafmc.customenchantment.item.removeenchant.CERemoveEnchantStorage;
import com.bafmc.customenchantment.item.removeenchantpoint.CERemoveEnchantPoint;
import com.bafmc.customenchantment.item.removeenchantpoint.CERemoveEnchantPointData;
import com.bafmc.customenchantment.item.removeenchantpoint.CERemoveEnchantPointStorage;
import com.bafmc.customenchantment.item.removegem.CERemoveGem;
import com.bafmc.customenchantment.item.removegem.CERemoveGemData;
import com.bafmc.customenchantment.item.removegem.CERemoveGemStorage;
import com.bafmc.customenchantment.item.removeprotectdead.CERemoveProtectDead;
import com.bafmc.customenchantment.item.removeprotectdead.CERemoveProtectDeadData;
import com.bafmc.customenchantment.item.removeprotectdead.CERemoveProtectDeadStorage;
import com.bafmc.customenchantment.utils.AttributeUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
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
		loadCEGemDrillStorage();
		loadCERemoveGemStorage();
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

		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.BOOK, storage);
	}

	public void loadCEProtectDeadStorage() {
		CEProtectDeadStorage storage = new CEProtectDeadStorage();
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.PROTECT_DEAD, storage);

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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.REMOVE_PROTECT_DEAD, storage);

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
        CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.LORE_FORMAT, storage);

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
        CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.REMOVE_ENCHANT_POINT, storage);

        for (String pattern : config.getKeySection("remove-enchant-point", false)) {
            String path = "remove-enchant-point." + pattern;

            ItemStack itemStack = config.getItemStack(path + ".item");

            CERemoveEnchantPoint ceItem = new CERemoveEnchantPoint(itemStack);
            CERemoveEnchantPointData data = new CERemoveEnchantPointData();
            data.setPattern(pattern);

            ceItem.setData(data);
            storage.put(pattern, ceItem);
        }
    }

	public void loadCERemoveGemStorage() {
		CERemoveGemStorage storage = new CERemoveGemStorage();
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.REMOVE_GEM, storage);

		for (String pattern : config.getKeySection("remove-gem", false)) {
			String path = "remove-gem." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");

			CERemoveGem ceItem = new CERemoveGem(itemStack);
			CERemoveGemData data = new CERemoveGemData();
			data.setPattern(pattern);

			ceItem.setData(data);
			storage.put(pattern, ceItem);
		}
	}

	public void loadCEProtectDestroyStorage() {
		CEProtectDestroyStorage storage = new CEProtectDestroyStorage();
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.PROTECT_DESTROY, storage);

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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.NAME_TAG, storage);

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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.ENCHANT_POINT, storage);

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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.INCREASE_RATE_BOOK, storage);

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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.RANDOM_BOOK, storage);

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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.REMOVE_ENCHANT, storage);

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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.EARSE_ENCHANT, storage);

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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.MASK, storage);

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

				ceItem.getWeaponEnchant().addCESimple(new CEEnchantSimple(enchantName, level));
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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.WEAPON, storage);

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

				ceItem.getWeaponEnchant().addCESimple(new CEEnchantSimple(enchantName, level));
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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.ARTIFACT, storage);

		if (config.isSet("artifact")) {
			loadCEArtifactStorage(storage, config.getAdvancedConfigurationSection("artifact"));
		}

		for (File file : CustomEnchantment.instance().getArtifactFolder().listFiles()) {
			if (!file.getName().endsWith(".yml")) {
				continue;
			}

			AdvancedConfigurationSection config = new AdvancedFileConfiguration(file);
			loadCEArtifactStorage(storage, config);
		}
	}

	public void loadCEArtifactStorage(CEArtifactStorage storage, AdvancedConfigurationSection config) {
		for (String pattern : config.getKeySection("", false)) {
			String path = pattern;

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

				ceItem.getWeaponEnchant().addCESimple(new CEEnchantSimple(enchantName, level));
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
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.BANNER, storage);

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

				ceItem.getWeaponEnchant().addCESimple(new CEEnchantSimple(enchantName, level));
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
		Map<Integer, CEGemSettings.GemLevelSettings> gemLevelSettingsMap = new HashMap<>();
		Map<String, CEGemSettings.SlotSettings> slotSettingsMap = new HashMap<>();

		for (String key : config.getKeySection("levels", false)) {
			try {
				int level = Integer.parseInt(key);
				String color = config.getString("levels." + key + ".color");

				CEGemSettings.GemLevelSettings settings = new CEGemSettings.GemLevelSettings(color);
				gemLevelSettingsMap.put(level, settings);
			} catch (Exception e) {
				e.printStackTrace();
            }
		}

		for (String key : config.getKeySection("slot", false)) {
			try {
				String slot = key;
				int priority = config.getInt("slot." + key + ".priority");
				String display = config.getString("slot." + key + ".display");

				CEGemSettings.SlotSettings settings = new CEGemSettings.SlotSettings(priority, display);
				slotSettingsMap.put(slot, settings);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new CEGemSettings(gemLevelSettingsMap, slotSettingsMap);
	}

	public void loadCEGemStorage() {
		CEGemStorage storage = new CEGemStorage();
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.GEM, storage);

		for (String pattern : config.getKeySection("gem", false)) {
			String path = "gem." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item", false, false);
			CEGem ceItem = new CEGem(itemStack);

			String display = config.getStringColor(path + ".display");
			MaterialList appliesMaterialList = MaterialList.getMaterialList(config.getStringList(path + ".applies"));
			List<String> appliesDescription = config.getStringList(path + ".applies-description");
			List<String> appliesSlot = config.getStringList(path + ".applies-slot");
			Map<Integer, List<NMSAttribute>> attributeMap = loadGemAttributeMap(config.getAdvancedConfigurationSection(path + ".levels"));
			int limitPerItem = config.getInt(path + ".limit-per-item");

			CEGemData.ConfigData configData = new CEGemData.ConfigData(display, appliesMaterialList, appliesDescription, appliesSlot, attributeMap, limitPerItem);
			CEGemData data = new CEGemData(pattern, configData);
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public Map<Integer, List<NMSAttribute>> loadGemAttributeMap(AdvancedConfigurationSection config) {
		Map<Integer, List<NMSAttribute>> map = new HashMap<>();

		for (String key : config.getKeys(false)) {
			try {
				int level = Integer.parseInt(key);
				List<NMSAttribute> list = new ArrayList<>();

				for (String attributeFormat : config.getStringList(key + ".attributes")) {
					NMSAttribute attribute = AttributeUtils.createAttribute(attributeFormat, "gem-");
					list.add(attribute);
				}

				map.put(level, list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return map;
	}

	public void loadCEGemDrillStorage() {
		CEGemDrillStorage storage = new CEGemDrillStorage();
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.GEM_DRILL, storage);

		for (String pattern : config.getKeySection("gem-drill", false)) {
			String path = "gem-drill." + pattern;

			ItemStack itemStack = config.getItemStack(path + ".item");
			CEGemDrill ceItem = new CEGemDrill(itemStack);

			int maxDrill = config.getInt(path + ".max-drill");
			MaterialList applies = MaterialList.getMaterialList(config.getStringList(path + ".applies"));
			String slot = config.getString(path + ".slot");

			CEGemDrillData.ConfigData configData = new CEGemDrillData.ConfigData(maxDrill, applies, slot);
			CEGemDrillData data = new CEGemDrillData(pattern, configData);
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}
}
