package com.bafmc.customenchantment.menu.bookupgrade.data;

import com.bafmc.bukkit.feature.requirement.RequirementList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.bafmc.customenchantment.enchant.CEEnchantSimple;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.RandomRangeInt;

import java.util.List;

@AllArgsConstructor
@Getter
public class BookUpgradeData {
    private RandomRangeInt xp;
    private int requiredXp;
    private Chance successChance;
    private CEEnchantSimple nextEnchant;
    private List<String> xpEnchantWhitelist;
    private RequirementList requirementList;
    private RandomRangeInt loseXpPercent;
}