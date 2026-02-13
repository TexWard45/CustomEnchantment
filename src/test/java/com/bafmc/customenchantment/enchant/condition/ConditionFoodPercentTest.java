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
 * Tests for ConditionFoodPercent - checks if player food percentage matches comparison.
 * Calculation: (foodLevel / 20) * 100
 */
@DisplayName("ConditionFoodPercent Tests")
class ConditionFoodPercentTest {

    private ConditionFoodPercent condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionFoodPercent();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return FOOD_PERCENT")
        void shouldReturnCorrectIdentifier() {
            assertEquals("FOOD_PERCENT", condition.getIdentify());
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
    @DisplayName("match Tests - Percent Calculations")
    class PercentCalculationTests {

        @ParameterizedTest(name = "food={0}, op={1}, value={2} should be {3}")
        @CsvSource({
                "20, =, 100.0, true",
                "10, =, 50.0, true",
                "0, =, 0.0, true",
                "5, =, 25.0, true",
                "15, >, 50.0, true",
                "10, <, 60.0, true",
                "20, >=, 100.0, true",
                "0, <=, 50.0, true",
                "10, !=, 100.0, true"
        })
        @DisplayName("should correctly calculate food percentage")
        void shouldCalculateFoodPercent(int food, String operator, double value, boolean expected) {
            condition.setup(new String[]{operator, String.valueOf(value)});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getFoodLevel()).thenReturn(food);

            assertEquals(expected, condition.match(mockData));
        }
    }
}
