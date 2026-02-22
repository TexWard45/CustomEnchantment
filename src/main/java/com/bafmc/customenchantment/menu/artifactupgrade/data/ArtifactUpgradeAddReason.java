package com.bafmc.customenchantment.menu.artifactupgrade.data;

import com.bafmc.customenchantment.constant.MessageKey;

public enum ArtifactUpgradeAddReason implements MessageKey {
    ADD_SELECTED_ARTIFACT, NOTHING, MAX_LEVEL, ADD_INGREDIENT, MAX_INGREDIENT, NOT_ARTIFACT, NOT_INGREDIENT;

    private static final String PREFIX = "menu.artifactupgrade.add-item.";

    @Override
    public String getKey() {
        return PREFIX + name().toLowerCase().replace("_", "-");
    }
}
