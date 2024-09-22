package com.bafmc.customenchantment.menu;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.bafmc.bukkit.api.EconomyAPI;

public class BookcraftSettings {
    private ConcurrentHashMap<String, Double> moneyGroupRequireMap = new ConcurrentHashMap<String, Double>();

    public BookcraftSettings(ConcurrentHashMap<String, Double> moneyGroupRequireMap) {
        this.moneyGroupRequireMap = moneyGroupRequireMap;
    }

    public double getMoneyRequire(String groupName) {
        return moneyGroupRequireMap.containsKey(groupName) ? moneyGroupRequireMap.get(groupName) : 0;
    }

    public boolean isRequireMoney(Player player, String groupName) {
        return getMoneyRequire(groupName) <= EconomyAPI.getMoney(player);
    }

    public boolean payMoney(Player player, String groupName) {
        if (!isRequireMoney(player, groupName)) {
            return false;
        }
        EconomyAPI.takeMoney(player, getMoneyRequire(groupName));
        return true;
    }

    //FastCraft Zone

    public boolean isRequireMoney(Player player, String groupName, double amount) {
        return (getMoneyRequire(groupName)*amount) <= EconomyAPI.getMoney(player);
    }

    public boolean payMoney(Player player, String groupName, double amount) {
        if (!isRequireMoney(player, groupName, amount)) {
            return false;
        }
        EconomyAPI.takeMoney(player, getMoneyRequire(groupName)*amount);
        return true;
    }
}