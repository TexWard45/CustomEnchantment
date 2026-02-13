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
 * Tests for ConditionOnGround - checks if living entity is on the ground.
 */
@DisplayName("ConditionOnGround Tests")
class ConditionOnGroundTest {

    private ConditionOnGround condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionOnGround();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return ON_GROUND")
        void shouldReturnCorrectIdentifier() {
            assertEquals("ON_GROUND", condition.getIdentify());
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
    @DisplayName("match Tests")
    class MatchTests {

        @Test
        @DisplayName("should return true when entity is on ground")
        void shouldReturnTrueWhenOnGround() {
            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.isOnGround()).thenReturn(true);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when entity is not on ground")
        void shouldReturnFalseWhenNotOnGround() {
            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.isOnGround()).thenReturn(false);

            assertFalse(condition.match(mockData));
        }
    }
}
