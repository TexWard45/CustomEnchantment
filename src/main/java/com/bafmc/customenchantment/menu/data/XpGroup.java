package com.bafmc.customenchantment.menu.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.bafmc.bukkit.utils.RandomRangeInt;

import java.util.Map;

@AllArgsConstructor
@Getter
public class XpGroup {
    private Map<Integer, RandomRangeInt> xpMap;
}