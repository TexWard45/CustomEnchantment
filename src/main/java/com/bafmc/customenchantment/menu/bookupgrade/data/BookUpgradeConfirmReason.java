package com.bafmc.customenchantment.menu.bookupgrade.data;

import com.bafmc.customenchantment.constant.MessageKey;

public enum BookUpgradeConfirmReason implements MessageKey {
    SUCCESS_XP, NOTHING, FAIL_CHANCE, SUCCESS;

    private static final String PREFIX = "menu.bookupgrade.confirm.";

    @Override
    public String getKey() {
        return PREFIX + name().toLowerCase().replace("_", "-");
    }
}
