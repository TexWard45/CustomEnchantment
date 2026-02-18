package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.feature.execute.Execute;
import com.bafmc.bukkit.feature.requirement.RequirementList;
import com.bafmc.bukkit.feature.requirement.RequirementManager;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.PathUtils;
import com.bafmc.bukkit.utils.RandomRangeInt;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeCustomMenu;
import com.bafmc.customenchantment.menu.bookupgrade.BookUpgradeSettings;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeData;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeLevelData;
import com.bafmc.customenchantment.menu.bookupgrade.data.RequiredXpGroup;
import com.bafmc.customenchantment.menu.bookupgrade.data.XpGroup;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BookUpgradeConfig extends AbstractConfig {
	protected void loadConfig() {
        if (BookUpgradeCustomMenu.getSettings() == null) {
            BookUpgradeSettings settings = new BookUpgradeSettings();
            BookUpgradeCustomMenu.setSettings(settings);

            Execute broadcastUpgradeSuccess = new Execute(config.getStringList("settings.broadcast-upgrade-success"));
            settings.setBroadcastUpgradeSuccessExecute(broadcastUpgradeSuccess);
        }

        BookUpgradeSettings settings = BookUpgradeCustomMenu.getSettings();

        Map<String, XpGroup> xpGroupMap = loadXpGroup(config);
        settings.putAllXpGroupMap(xpGroupMap);

        Map<String, RequiredXpGroup> requiredXpGroupMap = loadRequiredXpGroup(config);
        settings.putAllRequiredXpGroupMap(requiredXpGroupMap);

        Map<String, BookUpgradeLevelData> bookUpgradeLevelDataMap = loadBookUpgradeLevelData(config);
        settings.putAllBookUpgradeLevelDataMap(bookUpgradeLevelDataMap);
	}

    public Map<String, XpGroup> loadXpGroup(AdvancedConfigurationSection config) {
        Map<String, XpGroup> map = new LinkedHashMap<String, XpGroup>();

        String path = "settings.xp";

        for (String groupName : config.getKeySection(path, false)) {
            Set<String> levelList = config.getKeySection(PathUtils.of(path, groupName), false);

            Map<Integer, RandomRangeInt> xpMap = new LinkedHashMap<Integer, RandomRangeInt>();

            for (String level : levelList) {
                xpMap.put(Integer.valueOf(level), new RandomRangeInt(config.getString(PathUtils.of(path, groupName, level))));
            }

            XpGroup xpGroup = new XpGroup(xpMap);
            map.put(groupName, xpGroup);
        }

        return map;
    }

    public Map<String, RequiredXpGroup> loadRequiredXpGroup(AdvancedConfigurationSection config) {
        Map<String, RequiredXpGroup> map = new LinkedHashMap<String, RequiredXpGroup>();

        String path = "settings.required-xp";

        for (String groupName : config.getKeySection(path, false)) {
            Set<String> levelList = config.getKeySection(PathUtils.of(path, groupName), false);

            Map<Integer, Integer> requiredXpMap = new LinkedHashMap<>();

            for (String level : levelList) {
                requiredXpMap.put(Integer.valueOf(level), config.getInt(PathUtils.of(path, groupName, level)));
            }

            RequiredXpGroup requiredXpGroup = new RequiredXpGroup(requiredXpMap);
            map.put(groupName, requiredXpGroup);
        }

        return map;
    }

    public Map<String, BookUpgradeLevelData> loadBookUpgradeLevelData(AdvancedConfigurationSection config) {
        Map<String, BookUpgradeLevelData> map = new LinkedHashMap<>();

        String path = "list";

        for (String groupName : config.getKeySection(path, false)) {
            Set<String> levelList = config.getKeySection(PathUtils.of(path, groupName), false);

            Map<Integer, BookUpgradeData> levelMap = new LinkedHashMap<Integer, BookUpgradeData>();

            for (String level : levelList) {
                RandomRangeInt xp = null;

                int chance = config.getInt(PathUtils.of(path, groupName, level, "chance"), 100);
                Chance successChance = new Chance(chance);

                RandomRangeInt loseXpPercent = new RandomRangeInt(config.getString(PathUtils.of(path, groupName, level, "lose-xp-percent"), "0"));

                if (config.isSet(PathUtils.of(path, groupName, level, "xp"))) {
                    xp = new RandomRangeInt(config.getString(PathUtils.of(path, groupName, level, "xp")));
                }else {
                    xp = BookUpgradeCustomMenu.getSettings().getXp(groupName, Integer.valueOf(level));
                }

                int requiredXp = 0;
                if (config.isSet(PathUtils.of(path, groupName, level, "required-xp"))) {
                    requiredXp = config.getInt(PathUtils.of(path, groupName, level, "required-xp"));
                }else {
                    requiredXp = BookUpgradeCustomMenu.getSettings().getRequiredXp(groupName, Integer.valueOf(level));
                }

                String nextEnchantName = config.getString(PathUtils.of(path, groupName, level, "upgrade-enchant.name"));
                int nextEnchantLevel = config.getInt(PathUtils.of(path, groupName, level, "upgrade-enchant.level"));
                int nextEnchantSuccess = config.getInt(PathUtils.of(path, groupName, level, "upgrade-enchant.success"));
                int nextEnchantDestroy = config.getInt(PathUtils.of(path, groupName, level, "upgrade-enchant.destroy"));

                CEEnchantSimple nextEnchant = new CEEnchantSimple(nextEnchantName, nextEnchantLevel, nextEnchantSuccess, nextEnchantDestroy);

                if (nextEnchant.getCEEnchant() == null) {
                    CustomEnchantment.instance().getLogger().warning("Enchant not found: " + nextEnchantName + " when loading BookUpgradeConfig");
                    continue;
                }

                List<String> xpEnchantWhitelist = config.getStringList(PathUtils.of(path, groupName, level, "xp-enchant-whitelist"));
                RequirementList requirementList = RequirementManager.instance().createRequirementList(CustomEnchantment.instance(), config.getStringList(PathUtils.of(path, groupName, level, "requirement")));

                levelMap.put(Integer.valueOf(level), new BookUpgradeData(xp, requiredXp, successChance, nextEnchant, xpEnchantWhitelist, requirementList, loseXpPercent));
            }

            BookUpgradeLevelData levelData = new BookUpgradeLevelData(levelMap);
            map.put(groupName, levelData);
        }

        return map;
    }

}
