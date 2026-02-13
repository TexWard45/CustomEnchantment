package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("WeaponData Tests")
class WeaponDataTest {

    @Test
    @DisplayName("should create WeaponData instance")
    void shouldCreateWeaponDataInstance() {
        CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
        WeaponData data = new WeaponData(mockWeapon);
        assertNotNull(data);
    }
}
