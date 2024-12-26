package com.bafmc.customenchantment.item.artifact;

import com.bafmc.customenchantment.item.CEItemData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CEArtifactData extends CEItemData implements Cloneable {
    @Getter
    private CEArtifactData.ConfigData configData;
    @Getter
    @Setter
    private int level;

    @AllArgsConstructor
    @Getter
    public static class ConfigData {
        private String group;
        private String enchant;
    }

    public CEArtifactData() {
    }

    public CEArtifactData(String pattern, CEArtifactData.ConfigData configData) {
        super(pattern);
        this.configData = configData;
    }

    public CEArtifactData clone() {
        CEArtifactData ceArtifactData = new CEArtifactData();
        ceArtifactData.setPattern(this.getPattern());
        ceArtifactData.configData = this.configData;
        return ceArtifactData;
    }
}
