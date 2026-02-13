package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CENBT Tests")
class CENBTTest {

    @Test
    @DisplayName("should have DEFAULT constant")
    void shouldHaveDefaultConstant() {
        assertEquals("default", CENBT.DEFAULT);
    }

    @Test
    @DisplayName("should have UPGRADE constant")
    void shouldHaveUpgradeConstant() {
        assertEquals("upgrade", CENBT.UPGRADE);
    }

    @Test
    @DisplayName("should have CE constant")
    void shouldHaveCEConstant() {
        assertEquals("customenchantment", CENBT.CE);
    }

    @Test
    @DisplayName("should have TIME constant")
    void shouldHaveTimeConstant() {
        assertEquals("time", CENBT.TIME);
    }

    @Test
    @DisplayName("should have SETTINGS constant")
    void shouldHaveSettingsConstant() {
        assertEquals("settings", CENBT.SETTINGS);
    }

    @Test
    @DisplayName("should have ENCHANT constant")
    void shouldHaveEnchantConstant() {
        assertEquals("enchant", CENBT.ENCHANT);
    }

    @Test
    @DisplayName("should have DATA constant")
    void shouldHaveDataConstant() {
        assertEquals("data", CENBT.DATA);
    }

    @Test
    @DisplayName("should have TYPE constant")
    void shouldHaveTypeConstant() {
        assertEquals("type", CENBT.TYPE);
    }

    @Test
    @DisplayName("should have CUSTOM_TYPE constant")
    void shouldHaveCustomTypeConstant() {
        assertEquals("custom-type", CENBT.CUSTOM_TYPE);
    }

    @Test
    @DisplayName("should have PATTERN constant")
    void shouldHavePatternConstant() {
        assertEquals("pattern", CENBT.PATTERN);
    }

    @Test
    @DisplayName("should have NAME constant")
    void shouldHaveNameConstant() {
        assertEquals("name", CENBT.NAME);
    }

    @Test
    @DisplayName("should have LEVEL constant")
    void shouldHaveLevelConstant() {
        assertEquals("lvl", CENBT.LEVEL);
    }

    @Test
    @DisplayName("should have SUCCESS constant")
    void shouldHaveSuccessConstant() {
        assertEquals("success", CENBT.SUCCESS);
    }

    @Test
    @DisplayName("should have DESTROY constant")
    void shouldHaveDestroyConstant() {
        assertEquals("destroy", CENBT.DESTROY);
    }

    @Test
    @DisplayName("should have POINT constant")
    void shouldHavePointConstant() {
        assertEquals("point", CENBT.POINT);
    }

    @Test
    @DisplayName("should have ID constant")
    void shouldHaveIdConstant() {
        assertEquals("id", CENBT.ID);
    }

    @Test
    @DisplayName("should ensure unique constant values")
    void shouldEnsureUniqueConstantValues() {
        assertNotEquals(CENBT.DEFAULT, CENBT.UPGRADE);
        assertNotEquals(CENBT.CE, CENBT.DEFAULT);
        assertNotEquals(CENBT.TYPE, CENBT.CUSTOM_TYPE);
        assertNotEquals(CENBT.SUCCESS, CENBT.DESTROY);
    }
}
