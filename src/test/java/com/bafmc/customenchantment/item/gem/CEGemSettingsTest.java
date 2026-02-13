package com.bafmc.customenchantment.item.gem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEGemSettings Tests")
class CEGemSettingsTest {

    @Test
    @DisplayName("should provide settings instance")
    void shouldProvideSettingsInstance() {
        CEGemSettings settings = CEGemSettings.getSettings();
        assertNotNull(settings);
    }

    @Test
    @DisplayName("should return same instance on multiple calls")
    void shouldReturnSameInstanceOnMultipleCalls() {
        CEGemSettings settings1 = CEGemSettings.getSettings();
        CEGemSettings settings2 = CEGemSettings.getSettings();

        assertEquals(settings1, settings2);
    }
}
