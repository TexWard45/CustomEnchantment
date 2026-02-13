package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("WeaponGem Tests")
class WeaponGemTest {

    @Test
    @DisplayName("should create WeaponGem instance")
    void shouldCreateWeaponGemInstance() {
        CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
        WeaponGem gem = new WeaponGem(mockWeapon);
        assertNotNull(gem);
    }
}
