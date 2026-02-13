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
 * Tests for ConditionExp - checks if player total experience matches comparison.
 */
@DisplayName("ConditionExp Tests")
class ConditionExpTest {

    private ConditionExp condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionExp();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return EXP")
        void shouldReturnCorrectIdentifier() {
            assertEquals("EXP", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should setup with valid operator and value")
        void shouldSetupWithValidArgs() {
            assertDoesNotThrow(() -> condition.setup(new String[]{">", "100.0"}));
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
            condition.setup(new String[]{">", "100.0"});
            when(mockData.getPlayer()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - All Operators")
    class AllOperatorTests {

        @ParameterizedTest(name = "exp={0}, op={1}, value={2} should be {3}")
        @CsvSource({
                "500, >, 100.0, true",
                "50, >, 100.0, false",
                "100, =, 100.0, true",
                "500, >=, 500.0, true",
                "50, <, 100.0, true",
                "0, <=, 100.0, true",
                "50, !=, 100.0, true",
                "100, !=, 100.0, false"
        })
        @DisplayName("should compare total experience with all operators")
        void shouldCompareExp(int exp, String operator, double value, boolean expected) {
            condition.setup(new String[]{operator, String.valueOf(value)});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getTotalExperience()).thenReturn(exp);

            assertEquals(expected, condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle zero experience")
        void shouldHandleZeroExp() {
            condition.setup(new String[]{"=", "0.0"});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getTotalExperience()).thenReturn(0);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should handle large experience values")
        void shouldHandleLargeExpValues() {
            condition.setup(new String[]{">", "1000000.0"});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getTotalExperience()).thenReturn(5000000);

            assertTrue(condition.match(mockData));
        }
    }
}
