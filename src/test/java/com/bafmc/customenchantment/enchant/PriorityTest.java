package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Priority enum - defines enchant execution priority levels.
 */
@DisplayName("Priority Tests")
class PriorityTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValuesTests {

        @Test
        @DisplayName("should have exactly 6 priority values")
        void shouldHaveSixValues() {
            assertEquals(6, Priority.values().length);
        }

        @ParameterizedTest(name = "should contain {0}")
        @EnumSource(Priority.class)
        @DisplayName("should contain all expected priority values")
        void shouldContainAllValues(Priority priority) {
            assertNotNull(priority);
        }

        @Test
        @DisplayName("should resolve from name via valueOf")
        void shouldResolveFromName() {
            assertEquals(Priority.LOWEST, Priority.valueOf("LOWEST"));
            assertEquals(Priority.LOW, Priority.valueOf("LOW"));
            assertEquals(Priority.NORMAL, Priority.valueOf("NORMAL"));
            assertEquals(Priority.HIGH, Priority.valueOf("HIGH"));
            assertEquals(Priority.HIGHEST, Priority.valueOf("HIGHEST"));
            assertEquals(Priority.MORNITOR, Priority.valueOf("MORNITOR"));
        }

        @Test
        @DisplayName("should throw for invalid name")
        void shouldThrowForInvalidName() {
            assertThrows(IllegalArgumentException.class, () -> Priority.valueOf("INVALID"));
        }
    }

    @Nested
    @DisplayName("HIGH_TO_LOW Array")
    class HighToLowTests {

        @Test
        @DisplayName("should have 6 elements")
        void shouldHaveSixElements() {
            assertEquals(6, Priority.HIGH_TO_LOW.length);
        }

        @Test
        @DisplayName("should be ordered from highest to lowest")
        void shouldBeOrderedHighToLow() {
            Priority[] expected = {Priority.MORNITOR, Priority.HIGHEST, Priority.HIGH, Priority.NORMAL, Priority.LOW, Priority.LOWEST};
            assertArrayEquals(expected, Priority.HIGH_TO_LOW);
        }

        @Test
        @DisplayName("first element should be MORNITOR")
        void firstElementShouldBeMornitor() {
            assertEquals(Priority.MORNITOR, Priority.HIGH_TO_LOW[0]);
        }

        @Test
        @DisplayName("last element should be LOWEST")
        void lastElementShouldBeLowest() {
            assertEquals(Priority.LOWEST, Priority.HIGH_TO_LOW[5]);
        }
    }

    @Nested
    @DisplayName("LOW_TO_HIGH Array")
    class LowToHighTests {

        @Test
        @DisplayName("should have 6 elements")
        void shouldHaveSixElements() {
            assertEquals(6, Priority.LOW_TO_HIGH.length);
        }

        @Test
        @DisplayName("should be ordered from lowest to highest")
        void shouldBeOrderedLowToHigh() {
            Priority[] expected = {Priority.LOWEST, Priority.LOW, Priority.NORMAL, Priority.HIGH, Priority.HIGHEST, Priority.MORNITOR};
            assertArrayEquals(expected, Priority.LOW_TO_HIGH);
        }

        @Test
        @DisplayName("first element should be LOWEST")
        void firstElementShouldBeLowest() {
            assertEquals(Priority.LOWEST, Priority.LOW_TO_HIGH[0]);
        }

        @Test
        @DisplayName("last element should be MORNITOR")
        void lastElementShouldBeMornitor() {
            assertEquals(Priority.MORNITOR, Priority.LOW_TO_HIGH[5]);
        }
    }

    @Nested
    @DisplayName("Array Relationship")
    class ArrayRelationshipTests {

        @Test
        @DisplayName("HIGH_TO_LOW should be reverse of LOW_TO_HIGH")
        void shouldBeReverseOfEachOther() {
            for (int i = 0; i < Priority.HIGH_TO_LOW.length; i++) {
                assertEquals(Priority.HIGH_TO_LOW[i], Priority.LOW_TO_HIGH[Priority.LOW_TO_HIGH.length - 1 - i]);
            }
        }
    }
}
