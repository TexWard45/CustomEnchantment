package com.bafmc.customenchantment.item.gemdrill;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.ILine;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemType;
import lombok.Getter;

@Getter
//@ToString
public class CEGemDrillSimple implements ILine {
    private String name;

    public CEGemDrillSimple() {
    }

    public CEGemDrillSimple(String name) {
        this.name = name;
    }

    public CEGemDrill getCEGemDrill() {
        return (CEGemDrill) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.GEM_DRILL).get(name);
    }

    public String toLine() {
        return name;
    }

    public void fromLine(String line) {
        Parameter parameter = new Parameter(line);
        this.name = parameter.getString(0);
    }
}
