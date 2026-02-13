package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeaponData Tests")
class WeaponDataTest {

    @Test
    @DisplayName("should create WeaponData instance")
    void shouldCreateWeaponDataInstance() {
        WeaponData data = new WeaponData();
        assertNotNull(data);
    }
}
