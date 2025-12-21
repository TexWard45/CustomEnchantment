package com.bafmc.customenchantment.item.outfit;

import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.item.CEItemData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class CEOutfitData extends CEItemData implements Cloneable {
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
        private Map<Integer, CEOutfitData.ConfigByLevelData> levelMap;
    }

    @AllArgsConstructor
    @Getter
    @Builder
    public static class ConfigByLevelData {
        private Map<String, String> skinMap;
        private Map<String, ConfigCustomTypeData> customTypeMap;

        public String getSkinByCustomType(String customType) {
            return skinMap.containsKey(customType) ? skinMap.get(customType) : null;
        }

        public List<String> getSkinListByCustomType(String customType) {
            return customTypeMap.containsKey(customType) ? customTypeMap.get(customType).skinList : null;
        }

        public String getSkinByIndex(String customType, int index) {
            if (customTypeMap.containsKey(customType)) {
                List<String> skinList = customTypeMap.get(customType).skinList;
                if (skinList == null || skinList.isEmpty()) {
                    return getSkinByCustomType(customType);
                }

                if (index >= 0 && index < skinList.size()) {
                    return skinList.get(index);
                } else {
                    return skinList.get(0);
                }
            }
            return getSkinByCustomType(customType);
        }
    }

    @AllArgsConstructor
    public static class ConfigCustomTypeData {
        private List<String> skinList;
    }

    @AllArgsConstructor
    @Getter
    public static class SpecialDisplayData {
        private MaterialList materialList;
        private String display;
    }

    public CEOutfitData() {
    }

    public CEOutfitData(String pattern, ConfigData configData) {
        super(pattern);
        this.configData = configData;
    }

    public CEOutfitData clone() {
        CEOutfitData ceOutfitData = new CEOutfitData();
        ceOutfitData.setPattern(this.getPattern());
        ceOutfitData.configData = this.configData;
        return ceOutfitData;
    }

    public boolean equals(Object data) {
        if (data instanceof CEOutfitData) {
            return getPattern().equals(((CEOutfitData) data).getPattern()) && level == ((CEOutfitData) data).getLevel();
        }
        return false;
    }

    public CEOutfitData.ConfigByLevelData getConfigByLevelData() {
        return configData.getLevelMap().containsKey(level) ? configData.getLevelMap().get(level) : null;
    }
}
