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
 * Tests for ConditionHealthPercent - checks if player health percentage matches comparison.
 * Calculation: (health / maxHealth) * 100
 */
@DisplayName("ConditionHealthPercent Tests")
class ConditionHealthPercentTest {

    private ConditionHealthPercent condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionHealthPercent();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return HEALTH_PERCENT")
        void shouldReturnCorrectIdentifier() {
            assertEquals("HEALTH_PERCENT", condition.getIdentify());
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

        @ParameterizedTest(name = "health={0}, maxHealth={1}, op={2}, value={3} should be {4}")
        @CsvSource({
                "20.0, 20.0, =, 100.0, true",
                "10.0, 20.0, =, 50.0, true",
                "10.0, 20.0, >, 40.0, true",
                "10.0, 20.0, <, 60.0, true",
                "5.0, 20.0, =, 25.0, true",
                "0.0, 20.0, =, 0.0, true",
                "15.0, 20.0, >=, 75.0, true",
                "15.0, 20.0, <=, 75.0, true",
                "15.0, 20.0, !=, 50.0, true"
        })
        @DisplayName("should correctly calculate health percentage")
        void shouldCalculateHealthPercent(double health, double maxHealth, String operator, double value, boolean expected) {
            condition.setup(new String[]{operator, String.valueOf(value)});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getHealth()).thenReturn(health);
            when(mockPlayer.getMaxHealth()).thenReturn(maxHealth);

            assertEquals(expected, condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle full health (100%)")
        void shouldHandleFullHealth() {
            condition.setup(new String[]{"=", "100.0"});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getHealth()).thenReturn(20.0);
            when(mockPlayer.getMaxHealth()).thenReturn(20.0);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should handle zero health (0%)")
        void shouldHandleZeroHealth() {
            condition.setup(new String[]{"=", "0.0"});

            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getHealth()).thenReturn(0.0);
            when(mockPlayer.getMaxHealth()).thenReturn(20.0);

            assertTrue(condition.match(mockData));
        }
    }
}
