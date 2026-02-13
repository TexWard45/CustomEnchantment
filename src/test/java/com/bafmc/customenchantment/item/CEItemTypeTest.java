package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemType Tests")
class CEItemTypeTest {

    @Test
    @DisplayName("should have WEAPON type")
    void shouldHaveWeaponType() {
        String weaponType = CEItemType.WEAPON;
        assertNotNull(weaponType);
    }

    @Test
    @DisplayName("should have ARTIFACT type")
    void shouldHaveArtifactType() {
        String artifactType = CEItemType.ARTIFACT;
        assertNotNull(artifactType);
    }

    @Test
    @DisplayName("should have GEM type")
    void shouldHaveGemType() {
        String gemType = CEItemType.GEM;
        assertNotNull(gemType);
    }

    @Test
    @DisplayName("should distinguish between different item types")
    void shouldDistinguishBetweenTypes() {
        assertNotEquals(CEItemType.WEAPON, CEItemType.ARTIFACT);
        assertNotEquals(CEItemType.ARTIFACT, CEItemType.GEM);
        assertNotEquals(CEItemType.WEAPON, CEItemType.GEM);
    }

    @Test
    @DisplayName("should return string representation")
    void shouldReturnStringRepresentation() {
        String type = CEItemType.WEAPON;
        assertTrue(type instanceof String);
        assertFalse(type.isEmpty());
    }
}
