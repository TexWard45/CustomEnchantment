package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.feature.execute.Execute;
import com.bafmc.bukkit.feature.requirement.RequirementList;
import com.bafmc.bukkit.feature.requirement.RequirementManager;
import com.bafmc.bukkit.utils.PathUtils;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeCustomMenu;
import com.bafmc.customenchantment.menu.artifactupgrade.ArtifactUpgradeSettings;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeData;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeLevelData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ArtifactUpgradeConfig extends AbstractConfig {
	protected void loadConfig() {
        if (ArtifactUpgradeCustomMenu.getSettings() == null) {
            ArtifactUpgradeSettings settings = new ArtifactUpgradeSettings();
            ArtifactUpgradeCustomMenu.setSettings(settings);
        }

        ArtifactUpgradeSettings settings = ArtifactUpgradeCustomMenu.getSettings();

        Execute broadcastUpgradeSuccess = new Execute(config.getStringList("settings.broadcast-upgrade-success"));
        settings.setBroadcastUpgradeSuccessExecute(broadcastUpgradeSuccess);
        settings.setMaxIngredientCount(config.getInt("settings.max-ingredient"));

        Map<String, ArtifactUpgradeLevelData> bookUpgradeLevelDataMap = loadArtifactUpgradeLevelData(config);
        settings.setArtifactUpgradeLevelDataMap(bookUpgradeLevelDataMap);
        settings.setIngredientPointConfig(config.getAdvancedConfigurationSection("settings.ingredient-point"));
	}


    public Map<String, ArtifactUpgradeLevelData> loadArtifactUpgradeLevelData(AdvancedConfigurationSection config) {
        Map<String, ArtifactUpgradeLevelData> map = new LinkedHashMap<>();

        String path = "list";

        for (String groupName : config.getKeySection(path, false)) {
            Set<String> levelList = config.getKeySection(PathUtils.of(path, groupName), false);

            Map<Integer, ArtifactUpgradeData> levelMap = new LinkedHashMap<>();

            for (String level : levelList) {
                double requiredPoint = config.getDouble(PathUtils.of(path, groupName, level, "required-point"));

                RequirementList requirementList = RequirementManager.instance().createRequirementList(CustomEnchantment.instance(), config.getStringList(PathUtils.of(path, groupName, level, "requirement")));

                levelMap.put(Integer.valueOf(level), new ArtifactUpgradeData(requiredPoint, requirementList));
            }

            ArtifactUpgradeLevelData levelData = new ArtifactUpgradeLevelData(levelMap);
            map.put(groupName, levelData);
        }

        return map;
    }

}
