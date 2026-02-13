package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeaponAttribute Tests")
class WeaponAttributeTest {

    @Test
    @DisplayName("should create WeaponAttribute instance")
    void shouldCreateWeaponAttributeInstance() {
        WeaponAttribute attribute = new WeaponAttribute();
        assertNotNull(attribute);
    }
}
