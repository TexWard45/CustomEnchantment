package com.bafmc.customenchantment.menu.artifactupgrade;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.feature.execute.Execute;
import com.bafmc.bukkit.utils.PathUtils;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import com.bafmc.customenchantment.item.artifact.CEArtifactData;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeData;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeLevelData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArtifactUpgradeSettings {
    private int maxIngredientCount;
    private Execute broadcastUpgradeSuccessExecute;
    private AdvancedConfigurationSection ingredientPointConfig;
    private Map<String, ArtifactUpgradeLevelData> artifactUpgradeLevelDataMap = new LinkedHashMap<>();

    public ArtifactUpgradeLevelData getArtifactUpgradeLevelData(String group) {
        return artifactUpgradeLevelDataMap.get(group);
    }

    public ArtifactUpgradeData getArtifactUpgradeData(CEArtifact ceArtifact) {
        if (ceArtifact == null) {
            return null;
        }

        CEArtifactData data = ceArtifact.getData();
        String group = data.getConfigData().getGroup();
        int level = data.getLevel();

        ArtifactUpgradeLevelData levelData = getArtifactUpgradeLevelData(group);
        if (levelData == null) {
            return null;
        }

        return levelData.getLevelMap().get(level);
    }

    public double getRequiredPoint(CEItem ceItem) {
        if (ceItem == null) {
            return 0;
        }

        if (ceItem instanceof CEArtifact ceArtifact) {
            CEArtifactData data = ceArtifact.getData();
            CEArtifactData.ConfigData configData = data.getConfigData();
            return ingredientPointConfig.getDouble(PathUtils.of("artifact", configData.getGroup(), String.valueOf(data.getLevel())));
        }

        return ingredientPointConfig.getDouble(PathUtils.of(ceItem.getType(), ceItem.getData().getPattern()));
    }
}