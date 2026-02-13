package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionOxygenPercent - checks if player oxygen percentage matches comparison.
 * Note: Implementation actually uses foodLevel / 20 * 100, not remainingAir.
 * This test documents the actual behavior.
 */
@DisplayName("ConditionOxygenPercent Tests")
class ConditionOxygenPercentTest {

    private ConditionOxygenPercent condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionOxygenPercent();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return OXYGEN_PERCENT")
        void shouldReturnCorrectIdentifier() {
            assertEquals("OXYGEN_PERCENT", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should setup with valid operator and value")
        void shouldSetupWithValidArgs() {
            assertDoesNotThrow(() -> condition.setup(new String[]{">", "50.0"}));
        }

        @Test
        @DisplayName("should throw for non-numeric value")
        void shouldThrowForNonNumericValue() {
            assertThrows(NumberFormatException.class, () -> condition.setup(new String[]{">", "abc"}));
        }
    }

    @Nested
    @DisplayName("match Tests - Null Player")
    class NullPlayerTests {

        @Test
        @DisplayName("should return false when player is null")
        void shouldReturnFalseWhenPlayerNull() {
            condition.setup(new String[]{">", "50.0"});
            when(mockData.getPlayer()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - Actual Implementation Behavior")
    class ActualBehaviorTests {

        // Note: The implementation uses foodLevel / 20d * 100d, not remainingAir
        // This documents the actual behavior, not the expected behavior

        @ParameterizedTest(name = "food={0}, op={1}, value={2} should be {3}")
        @CsvSource({
                "20, =, 100.0, true",
                "10, =, 50.0, true",
                "0, =, 0.0, true",
                "5, =, 25.0, true",
                "15, >, 50.0, true",
                "10, <, 60.0, true"
        })
        @DisplayName("should use food level for percentage calculation (actual implementation)")
        void shouldUseFoodLevelForPercent(int food, String operator, double value, boolean expected) {
            condition.setup(new String[]{operator, String.valueOf(value)});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getFoodLevel()).thenReturn(food);

            assertEquals(expected, condition.match(mockData));
        }
    }
}
