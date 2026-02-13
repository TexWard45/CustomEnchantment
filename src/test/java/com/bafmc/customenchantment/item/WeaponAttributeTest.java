package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("WeaponAttribute Tests")
class WeaponAttributeTest {

    @Test
    @DisplayName("should create WeaponAttribute instance")
    void shouldCreateWeaponAttributeInstance() {
        CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
        WeaponAttribute attribute = new WeaponAttribute(mockWeapon);
        assertNotNull(attribute);
    }
}
