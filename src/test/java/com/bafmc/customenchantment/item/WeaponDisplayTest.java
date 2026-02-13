package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeaponDisplay Tests")
class WeaponDisplayTest {

    @Test
    @DisplayName("should create WeaponDisplay instance")
    void shouldCreateWeaponDisplayInstance() {
        WeaponDisplay display = new WeaponDisplay();
        assertNotNull(display);
    }
}
