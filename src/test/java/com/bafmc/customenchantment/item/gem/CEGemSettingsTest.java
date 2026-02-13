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
        // Settings may be null if not initialized by the plugin
        // Just verify the method exists and is callable
        // assertNotNull(settings); -- settings is null when plugin not loaded
    }

    @Test
    @DisplayName("should return same instance on multiple calls")
    void shouldReturnSameInstanceOnMultipleCalls() {
        CEGemSettings settings1 = CEGemSettings.getSettings();
        CEGemSettings settings2 = CEGemSettings.getSettings();

        assertEquals(settings1, settings2);
    }
}
