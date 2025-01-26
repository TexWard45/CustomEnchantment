package com.bafmc.customenchantment.menu.artifactupgrade.data;

import com.bafmc.bukkit.feature.requirement.RequirementList;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ArtifactUpgradeData {
    private double requiredPoint;
    private RequirementList requirementList;
}