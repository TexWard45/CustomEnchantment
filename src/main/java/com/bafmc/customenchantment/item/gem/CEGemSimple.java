package com.bafmc.customenchantment.item.gem;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.ILine;
import com.bafmc.customenchantment.api.Parameter;
import com.bafmc.customenchantment.item.CEItemType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class CEGemSimple implements ILine, Cloneable {
    private String name;
    private int level;
    @Setter
    private int index;

    public CEGemSimple(String line) {
        this.fromLine(line);
    }

    public CEGemSimple(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public CEGem getCEGem() {
        return (CEGem) CustomEnchantment.instance().getCeItemStorageMap().get(CEItemType.GEM).get(name);
    }

    public CEGemData getCEGemData() {
        CEGem ceGem = getCEGem();

        if (ceGem == null) {
            return null;
        }

        CEGemData data = ceGem.getData().clone();
        data.setLevel(level);

        return data;
    }

    public String toLine() {
        return name + ":" + level;
    }

    public void fromLine(String line) {
        Parameter parameter = new Parameter(line);
        this.name = parameter.getString(0);
        this.level = parameter.getInteger(1, 1);
    }

    public CEGemSimple clone() {
        return new CEGemSimple(name, level);
    }
}
