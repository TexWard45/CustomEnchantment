package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeaponGem Tests")
class WeaponGemTest {

    @Test
    @DisplayName("should create WeaponGem instance")
    void shouldCreateWeaponGemInstance() {
        WeaponGem gem = new WeaponGem();
        assertNotNull(gem);
    }
}
