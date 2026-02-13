package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeaponSettings Tests")
class WeaponSettingsTest {

    @Test
    @DisplayName("should create WeaponSettings instance via builder")
    void shouldCreateWeaponSettingsInstance() {
        WeaponSettings settings = WeaponSettings.builder().build();
        assertNotNull(settings);
    }
}
