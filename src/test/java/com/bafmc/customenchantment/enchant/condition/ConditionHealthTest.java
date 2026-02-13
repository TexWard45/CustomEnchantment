package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.api.CompareOperation;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionHealth - checks if living entity health matches a comparison.
 */
@DisplayName("ConditionHealth Tests")
class ConditionHealthTest {

    private ConditionHealth condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionHealth();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return HEALTH")
        void shouldReturnCorrectIdentifier() {
            assertEquals("HEALTH", condition.getIdentify());
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
        @DisplayName("should throw for missing value argument")
        void shouldThrowForMissingValue() {
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> condition.setup(new String[]{">"}));
        }

        @Test
        @DisplayName("should throw for non-numeric value")
        void shouldThrowForNonNumericValue() {
            assertThrows(NumberFormatException.class, () -> condition.setup(new String[]{">", "abc"}));
        }
    }

    @Nested
    @DisplayName("match Tests - Null Entity")
    class NullEntityTests {

        @Test
        @DisplayName("should return false when living entity is null")
        void shouldReturnFalseWhenEntityNull() {
            condition.setup(new String[]{">", "10.0"});
            when(mockData.getLivingEntity()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - All Operators")
    class AllOperatorTests {

        @ParameterizedTest(name = "health={0}, op={1}, value={2} should be {3}")
        @CsvSource({
                "15.0, >, 10.0, true",
                "5.0, >, 10.0, false",
                "10.0, >, 10.0, false",
                "5.0, <, 10.0, true",
                "15.0, <, 10.0, false",
                "10.0, <, 10.0, false",
                "10.0, =, 10.0, true",
                "5.0, =, 10.0, false",
                "10.0, >=, 10.0, true",
                "15.0, >=, 10.0, true",
                "5.0, >=, 10.0, false",
                "10.0, <=, 10.0, true",
                "5.0, <=, 10.0, true",
                "15.0, <=, 10.0, false",
                "5.0, !=, 10.0, true",
                "10.0, !=, 10.0, false"
        })
        @DisplayName("should compare health with all operators")
        void shouldCompareHealthWithAllOperators(double health, String operator, double value, boolean expected) {
            condition.setup(new String[]{operator, String.valueOf(value)});

            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.getHealth()).thenReturn(health);

            assertEquals(expected, condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle zero health")
        void shouldHandleZeroHealth() {
            condition.setup(new String[]{"=", "0.0"});

            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.getHealth()).thenReturn(0.0);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should handle max health value")
        void shouldHandleMaxHealthValue() {
            condition.setup(new String[]{"=", "20.0"});

            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.getHealth()).thenReturn(20.0);

            assertTrue(condition.match(mockData));
        }
    }
}
