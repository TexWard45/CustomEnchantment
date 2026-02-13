package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeaponEnchant Tests")
class WeaponEnchantTest {

    @Test
    @DisplayName("should create WeaponEnchant instance")
    void shouldCreateWeaponEnchantInstance() {
        WeaponEnchant enchant = new WeaponEnchant();
        assertNotNull(enchant);
    }
}
