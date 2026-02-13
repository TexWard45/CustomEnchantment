package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TargetFilter - filters nearby players based on distance and exclusion rules.
 * Note: getTargetsByPlayer requires live Bukkit server (calls Bukkit.getOnlinePlayers()),
 * so we test constructor, getters, and structural behavior.
 */
@DisplayName("TargetFilter Tests")
class TargetFilterTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with all parameters")
        void shouldCreateWithAllParams() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, true, false, 5.0, 50.0, 1, 10);

            assertNotNull(filter);
        }
    }

    @Nested
    @DisplayName("isEnable Tests")
    class IsEnableTests {

        @Test
        @DisplayName("should return true when enabled")
        void shouldReturnTrueWhenEnabled() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, false, false, 0, 100, 0, 10);

            assertTrue(filter.isEnable());
        }

        @Test
        @DisplayName("should return false when disabled")
        void shouldReturnFalseWhenDisabled() {
            TargetFilter filter = new TargetFilter(false, Target.PLAYER, false, false, 0, 100, 0, 10);

            assertFalse(filter.isEnable());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("should return exceptPlayer flag")
        void shouldReturnExceptPlayer() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, true, false, 0, 100, 0, 10);

            assertTrue(filter.isExceptPlayer());
        }

        @Test
        @DisplayName("should return exceptEnemy flag")
        void shouldReturnExceptEnemy() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, false, true, 0, 100, 0, 10);

            assertTrue(filter.isExceptEnemy());
        }

        @Test
        @DisplayName("should return minDistance")
        void shouldReturnMinDistance() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, false, false, 5.5, 100, 0, 10);

            assertEquals(5.5, filter.getMinDistance());
        }

        @Test
        @DisplayName("should return maxDistance")
        void shouldReturnMaxDistance() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, false, false, 0, 50.5, 0, 10);

            assertEquals(50.5, filter.getMaxDistance());
        }

        @Test
        @DisplayName("should return minTarget")
        void shouldReturnMinTarget() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, false, false, 0, 100, 3, 10);

            assertEquals(3, filter.getMinTarget());
        }

        @Test
        @DisplayName("should return maxTarget")
        void shouldReturnMaxTarget() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, false, false, 0, 100, 0, 15);

            assertEquals(15, filter.getMaxTarget());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle zero distance range")
        void shouldHandleZeroDistanceRange() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, false, false, 0, 0, 0, 10);

            assertEquals(0, filter.getMinDistance());
            assertEquals(0, filter.getMaxDistance());
        }

        @Test
        @DisplayName("should handle zero target limits")
        void shouldHandleZeroTargetLimits() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, false, false, 0, 100, 0, 0);

            assertEquals(0, filter.getMinTarget());
            assertEquals(0, filter.getMaxTarget());
        }

        @Test
        @DisplayName("should handle ENEMY target")
        void shouldHandleEnemyTarget() {
            TargetFilter filter = new TargetFilter(true, Target.ENEMY, true, true, 10, 50, 1, 5);

            assertTrue(filter.isExceptPlayer());
            assertTrue(filter.isExceptEnemy());
        }

        @Test
        @DisplayName("should handle both except flags false")
        void shouldHandleBothExceptFalse() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, false, false, 0, 100, 0, 10);

            assertFalse(filter.isExceptPlayer());
            assertFalse(filter.isExceptEnemy());
        }

        @Test
        @DisplayName("should handle both except flags true")
        void shouldHandleBothExceptTrue() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, true, true, 0, 100, 0, 10);

            assertTrue(filter.isExceptPlayer());
            assertTrue(filter.isExceptEnemy());
        }

        @Test
        @DisplayName("should handle large distance values")
        void shouldHandleLargeDistanceValues() {
            TargetFilter filter = new TargetFilter(true, Target.PLAYER, false, false, 0, 1000000.0, 0, 100);

            assertEquals(1000000.0, filter.getMaxDistance());
        }
    }
}
