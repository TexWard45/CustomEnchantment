package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEWeaponType Tests")
class CEWeaponTypeTest {

    @Test
    @DisplayName("should have HELMET type")
    void shouldHaveHelmetType() {
        assertNotNull(CEWeaponType.HELMET);
    }

    @Test
    @DisplayName("should have SWORD type")
    void shouldHaveSwordType() {
        assertNotNull(CEWeaponType.SWORD);
    }

    @Test
    @DisplayName("should have BOW type")
    void shouldHaveBowType() {
        assertNotNull(CEWeaponType.BOW);
    }

    @Test
    @DisplayName("should have armor piece types")
    void shouldHaveArmorPieceTypes() {
        assertNotNull(CEWeaponType.HELMET);
        assertNotNull(CEWeaponType.CHESTPLATE);
        assertNotNull(CEWeaponType.LEGGINGS);
        assertNotNull(CEWeaponType.BOOTS);
    }

    @Test
    @DisplayName("should have weapon types")
    void shouldHaveWeaponTypes() {
        assertNotNull(CEWeaponType.SWORD);
        assertNotNull(CEWeaponType.DAGGER);
        assertNotNull(CEWeaponType.SPEAR);
        assertNotNull(CEWeaponType.GREATSWORD);
        assertNotNull(CEWeaponType.AXE);
        assertNotNull(CEWeaponType.HAMMER);
        assertNotNull(CEWeaponType.STICK);
        assertNotNull(CEWeaponType.STAFF);
        assertNotNull(CEWeaponType.SCYTHE);
        assertNotNull(CEWeaponType.TRIDENT);
    }

    @Test
    @DisplayName("should have ranged weapon types")
    void shouldHaveRangedWeaponTypes() {
        assertNotNull(CEWeaponType.BOW);
        assertNotNull(CEWeaponType.CROSSBOW);
    }

    @Test
    @DisplayName("should have tool types")
    void shouldHaveToolTypes() {
        assertNotNull(CEWeaponType.PICKAXE);
        assertNotNull(CEWeaponType.SHOVEL);
        assertNotNull(CEWeaponType.HOE);
        assertNotNull(CEWeaponType.FISHINGROD);
    }

    @Test
    @DisplayName("should have special types")
    void shouldHaveSpecialTypes() {
        assertNotNull(CEWeaponType.SHIELD);
        assertNotNull(CEWeaponType.WINGS);
        assertNotNull(CEWeaponType.HAT);
    }

    @Test
    @DisplayName("should distinguish between different types")
    void shouldDistinguishBetweenTypes() {
        assertNotEquals(CEWeaponType.SWORD, CEWeaponType.BOW);
        assertNotEquals(CEWeaponType.HELMET, CEWeaponType.CHESTPLATE);
        assertNotEquals(CEWeaponType.PICKAXE, CEWeaponType.SWORD);
    }

    @Test
    @DisplayName("should have 23 total weapon types")
    void shouldHave23TotalTypes() {
        CEWeaponType[] types = CEWeaponType.values();
        assertEquals(23, types.length);
    }
}
