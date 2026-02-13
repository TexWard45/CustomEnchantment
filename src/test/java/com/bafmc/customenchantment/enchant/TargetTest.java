package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Target enum - defines target types for conditions and effects.
 */
@DisplayName("Target Tests")
class TargetTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValuesTests {

        @Test
        @DisplayName("should have exactly 2 target values")
        void shouldHaveTwoValues() {
            assertEquals(2, Target.values().length);
        }

        @ParameterizedTest(name = "should contain {0}")
        @EnumSource(Target.class)
        @DisplayName("should contain all expected target values")
        void shouldContainAllValues(Target target) {
            assertNotNull(target);
        }

        @Test
        @DisplayName("should contain PLAYER")
        void shouldContainPlayer() {
            assertEquals(Target.PLAYER, Target.valueOf("PLAYER"));
        }

        @Test
        @DisplayName("should contain ENEMY")
        void shouldContainEnemy() {
            assertEquals(Target.ENEMY, Target.valueOf("ENEMY"));
        }

        @Test
        @DisplayName("should throw for invalid name")
        void shouldThrowForInvalidName() {
            assertThrows(IllegalArgumentException.class, () -> Target.valueOf("ALLY"));
        }
    }

    @Nested
    @DisplayName("Ordinal Tests")
    class OrdinalTests {

        @Test
        @DisplayName("PLAYER should have ordinal 0")
        void playerOrdinalShouldBeZero() {
            assertEquals(0, Target.PLAYER.ordinal());
        }

        @Test
        @DisplayName("ENEMY should have ordinal 1")
        void enemyOrdinalShouldBeOne() {
            assertEquals(1, Target.ENEMY.ordinal());
        }
    }
}
