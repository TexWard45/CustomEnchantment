package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.api.LocationFormat;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionOutOfSight - checks if enemy is out of sight based on yaw angle difference.
 */
@DisplayName("ConditionOutOfSight Tests")
class ConditionOutOfSightTest {

    private ConditionOutOfSight condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionOutOfSight();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return OUT_OF_SIGHT")
        void shouldReturnCorrectIdentifier() {
            assertEquals("OUT_OF_SIGHT", condition.getIdentify());
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
    @DisplayName("match Tests - Null Entities")
    class NullEntityTests {

        @Test
        @DisplayName("should return false when entity is null")
        void shouldReturnFalseWhenEntityNull() {
            when(mockData.getLivingEntity()).thenReturn(null);
            when(mockData.getEnemyLivingEntity()).thenReturn(mock(LivingEntity.class));

            assertFalse(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when enemy is null")
        void shouldReturnFalseWhenEnemyNull() {
            when(mockData.getLivingEntity()).thenReturn(mock(LivingEntity.class));
            when(mockData.getEnemyLivingEntity()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when both are null")
        void shouldReturnFalseWhenBothNull() {
            when(mockData.getLivingEntity()).thenReturn(null);
            when(mockData.getEnemyLivingEntity()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - Yaw Calculations")
    class YawCalculationTests {

        @Test
        @DisplayName("should return true when entities face same direction (out of sight)")
        void shouldReturnTrueWhenFacingSameDirection() {
            LivingEntity mockEntity = mock(LivingEntity.class);
            LivingEntity mockEnemy = mock(LivingEntity.class);
            Location entityLoc = mock(Location.class);
            Location enemyLoc = mock(Location.class);

            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockData.getEnemyLivingEntity()).thenReturn(mockEnemy);
            when(mockEntity.getLocation()).thenReturn(entityLoc);
            when(mockEnemy.getLocation()).thenReturn(enemyLoc);
            when(entityLoc.getYaw()).thenReturn(0.0f);
            when(enemyLoc.getYaw()).thenReturn(0.0f);

            // getExactYaw(0) = 270 - 0 = 270
            // degree = |270 - 270| = 0
            // 0 <= 141.5 => true (out of sight)
            try (MockedStatic<LocationFormat> mockedLocationFormat = mockStatic(LocationFormat.class)) {
                mockedLocationFormat.when(() -> LocationFormat.getExactYaw(0.0f)).thenReturn(270.0f);
                mockedLocationFormat.when(() -> LocationFormat.getExactYaw(0.0f)).thenReturn(270.0f);

                assertTrue(condition.match(mockData));
            }
        }

        @Test
        @DisplayName("should return false when entities face opposite directions (in sight)")
        void shouldReturnFalseWhenFacingOppositeDirections() {
            LivingEntity mockEntity = mock(LivingEntity.class);
            LivingEntity mockEnemy = mock(LivingEntity.class);
            Location entityLoc = mock(Location.class);
            Location enemyLoc = mock(Location.class);

            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockData.getEnemyLivingEntity()).thenReturn(mockEnemy);
            when(mockEntity.getLocation()).thenReturn(entityLoc);
            when(mockEnemy.getLocation()).thenReturn(enemyLoc);
            when(entityLoc.getYaw()).thenReturn(0.0f);
            when(enemyLoc.getYaw()).thenReturn(180.0f);

            try (MockedStatic<LocationFormat> mockedLocationFormat = mockStatic(LocationFormat.class)) {
                // getExactYaw(0) = 270, getExactYaw(180) = 90
                mockedLocationFormat.when(() -> LocationFormat.getExactYaw(0.0f)).thenReturn(270.0f);
                mockedLocationFormat.when(() -> LocationFormat.getExactYaw(180.0f)).thenReturn(90.0f);

                // degree = |270 - 90| = 180 > 141.5 => false (in sight)
                assertFalse(condition.match(mockData));
            }
        }
    }
}
