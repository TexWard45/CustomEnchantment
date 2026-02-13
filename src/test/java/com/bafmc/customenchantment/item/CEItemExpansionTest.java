package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEItemExpansion Tests")
class CEItemExpansionTest {

    @Test
    @DisplayName("should create CEItemExpansion instance")
    void shouldCreateCEItemExpansionInterface() {
        CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
        CEItemExpansion expansion = new CEItemExpansion(mockWeapon) {};
        assertNotNull(expansion);
    }

    @Test
    @DisplayName("should implement CEItemExpansion")
    void shouldImplementCEItemExpansionInterface() {
        CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
        CEItemExpansion expansion = new CEItemExpansion(mockWeapon) {};
        assertTrue(expansion instanceof CEItemExpansion);
    }
}
