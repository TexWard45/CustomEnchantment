package com.bafmc.customenchantment.enchant;

import java.util.LinkedHashMap;

public class CELevelMap extends LinkedHashMap<Integer, CELevel> {
    private static final long serialVersionUID = 1L;

    public CELevelMap() {
    }

    public CELevel get(int key) {
        return this.get(key, 0);
    }

    public CELevel get(int key, int other) {
        return this.containsKey(key) ? super.get(key) : super.get(other);
    }
}
