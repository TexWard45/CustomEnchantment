package com.bafmc.customenchantment.item.sigil;

import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.item.CEItemData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CESigilData extends CEItemData implements Cloneable {
    @Getter
    private ConfigData configData;
    @Getter
    @Setter
    private int level;
    @Getter
    @Setter
    private String id;

    @AllArgsConstructor
    @Getter
    public static class ConfigData {
        private String group;
        private String enchant;
        private int maxLevel;
        private String itemDisplay;
        private List<String> itemLore;
        private List<SpecialDisplayData> specialDisplayDataList;
    }

    @AllArgsConstructor
    @Getter
    public static class SpecialDisplayData {
        private MaterialList materialList;
        private String display;
    }

    public CESigilData() {
    }

    public CESigilData(String pattern, ConfigData configData) {
        super(pattern);
        this.configData = configData;
    }

    public CESigilData clone() {
        CESigilData ceSigilData = new CESigilData();
        ceSigilData.setPattern(this.getPattern());
        ceSigilData.configData = this.configData;
        return ceSigilData;
    }

    public boolean equals(Object data) {
        if (data instanceof CESigilData) {
            return getPattern().equals(((CESigilData) data).getPattern()) && level == ((CESigilData) data).getLevel();
        }
        return false;
    }
}
