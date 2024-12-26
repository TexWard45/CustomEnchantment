package com.bafmc.customenchantment.menu.bookupgrade;

import com.bafmc.bukkit.feature.execute.Execute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeData;
import com.bafmc.customenchantment.menu.bookupgrade.data.BookUpgradeLevelData;
import com.bafmc.customenchantment.menu.bookupgrade.data.RequiredXpGroup;
import com.bafmc.customenchantment.menu.bookupgrade.data.XpGroup;
import com.bafmc.bukkit.utils.RandomRangeInt;

import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookUpgradeSettings {
    private Map<String, XpGroup> xpGroupMap = new LinkedHashMap<>();
    private Map<String, RequiredXpGroup> requiredXpGroupMap = new LinkedHashMap<>();
    private Map<String, BookUpgradeLevelData> bookUpgradeLevelDataMap = new LinkedHashMap<>();
    private Execute broadcastUpgradeSuccessExecute;

    public void putAllBookUpgradeLevelDataMap(Map<String, BookUpgradeLevelData> bookUpgradeLevelDataMap) {
        this.bookUpgradeLevelDataMap.putAll(bookUpgradeLevelDataMap);
    }

    public XpGroup getXpGroup(String groupName) {
        return xpGroupMap.get(groupName) != null ? xpGroupMap.get(groupName) : xpGroupMap.get("default");
    }

    public RandomRangeInt getXp(CEEnchantSimple ceEnchantSimple) {
        BookUpgradeData bookUpgradeData = getBookUpgradeData(ceEnchantSimple.getName(), ceEnchantSimple.getLevel());

        if (bookUpgradeData != null) {
            return bookUpgradeData.getXp();
        }

        return getXp(ceEnchantSimple.getCEEnchant().getGroupName(), ceEnchantSimple.getLevel());
    }

    public RandomRangeInt getXp(String groupName, int level) {
        XpGroup xpGroup = getXpGroup(groupName);

        if (xpGroup.getXpMap().get(level) == null) {
            xpGroup = getXpGroup("default");
        }

        return xpGroup.getXpMap().getOrDefault(level, xpGroup.getXpMap().get(0));
    }

    public RequiredXpGroup getRequiredXpGroup(String groupName) {
        return requiredXpGroupMap.get(groupName) != null ? requiredXpGroupMap.get(groupName) : requiredXpGroupMap.get("default");
    }

    public int getRequiredXp(String groupName, int level) {
        RequiredXpGroup requiredXpGroup = getRequiredXpGroup(groupName);

        if (requiredXpGroup.getRequiredXpMap().get(level) == null) {
            requiredXpGroup = getRequiredXpGroup("default");
        }

        return requiredXpGroup.getRequiredXpMap().getOrDefault(level, requiredXpGroup.getRequiredXpMap().get(0));
    }

    public BookUpgradeLevelData getBookUpgradeLevelData(String enchantName) {
        return bookUpgradeLevelDataMap.get(enchantName);
    }

    public BookUpgradeData getBookUpgradeData(String enchantName, int level) {
        if (hasBookUpgradeData(enchantName, level)) {
            return bookUpgradeLevelDataMap.get(enchantName).getLevelMap().get(level);
        }
        return null;
    }

    public boolean hasBookUpgradeData(String enchantName, int level) {
        if (bookUpgradeLevelDataMap.get(enchantName) == null) {
            return false;
        }

        return bookUpgradeLevelDataMap.get(enchantName).getLevelMap().get(level) != null;
    }

    public void putAllXpGroupMap(Map<String, XpGroup> xpGroupMap) {
        this.xpGroupMap.putAll(xpGroupMap);
    }

    public void putAllRequiredXpGroupMap(Map<String, RequiredXpGroup> requiredXpGroupMap) {
        this.requiredXpGroupMap.putAll(requiredXpGroupMap);
    }
}