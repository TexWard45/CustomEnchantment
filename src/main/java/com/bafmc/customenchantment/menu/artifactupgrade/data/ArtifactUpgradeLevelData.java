package com.bafmc.customenchantment.menu.artifactupgrade.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class ArtifactUpgradeLevelData {
    private Map<Integer, ArtifactUpgradeData> levelMap;
}