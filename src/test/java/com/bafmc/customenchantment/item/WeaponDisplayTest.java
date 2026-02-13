package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("WeaponDisplay Tests")
class WeaponDisplayTest {

    @Test
    @DisplayName("should create WeaponDisplay instance")
    void shouldCreateWeaponDisplayInstance() {
        CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
        WeaponDisplay display = new WeaponDisplay(mockWeapon);
        assertNotNull(display);
    }
}
