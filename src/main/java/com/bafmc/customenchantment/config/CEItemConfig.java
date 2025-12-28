package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.bafframework.utils.MaterialUtils;
import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.feature.placeholder.PlaceholderBuilder;
import com.bafmc.bukkit.utils.EnumUtils;
import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.bukkit.utils.StringUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.config.item.CEOutfitConfig;
import com.bafmc.customenchantment.config.item.CESkinConfig;
import com.bafmc.customenchantment.config.item.CEWeaponConfig;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.item.CEItemType;
import com.bafmc.customenchantment.item.CEWeaponStorage;
import com.bafmc.customenchantment.item.WeaponSettings;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.artifact.CEArtifactData;
import com.bafmc.customenchantment.item.artifact.CEArtifactGroup;
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
import com.bafmc.customenchantment.item.outfit.CEOutfit;
import com.bafmc.customenchantment.item.outfit.CEOutfitData;
import com.bafmc.customenchantment.item.outfit.CEOutfitGroup;
import com.bafmc.customenchantment.item.outfit.CEOutfitStorage;
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
import com.bafmc.customenchantment.item.sigil.CESigil;
import com.bafmc.customenchantment.item.sigil.CESigilData;
import com.bafmc.customenchantment.item.sigil.CESigilGroup;
import com.bafmc.customenchantment.item.sigil.CESigilStorage;
import com.bafmc.customenchantment.item.skin.CESkin;
import com.bafmc.customenchantment.item.skin.CESkinData;
import com.bafmc.customenchantment.item.skin.CESkinStorage;
import com.bafmc.customenchantment.utils.AttributeUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
		loadCESigilStorage();
		loadCESkinStorage();
		loadCEOutfitStorage();
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

				WeaponSettings settings = loadWeaponSettings(config.getAdvancedConfigurationSection(key), config.getAdvancedConfigurationSection(template));

				settings.setKey(key);

				map.put(key, settings);
			}catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		return map;
	}

	public WeaponSettings loadWeaponSettings(AdvancedConfigurationSection config, AdvancedConfigurationSection alternativeConfig) {
		int enchantPoint = config.getInt("custom-enchant.default-point", alternativeConfig.getInt("custom-enchant.default-point"));
		String customGemLore = config.getStringColor("gem.default-lore", alternativeConfig.getString("gem.default-lore"));
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
				.customGemLore(customGemLore)
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
			String slot = String.valueOf(key);

			if (slot == null) {
				continue;
			}

			map.put(slot, config.getStringColor(key, alternativeConfig.getStringColor(key)));
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
			data.setAdvancedMode(config.getBoolean(path + ".advanced-mode"));
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

		CEWeaponConfig weaponConfig = new CEWeaponConfig(storage);
		weaponConfig.loadConfig(config);

		File folder = CustomEnchantment.instance().getWeaponFolder();
		weaponConfig.loadConfig(folder);
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

			String group = config.getStringColor(path + ".group");
			String enchant = config.getStringColor(path + ".enchant");

			CEArtifactGroup artifactGroup = CustomEnchantment.instance().getCeArtifactGroupMap().get(group);

			ItemStack itemStack = config.getItemStack(path + ".item", true, true);
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (artifactGroup.getItemDisplay() != null) {
				PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
				placeholderBuilder.put("{item_display}", itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "");

				String itemDisplay = artifactGroup.getItemDisplay();
				itemMeta.setDisplayName(placeholderBuilder.build().apply(itemDisplay));
			}

			if (artifactGroup.getItemLore() != null) {
				PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
				placeholderBuilder.put("{item_lore}", itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>());
				itemMeta.setLore(placeholderBuilder.build().apply(artifactGroup.getItemLore()));
			}
			itemStack.setItemMeta(itemMeta);

			CEArtifact ceItem = new CEArtifact(itemStack);

			for (String attributeFormat : config.getStringList(path + ".attributes")) {
				ceItem.getWeaponAttribute().addAttribute(attributeFormat);
			}

			int maxLevel = config.getInt(path + ".max-level");

			CEArtifactData.ConfigData configData = new CEArtifactData.ConfigData(group, enchant, maxLevel, itemMeta.getDisplayName(), new ArrayList<>(itemMeta.getLore()));

			CEArtifactData data = new CEArtifactData(pattern, configData);
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

	public SparseMap<String> loadLevels(AdvancedConfigurationSection mainConfig, AdvancedConfigurationSection settingsConfig) {
		SparseMap<String> map = new SparseMap<>();

		AdvancedConfigurationSection config = null;
		if (mainConfig.isSet("levels")) {
			config = mainConfig.getAdvancedConfigurationSection("levels");
		}else if (settingsConfig.isSet("levels")) {
			config = settingsConfig.getAdvancedConfigurationSection("levels");
		}

		if (config != null) {
			for (String levelKey : config.getKeys(false)) {
				try {
					map.put(Integer.valueOf(levelKey), config.getString(levelKey + ".color"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return map;
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
			Map<Integer, CEGemData.ConfigByLevelData> attributeMap = loadGemConfigByLevelMap(config.getAdvancedConfigurationSection(path + ".levels"));
			int limitPerItem = config.getInt(path + ".limit-per-item");

			ItemMeta itemMeta = itemStack.getItemMeta();
			String displayName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : MaterialUtils.getDisplayName(itemStack.getType());
			List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();

			CEGemData.ConfigData configData = CEGemData.ConfigData.builder()
					.display(display)
					.appliesMaterialList(appliesMaterialList)
					.appliesDescription(appliesDescription)
					.appliesSlot(appliesSlot)
					.levelMap(attributeMap)
					.limitPerItem(limitPerItem)
					.itemDisplay(displayName)
					.itemLore(lore).build();
			CEGemData data = new CEGemData(pattern, configData);
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public Map<Integer, CEGemData.ConfigByLevelData> loadGemConfigByLevelMap(AdvancedConfigurationSection config) {
		Map<Integer, CEGemData.ConfigByLevelData> map = new HashMap<>();

		for (String key : config.getKeys(false)) {
			try {
				int level = Integer.parseInt(key);

				List<NMSAttribute> list = new ArrayList<>();

				for (String attributeFormat : config.getStringList(key + ".attributes")) {
					NMSAttribute attribute = AttributeUtils.createAttribute(attributeFormat, "gem-");
					list.add(attribute);
				}

				int customModelData = config.getInt(key + ".custom-model-data", 0);

				CEGemData.ConfigByLevelData configByLevelData = CEGemData.ConfigByLevelData.builder()
						.nmsAttributes(list)
						.customModelData(customModelData)
						.build();

				map.put(level, configByLevelData);
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
			Map<Integer, Double> slotChance = new LinkedHashMap<>();
			for (String key : config.getKeySection(path + ".slot-chance", false)) {
				slotChance.put(Integer.parseInt(key), config.getDouble(path + ".slot-chance." + key));
			}

			CEGemDrillData.ConfigData configData = new CEGemDrillData.ConfigData(maxDrill, applies, slot, slotChance);
			CEGemDrillData data = new CEGemDrillData(pattern, configData);
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public void loadCESigilStorage() {
		CESigilStorage storage = new CESigilStorage();
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.SIGIL, storage);

		for (String pattern : config.getKeySection("sigil", false)) {
			String path = "sigil." + pattern;

			String group = config.getStringColor(path + ".group");
			String enchant = config.getStringColor(path + ".enchant");

			CESigilGroup sigilGroup = CustomEnchantment.instance().getCeSigilGroupMap().get(group);

			ItemStack itemStack = config.getItemStack(path + ".item", true, true);
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (sigilGroup.getItemDisplay() != null) {
				PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
				placeholderBuilder.put("{item_display}", itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : "");

				String itemDisplay = sigilGroup.getItemDisplay();
				itemMeta.setDisplayName(placeholderBuilder.build().apply(itemDisplay));
			}

			if (sigilGroup.getItemLore() != null) {
				PlaceholderBuilder placeholderBuilder = PlaceholderBuilder.builder();
				placeholderBuilder.put("{item_lore}", itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>());
				itemMeta.setLore(placeholderBuilder.build().apply(sigilGroup.getItemLore()));
			}
			itemStack.setItemMeta(itemMeta);

			CESigil ceItem = new CESigil(itemStack);

			for (String attributeFormat : config.getStringList(path + ".attributes")) {
				ceItem.getWeaponAttribute().addAttribute(attributeFormat);
			}

			int maxLevel = config.getInt(path + ".max-level");

			List<CESigilData.SpecialDisplayData> specialDisplayDataList = loadCESigilSpecialDisplayData(config.getAdvancedConfigurationSection(path + ".special-display"));

			CESigilData.ConfigData configData = new CESigilData.ConfigData(group, enchant, maxLevel, itemMeta.getDisplayName(), new ArrayList<>(itemMeta.getLore()), specialDisplayDataList);

			CESigilData data = new CESigilData(pattern, configData);
			ceItem.setData(data);

			storage.put(pattern, ceItem);
		}
	}

	public List<CESigilData.SpecialDisplayData> loadCESigilSpecialDisplayData(AdvancedConfigurationSection config) {
		List<CESigilData.SpecialDisplayData> list = new ArrayList<>();

		for (String key : config.getKeys(false)) {
			try {
				MaterialList materialList = MaterialList.getMaterialList(config.getStringList(key + ".type"));
				String display = config.getString(key + ".display");

				CESigilData.SpecialDisplayData data = new CESigilData.SpecialDisplayData(materialList, display);
				list.add(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	public void loadCEOutfitStorage() {
		CEOutfitStorage storage = new CEOutfitStorage();
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.OUTFIT, storage);

		CEOutfitConfig outfitConfig = new CEOutfitConfig(storage);
		outfitConfig.loadConfig(config);

		File folder = CustomEnchantment.instance().getOutfitFolder();
		outfitConfig.loadConfig(folder);
	}

	public void loadCESkinStorage() {
		CESkinStorage storage = new CESkinStorage();
		CustomEnchantment.instance().getCeItemStorageMap().put(CEItemType.SKIN, storage);

		CESkinConfig skinConfig = new CESkinConfig(storage);
		skinConfig.loadConfig(config);

		File folder = CustomEnchantment.instance().getSkinFolder();
		skinConfig.loadConfig(folder);
	}
}
