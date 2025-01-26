package com.bafmc.customenchantment.utils;

import com.bafmc.customenchantment.player.PlayerStorage;

public class StorageUtils {
    public static void setProtectDeadAmount(PlayerStorage storage, int amount) {
        storage.getConfig().set("equipment.PROTECT_DEAD.amount", amount);
    }

    public static void setProtectDeadType(PlayerStorage storage, String type) {
        storage.getConfig().set("equipment.PROTECT_DEAD.type", type);
    }

    public static boolean isDifferentProtectDead(PlayerStorage storage, String type) {
        if (storage.getConfig().getString("equipment.PROTECT_DEAD.type") == null) {
            return true;
        }

        return !type.equals(storage.getConfig().getString("equipment.PROTECT_DEAD.type"));
    }

    public static String getProtectDeadType(PlayerStorage storage) {
        return storage.getConfig().getString("equipment.PROTECT_DEAD.type");
    }

    public static int getProtectDeadAmount(PlayerStorage storage) {
        return storage.getConfig().getInt("equipment.PROTECT_DEAD.amount");
    }

    public static void useProtectDead(PlayerStorage storage) {
        storage.getConfig().set("equipment.PROTECT_DEAD.amount", storage.getConfig().getInt("equipment.PROTECT_DEAD.amount") - 1);

        if (storage.getConfig().getInt("equipment.PROTECT_DEAD.amount") <= 0) {
            storage.getConfig().set("equipment.PROTECT_DEAD", null);
        }
    }

    public static void removeProtectDead(PlayerStorage playerStorage) {
        playerStorage.getConfig().set("equipment.PROTECT_DEAD", null);
    }
}
