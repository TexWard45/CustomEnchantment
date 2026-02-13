package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ModifyType enum - defines modification types (SET, ADD, REMOVE).
 */
@DisplayName("ModifyType Tests")
class ModifyTypeTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValuesTests {

        @Test
        @DisplayName("should have exactly 3 modification types")
        void shouldHaveThreeValues() {
            assertEquals(3, ModifyType.values().length);
        }

        @ParameterizedTest(name = "should contain {0}")
        @EnumSource(ModifyType.class)
        @DisplayName("should contain all expected modification types")
        void shouldContainAllValues(ModifyType type) {
            assertNotNull(type);
        }

        @Test
        @DisplayName("should resolve SET from name")
        void shouldResolveSet() {
            assertEquals(ModifyType.SET, ModifyType.valueOf("SET"));
        }

        @Test
        @DisplayName("should resolve ADD from name")
        void shouldResolveAdd() {
            assertEquals(ModifyType.ADD, ModifyType.valueOf("ADD"));
        }

        @Test
        @DisplayName("should resolve REMOVE from name")
        void shouldResolveRemove() {
            assertEquals(ModifyType.REMOVE, ModifyType.valueOf("REMOVE"));
        }

        @Test
        @DisplayName("should throw for invalid name")
        void shouldThrowForInvalidName() {
            assertThrows(IllegalArgumentException.class, () -> ModifyType.valueOf("MULTIPLY"));
        }
    }

    @Nested
    @DisplayName("Ordinal Tests")
    class OrdinalTests {

        @Test
        @DisplayName("SET should have ordinal 0")
        void setOrdinalShouldBeZero() {
            assertEquals(0, ModifyType.SET.ordinal());
        }

        @Test
        @DisplayName("ADD should have ordinal 1")
        void addOrdinalShouldBeOne() {
            assertEquals(1, ModifyType.ADD.ordinal());
        }

        @Test
        @DisplayName("REMOVE should have ordinal 2")
        void removeOrdinalShouldBeTwo() {
            assertEquals(2, ModifyType.REMOVE.ordinal());
        }
    }
}
