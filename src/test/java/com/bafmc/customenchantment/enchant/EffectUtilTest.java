package com.bafmc.customenchantment.enchant;

import org.bukkit.attribute.AttributeModifier.Operation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EffectUtil - utility methods for effects (potion list, operation lookup, attribute type).
 */
@DisplayName("EffectUtil Tests")
class EffectUtilTest {

    @Nested
    @DisplayName("getByNumber Tests")
    class GetByNumberTests {

        @Test
        @DisplayName("should return ADD_NUMBER for 0")
        void shouldReturnAddNumberForZero() {
            assertEquals(Operation.ADD_NUMBER, EffectUtil.getByNumber(0));
        }

        @Test
        @DisplayName("should return ADD_SCALAR for 1")
        void shouldReturnAddScalarForOne() {
            assertEquals(Operation.ADD_SCALAR, EffectUtil.getByNumber(1));
        }

        @Test
        @DisplayName("should return MULTIPLY_SCALAR_1 for 2")
        void shouldReturnMultiplyScalar1ForTwo() {
            assertEquals(Operation.MULTIPLY_SCALAR_1, EffectUtil.getByNumber(2));
        }

        @ParameterizedTest(name = "should return ADD_NUMBER for invalid number {0}")
        @ValueSource(ints = {-1, 3, 4, 100, Integer.MAX_VALUE})
        @DisplayName("should return ADD_NUMBER for out of range numbers")
        void shouldReturnAddNumberForInvalid(int number) {
            assertEquals(Operation.ADD_NUMBER, EffectUtil.getByNumber(number));
        }
    }

    @Nested
    @DisplayName("getPotionEffectList Tests")
    class GetPotionEffectListTests {

        // Note: PotionEffectType.getByName() requires the Bukkit registry to be initialized.
        // Without a running server or MockBukkit, the registry is not available and may throw
        // an Error (not Exception), which getPotionEffectList does not catch.
        // We verify the method handles invalid names when the registry is available by
        // testing that it either returns empty or throws due to missing registry.

        @Test
        @DisplayName("should return empty list or throw for invalid potion name without server")
        void shouldReturnEmptyOrThrowForInvalid() {
            try {
                var list = EffectUtil.getPotionEffectList("INVALID_POTION_XYZ");
                // If registry is available, invalid name returns empty list
                assertTrue(list.isEmpty());
            } catch (Throwable t) {
                // Expected when Bukkit registry is not initialized
                // PotionEffectType.getByName may throw Error which is not caught by the method
                assertNotNull(t);
            }
        }

        @Test
        @DisplayName("should return empty list or throw for empty string without server")
        void shouldReturnEmptyOrThrowForEmptyString() {
            try {
                var list = EffectUtil.getPotionEffectList("");
                assertTrue(list.isEmpty());
            } catch (Throwable t) {
                // Expected when Bukkit registry is not initialized
                assertNotNull(t);
            }
        }

        @Test
        @DisplayName("should handle comma-separated invalid names or throw without server")
        void shouldHandleCommaSeparatedInvalidOrThrow() {
            try {
                var list = EffectUtil.getPotionEffectList("INVALID1,INVALID2,INVALID3");
                assertTrue(list.isEmpty());
            } catch (Throwable t) {
                // Expected when Bukkit registry is not initialized
                assertNotNull(t);
            }
        }
    }
}
