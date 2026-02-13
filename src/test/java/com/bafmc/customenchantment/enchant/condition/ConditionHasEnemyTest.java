package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionHasEnemy - checks if enemy living entity exists in data.
 */
@DisplayName("ConditionHasEnemy Tests")
class ConditionHasEnemyTest {

    private ConditionHasEnemy condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionHasEnemy();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return HAS_ENEMY")
        void shouldReturnCorrectIdentifier() {
            assertEquals("HAS_ENEMY", condition.getIdentify());
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
    @DisplayName("match Tests")
    class MatchTests {

        @Test
        @DisplayName("should return true when enemy living entity is not null")
        void shouldReturnTrueWhenEnemyExists() {
            LivingEntity mockEnemy = mock(LivingEntity.class);
            when(mockData.getEnemyLivingEntity()).thenReturn(mockEnemy);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when enemy living entity is null")
        void shouldReturnFalseWhenNoEnemy() {
            when(mockData.getEnemyLivingEntity()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }
}
