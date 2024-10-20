package com.bafmc.customenchantment.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class CEGemSettings {
    @Getter
    private static CEGemSettings settings;

    public static void setSettings(CEGemSettings settings) {
        CEGemSettings.settings = settings;
    }

    @AllArgsConstructor
    @Getter
    public static class GemLevelSettings {
        private String color;
    }

    private Map<Integer, GemLevelSettings> gemLevelSettingsMap;

    public GemLevelSettings getGemLevelSettings(int level) {
        return gemLevelSettingsMap.get(level);
    }
}
