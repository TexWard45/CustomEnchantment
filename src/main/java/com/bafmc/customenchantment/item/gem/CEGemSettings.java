package com.bafmc.customenchantment.item.gem;

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

    @AllArgsConstructor
    @Getter
    public static class SlotSettings {
        private int priority;
        private String display;
    }

    private Map<Integer, GemLevelSettings> gemLevelSettingsMap;
    private Map<String, SlotSettings> slotSettingsMap;

    public GemLevelSettings getGemLevelSettings(int level) {
        return gemLevelSettingsMap.get(level);
    }

    public boolean containsGemLevelSettings(int level) {
        return gemLevelSettingsMap.containsKey(level);
    }

    public SlotSettings getSlotSettings(String slot) {
        return slotSettingsMap.get(slot);
    }
}
