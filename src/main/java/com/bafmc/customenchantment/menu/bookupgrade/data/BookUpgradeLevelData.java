package com.bafmc.customenchantment.menu.bookupgrade.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class BookUpgradeLevelData {
    private Map<Integer, BookUpgradeData> levelMap;
}