package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionOnFire - checks if living entity has fire ticks > 0.
 */
@DisplayName("ConditionOnFire Tests")
class ConditionOnFireTest {

    private ConditionOnFire condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionOnFire();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return ON_FIRE")
        void shouldReturnCorrectIdentifier() {
            assertEquals("ON_FIRE", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should accept empty args without error")
        void shouldAcceptEmptyArgs() {
            assertDoesNotThrow(() -> condition.setup(new String[]{}));
        }
    }

    @Nested
    @DisplayName("match Tests - Null Entity")
    class NullEntityTests {

        @Test
        @DisplayName("should return false when living entity is null")
        void shouldReturnFalseWhenEntityNull() {
            when(mockData.getLivingEntity()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - Fire Ticks")
    class FireTickTests {

        @ParameterizedTest(name = "should return true when fire ticks = {0}")
        @ValueSource(ints = {1, 20, 100, 1000})
        @DisplayName("should return true when entity has positive fire ticks")
        void shouldReturnTrueWhenOnFire(int fireTicks) {
            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.getFireTicks()).thenReturn(fireTicks);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when fire ticks is zero")
        void shouldReturnFalseWhenFireTicksZero() {
            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.getFireTicks()).thenReturn(0);

            assertFalse(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when fire ticks is negative")
        void shouldReturnFalseWhenFireTicksNegative() {
            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.getFireTicks()).thenReturn(-1);

            assertFalse(condition.match(mockData));
        }
    }
}
