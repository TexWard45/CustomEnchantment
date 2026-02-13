package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.api.CompareOperation;
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
 * Tests for ConditionFood - checks if player food level matches comparison.
 */
@DisplayName("ConditionFood Tests")
class ConditionFoodTest {

    private ConditionFood condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionFood();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return FOOD")
        void shouldReturnCorrectIdentifier() {
            assertEquals("FOOD", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should setup with valid operator and value")
        void shouldSetupWithValidArgs() {
            assertDoesNotThrow(() -> condition.setup(new String[]{">", "10.0"}));
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
            condition.setup(new String[]{">", "10.0"});
            when(mockData.getPlayer()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - All Operators")
    class AllOperatorTests {

        @ParameterizedTest(name = "food={0}, op={1}, value={2} should be {3}")
        @CsvSource({
                "20, >, 10.0, true",
                "5, >, 10.0, false",
                "10, =, 10.0, true",
                "20, >=, 15.0, true",
                "5, <, 10.0, true",
                "15, <=, 20.0, true",
                "5, !=, 10.0, true",
                "10, !=, 10.0, false"
        })
        @DisplayName("should compare food level with all operators")
        void shouldCompareFoodLevel(int food, String operator, double value, boolean expected) {
            condition.setup(new String[]{operator, String.valueOf(value)});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getFoodLevel()).thenReturn(food);

            assertEquals(expected, condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle full food level (20)")
        void shouldHandleFullFoodLevel() {
            condition.setup(new String[]{"=", "20.0"});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getFoodLevel()).thenReturn(20);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should handle zero food level")
        void shouldHandleZeroFoodLevel() {
            condition.setup(new String[]{"=", "0.0"});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getFoodLevel()).thenReturn(0);

            assertTrue(condition.match(mockData));
        }
    }
}
