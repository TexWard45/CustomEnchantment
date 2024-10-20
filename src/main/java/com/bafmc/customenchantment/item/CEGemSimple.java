package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.ILine;
import com.bafmc.customenchantment.api.Parameter;

public class CEGemSimple implements ILine {
    private String name;
    private int level;

    public CEGemSimple(String line) {
        this.fromLine(line);
    }

    public CEGemSimple(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public CEGem getCEGem() {
        return (CEGem) CustomEnchantment.instance().getCEItemStorageMap().get(CEItemType.GEM).get(name);
    }

    public String toLine() {
        return name + ":" + level;
    }

    public void fromLine(String line) {
        Parameter parameter = new Parameter(line);
        this.name = parameter.getString(0);
        this.level = parameter.getInteger(1, 1);
    }

    public String toString() {
        return "CEGemSimple [name=" + name + ", level=" + level + "]";
    }
}
