package com.bafmc.customenchantment.menu.bookupgrade.data;

import com.bafmc.customenchantment.constant.MessageKey;

public enum BookUpgradeAddReason implements MessageKey {
    SUCCESS, ALREADY_HAS_BOOK, NOT_UPGRADE_BOOK, NOTHING, DIFFERENT_ENCHANT, NOT_PERFECT_BOOK, NOT_XP_BOOK;

    private static final String PREFIX = "menu.bookupgrade.add-book.";

    @Override
    public String getKey() {
        return PREFIX + name().toLowerCase().replace("_", "-");
    }
}
