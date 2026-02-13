package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CELevelMap - maps enchant levels to CELevel instances.
 * Extends LinkedHashMap with custom get(int) and get(int, int) methods.
 */
@DisplayName("CELevelMap Tests")
class CELevelMapTest {

    private CELevelMap levelMap;
    private CELevel level1;
    private CELevel level2;
    private CELevel level0;

    @BeforeEach
    void setUp() {
        levelMap = new CELevelMap();
        level0 = new CELevel(new LinkedHashMap<>());
        level1 = new CELevel(new LinkedHashMap<>());
        level2 = new CELevel(new LinkedHashMap<>());
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create empty map")
        void shouldCreateEmptyMap() {
            CELevelMap map = new CELevelMap();
            assertTrue(map.isEmpty());
        }
    }

    @Nested
    @DisplayName("get(int) Tests")
    class GetIntTests {

        @Test
        @DisplayName("should return level when key exists")
        void shouldReturnLevelWhenKeyExists() {
            levelMap.put(1, level1);
            levelMap.put(2, level2);

            assertSame(level1, levelMap.get(1));
            assertSame(level2, levelMap.get(2));
        }

        @Test
        @DisplayName("should fall back to key 0 when key does not exist")
        void shouldFallBackToZero() {
            levelMap.put(0, level0);

            assertSame(level0, levelMap.get(99));
        }

        @Test
        @DisplayName("should return null when key does not exist and key 0 does not exist")
        void shouldReturnNullWhenNeitherExists() {
            levelMap.put(1, level1);

            assertNull(levelMap.get(99));
        }
    }

    @Nested
    @DisplayName("get(int, int) Tests")
    class GetIntIntTests {

        @Test
        @DisplayName("should return value for key when key exists")
        void shouldReturnValueWhenKeyExists() {
            levelMap.put(1, level1);
            levelMap.put(2, level2);

            assertSame(level1, levelMap.get(1, 0));
        }

        @Test
        @DisplayName("should fall back to other key when key does not exist")
        void shouldFallBackToOtherKey() {
            levelMap.put(1, level1);

            assertSame(level1, levelMap.get(99, 1));
        }

        @Test
        @DisplayName("should return null when neither key nor fallback exists")
        void shouldReturnNullWhenNeitherExists() {
            levelMap.put(1, level1);

            assertNull(levelMap.get(99, 50));
        }

        @Test
        @DisplayName("should use zero as default other in single-arg get")
        void shouldUseZeroAsDefaultOther() {
            levelMap.put(0, level0);
            levelMap.put(1, level1);

            // get(1) should return level1 directly
            assertSame(level1, levelMap.get(1));

            // get(99) should fall back to 0
            assertSame(level0, levelMap.get(99));
        }
    }

    @Nested
    @DisplayName("Extends LinkedHashMap Tests")
    class ExtendsLinkedHashMapTests {

        @Test
        @DisplayName("should support put and size operations")
        void shouldSupportPutAndSize() {
            levelMap.put(1, level1);
            levelMap.put(2, level2);

            assertEquals(2, levelMap.size());
        }

        @Test
        @DisplayName("should support containsKey")
        void shouldSupportContainsKey() {
            levelMap.put(1, level1);

            assertTrue(levelMap.containsKey(1));
            assertFalse(levelMap.containsKey(2));
        }

        @Test
        @DisplayName("should support remove")
        void shouldSupportRemove() {
            levelMap.put(1, level1);
            levelMap.remove(1);

            assertFalse(levelMap.containsKey(1));
            assertTrue(levelMap.isEmpty());
        }

        @Test
        @DisplayName("should maintain insertion order")
        void shouldMaintainInsertionOrder() {
            levelMap.put(3, level2);
            levelMap.put(1, level1);
            levelMap.put(2, level0);

            Integer[] keys = levelMap.keySet().toArray(new Integer[0]);
            assertEquals(3, keys[0]);
            assertEquals(1, keys[1]);
            assertEquals(2, keys[2]);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle negative level keys")
        void shouldHandleNegativeLevelKeys() {
            levelMap.put(-1, level1);

            assertSame(level1, levelMap.get(-1));
        }

        @Test
        @DisplayName("should handle zero as a valid key")
        void shouldHandleZeroKey() {
            levelMap.put(0, level0);

            assertSame(level0, levelMap.get(0));
        }

        @Test
        @DisplayName("should handle large level keys")
        void shouldHandleLargeLevelKeys() {
            levelMap.put(Integer.MAX_VALUE, level1);

            assertSame(level1, levelMap.get(Integer.MAX_VALUE));
        }
    }
}
