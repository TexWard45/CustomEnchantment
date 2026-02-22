package com.bafmc.customenchantment.menu.artifactupgrade.data;

import com.bafmc.customenchantment.constant.MessageKey;

public enum ArtifactUpgradeConfirmReason implements MessageKey {
    NOTHING, FAIL_CHANCE, SUCCESS, NO_INGREDIENT, NO_SELECTED_ARTIFACT;

    private static final String PREFIX = "menu.artifactupgrade.confirm.";

    @Override
    public String getKey() {
        return PREFIX + name().toLowerCase().replace("_", "-");
    }
}
