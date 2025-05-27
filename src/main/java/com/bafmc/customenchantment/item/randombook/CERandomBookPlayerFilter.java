package com.bafmc.customenchantment.item.randombook;

import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CERandomBookPlayerFilter {
    private static Map<String, Data> dataMap = new HashMap<>();

    @Getter
    @Setter
    private static List<String> filterCEList = new ArrayList<>();

    public static class Data {
        private String playerName;
        private List<CEEnchantSimple> enchantList;
    }

    public static void add(Player player, CEEnchantSimple ceEnchantSimple) {
        Data data = dataMap.get(player.getName());

        if (data == null) {
            data = new Data();
            data.playerName = player.getName();
            data.enchantList = new ArrayList<>();
            dataMap.put(player.getName(), data);
        }

        data.enchantList.add(ceEnchantSimple);
    }

    public static void remove(Player player, String enchant) {
        enchant = enchant.toLowerCase();

        Data data = dataMap.get(player.getName());

        if (data == null) {
            return;
        }

        for (CEEnchantSimple ceEnchantSimple : data.enchantList) {
            if (ceEnchantSimple.getCEEnchant().getName().equalsIgnoreCase(enchant)) {
                data.enchantList.remove(ceEnchantSimple);
                break;
            }
        }
    }

    public static void clear(Player player) {
        dataMap.remove(player.getName());
    }

    public static boolean isEmpty(Player player) {
        Data data = dataMap.get(player.getName());

        return data == null || data.enchantList.isEmpty();
    }

    public static boolean isFilter(Player player) {
        Data data = dataMap.get(player.getName());

        return data != null && !data.enchantList.isEmpty();
    }

    public static boolean isFilter(Player player, String enchant) {
        enchant = enchant.toLowerCase();

        Data data = dataMap.get(player.getName());

        if (data == null) {
            return false;
        }

        for (CEEnchantSimple ceEnchantSimple : data.enchantList) {
            if (ceEnchantSimple.getCEEnchant().getName().equalsIgnoreCase(enchant)) {
                return true;
            }
        }

        return false;
    }

    public static List<CEEnchantSimple> get(Player player) {
        Data data = dataMap.get(player.getName());

        if (data == null) {
            return new ArrayList<>();
        }

        return data.enchantList;
    }
}
