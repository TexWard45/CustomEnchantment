package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionHasNearbyEnemy - checks if there are nearby enemy players within distance.
 * Note: match() internally calls FactionAPI which requires Factions (compileOnly).
 * FactionAPI class cannot be loaded in the test environment, so match() tests are limited
 * to structure and setup verification. The distance/faction logic is documented but untestable
 * without the Factions dependency.
 */
@DisplayName("ConditionHasNearbyEnemy Tests")
class ConditionHasNearbyEnemyTest {

    private ConditionHasNearbyEnemy condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionHasNearbyEnemy();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return HAS_NEARBY_ENEMY")
        void shouldReturnCorrectIdentifier() {
            assertEquals("HAS_NEARBY_ENEMY", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should setup with valid distance argument")
        void shouldSetupWithValidDistance() {
            assertDoesNotThrow(() -> condition.setup(new String[]{"10.0"}));
        }

        @Test
        @DisplayName("should setup with integer distance")
        void shouldSetupWithIntegerDistance() {
            assertDoesNotThrow(() -> condition.setup(new String[]{"5"}));
        }

        @Test
        @DisplayName("should throw for non-numeric distance")
        void shouldThrowForNonNumericDistance() {
            assertThrows(NumberFormatException.class, () -> condition.setup(new String[]{"abc"}));
        }

        @Test
        @DisplayName("should throw for empty args")
        void shouldThrowForEmptyArgs() {
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> condition.setup(new String[]{}));
        }

        @Test
        @DisplayName("should setup with zero distance")
        void shouldSetupWithZeroDistance() {
            assertDoesNotThrow(() -> condition.setup(new String[]{"0"}));
        }

        @Test
        @DisplayName("should setup with negative distance")
        void shouldSetupWithNegativeDistance() {
            assertDoesNotThrow(() -> condition.setup(new String[]{"-5.0"}));
        }

        @Test
        @DisplayName("should store distance value correctly")
        void shouldStoreDistanceValue() {
            condition.setup(new String[]{"25.5"});
            try {
                var field = ConditionHasNearbyEnemy.class.getDeclaredField("distance");
                field.setAccessible(true);
                assertEquals(25.5, field.getDouble(condition));
            } catch (Exception e) {
                fail("Failed to read distance field: " + e.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("should extend ConditionHook")
        void shouldExtendConditionHook() {
            assertInstanceOf(ConditionHook.class, condition);
        }

        @Test
        @DisplayName("should have distance field")
        void shouldHaveDistanceField() {
            try {
                var field = ConditionHasNearbyEnemy.class.getDeclaredField("distance");
                field.setAccessible(true);
                assertNotNull(field);
                assertEquals(double.class, field.getType());
            } catch (NoSuchFieldException e) {
                fail("Should have 'distance' field");
            }
        }

        @Test
        @DisplayName("should have match method accepting CEFunctionData")
        void shouldHaveMatchMethod() {
            try {
                var method = ConditionHasNearbyEnemy.class.getMethod("match", CEFunctionData.class);
                assertNotNull(method);
                assertEquals(boolean.class, method.getReturnType());
            } catch (NoSuchMethodException e) {
                fail("Should have match(CEFunctionData) method");
            }
        }

        @Test
        @DisplayName("should be cloneable via ConditionHook")
        void shouldBeCloneable() {
            condition.setup(new String[]{"10.0"});
            ConditionHook cloned = condition.clone();
            assertNotNull(cloned);
            assertInstanceOf(ConditionHasNearbyEnemy.class, cloned);
            assertEquals(condition.getIdentify(), cloned.getIdentify());
        }
    }
}
