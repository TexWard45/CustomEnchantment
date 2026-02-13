package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("WeaponEnchant Tests")
class WeaponEnchantTest {

    @Test
    @DisplayName("should create WeaponEnchant instance")
    void shouldCreateWeaponEnchantInstance() {
        CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
        WeaponEnchant enchant = new WeaponEnchant(mockWeapon);
        assertNotNull(enchant);
    }
}
