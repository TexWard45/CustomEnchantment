package com.bafmc.customenchantment.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Pair class - a simple key-value data structure.
 */
@DisplayName("Pair Tests")
class PairTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create pair with key and value")
        void shouldCreatePairWithKeyAndValue() {
            Pair<String, Integer> pair = new Pair<>("test", 42);

            assertNotNull(pair);
            assertEquals("test", pair.getKey());
            assertEquals(42, pair.getValue());
        }

        @Test
        @DisplayName("Should create pair with null key")
        void shouldCreatePairWithNullKey() {
            Pair<String, Integer> pair = new Pair<>(null, 42);

            assertNull(pair.getKey());
            assertEquals(42, pair.getValue());
        }

        @Test
        @DisplayName("Should create pair with null value")
        void shouldCreatePairWithNullValue() {
            Pair<String, Integer> pair = new Pair<>("test", null);

            assertEquals("test", pair.getKey());
            assertNull(pair.getValue());
        }

        @Test
        @DisplayName("Should create pair with both null")
        void shouldCreatePairWithBothNull() {
            Pair<String, Integer> pair = new Pair<>(null, null);

            assertNull(pair.getKey());
            assertNull(pair.getValue());
        }
    }

    @Nested
    @DisplayName("Type Parameter Tests")
    class TypeParameterTests {

        @Test
        @DisplayName("Should work with String-String pair")
        void shouldWorkWithStringStringPair() {
            Pair<String, String> pair = new Pair<>("key", "value");

            assertEquals("key", pair.getKey());
            assertEquals("value", pair.getValue());
        }

        @Test
        @DisplayName("Should work with Integer-Double pair")
        void shouldWorkWithIntegerDoublePair() {
            Pair<Integer, Double> pair = new Pair<>(1, 3.14);

            assertEquals(1, pair.getKey());
            assertEquals(3.14, pair.getValue(), 0.001);
        }

        @Test
        @DisplayName("Should work with complex types")
        void shouldWorkWithComplexTypes() {
            Pair<String, Pair<Integer, Boolean>> pair = new Pair<>("outer", new Pair<>(10, true));

            assertEquals("outer", pair.getKey());
            assertNotNull(pair.getValue());
            assertEquals(10, pair.getValue().getKey());
            assertTrue(pair.getValue().getValue());
        }

        @Test
        @DisplayName("Should work with same types for key and value")
        void shouldWorkWithSameTypes() {
            Pair<String, String> pair = new Pair<>("first", "second");

            assertEquals("first", pair.getKey());
            assertEquals("second", pair.getValue());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("getKey should return correct key")
        void getKeyShouldReturnCorrectKey() {
            Pair<String, Integer> pair = new Pair<>("myKey", 100);

            String result = pair.getKey();

            assertEquals("myKey", result);
        }

        @Test
        @DisplayName("getValue should return correct value")
        void getValueShouldReturnCorrectValue() {
            Pair<String, Integer> pair = new Pair<>("myKey", 100);

            Integer result = pair.getValue();

            assertEquals(100, result);
        }

        @Test
        @DisplayName("Multiple calls to getKey should return same value")
        void multipleCallsToGetKeyShouldReturnSameValue() {
            Pair<String, Integer> pair = new Pair<>("consistent", 42);

            assertEquals(pair.getKey(), pair.getKey());
            assertEquals("consistent", pair.getKey());
        }

        @Test
        @DisplayName("Multiple calls to getValue should return same value")
        void multipleCallsToGetValueShouldReturnSameValue() {
            Pair<String, Integer> pair = new Pair<>("test", 42);

            assertEquals(pair.getValue(), pair.getValue());
            assertEquals(42, pair.getValue());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty string key")
        void shouldHandleEmptyStringKey() {
            Pair<String, Integer> pair = new Pair<>("", 42);

            assertEquals("", pair.getKey());
            assertEquals(42, pair.getValue());
        }

        @Test
        @DisplayName("Should handle empty string value")
        void shouldHandleEmptyStringValue() {
            Pair<String, String> pair = new Pair<>("key", "");

            assertEquals("key", pair.getKey());
            assertEquals("", pair.getValue());
        }

        @Test
        @DisplayName("Should handle Integer min value")
        void shouldHandleIntegerMinValue() {
            Pair<String, Integer> pair = new Pair<>("min", Integer.MIN_VALUE);

            assertEquals(Integer.MIN_VALUE, pair.getValue());
        }

        @Test
        @DisplayName("Should handle Integer max value")
        void shouldHandleIntegerMaxValue() {
            Pair<String, Integer> pair = new Pair<>("max", Integer.MAX_VALUE);

            assertEquals(Integer.MAX_VALUE, pair.getValue());
        }

        @Test
        @DisplayName("Should handle zero value")
        void shouldHandleZeroValue() {
            Pair<String, Integer> pair = new Pair<>("zero", 0);

            assertEquals(0, pair.getValue());
        }

        @Test
        @DisplayName("Should handle negative value")
        void shouldHandleNegativeValue() {
            Pair<String, Integer> pair = new Pair<>("negative", -100);

            assertEquals(-100, pair.getValue());
        }
    }
}
