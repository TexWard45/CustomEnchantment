package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.World;
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
 * Tests for ConditionWorldTime - checks if world time matches comparison.
 * Minecraft day: 0-23999 ticks (0=dawn, 6000=noon, 12000=dusk, 18000=midnight).
 */
@DisplayName("ConditionWorldTime Tests")
class ConditionWorldTimeTest {

    private ConditionWorldTime condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionWorldTime();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return WORLD_TIME")
        void shouldReturnCorrectIdentifier() {
            assertEquals("WORLD_TIME", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should setup with valid operator and time value")
        void shouldSetupWithValidArgs() {
            assertDoesNotThrow(() -> condition.setup(new String[]{">", "12000"}));
        }

        @Test
        @DisplayName("should throw for non-numeric time value")
        void shouldThrowForNonNumericValue() {
            assertThrows(NumberFormatException.class, () -> condition.setup(new String[]{">", "noon"}));
        }

        @Test
        @DisplayName("should throw for empty args")
        void shouldThrowForEmptyArgs() {
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> condition.setup(new String[]{}));
        }
    }

    @Nested
    @DisplayName("match Tests - Null Player")
    class NullPlayerTests {

        @Test
        @DisplayName("should return false when player is null")
        void shouldReturnFalseWhenPlayerNull() {
            condition.setup(new String[]{">", "12000"});
            when(mockData.getPlayer()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - All Operators")
    class AllOperatorTests {

        @ParameterizedTest(name = "worldTime={0}, op={1}, value={2} should be {3}")
        @CsvSource({
                "18000, >, 12000, true",
                "6000, >, 12000, false",
                "12000, =, 12000, true",
                "6000, =, 12000, false",
                "6000, <, 12000, true",
                "18000, <, 12000, false",
                "12000, >=, 12000, true",
                "18000, >=, 12000, true",
                "12000, <=, 12000, true",
                "6000, <=, 12000, true",
                "6000, !=, 12000, true",
                "12000, !=, 12000, false"
        })
        @DisplayName("should compare world time with all operators")
        void shouldCompareWorldTime(long worldTime, String operator, long value, boolean expected) {
            condition.setup(new String[]{operator, String.valueOf(value)});

            Player mockPlayer = mock(Player.class);
            World mockWorld = mock(World.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getWorld()).thenReturn(mockWorld);
            when(mockWorld.getTime()).thenReturn(worldTime);

            assertEquals(expected, condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - Minecraft Day Cycle")
    class MinecraftDayCycleTests {

        @Test
        @DisplayName("should detect dawn (time 0)")
        void shouldDetectDawn() {
            condition.setup(new String[]{"=", "0"});

            Player mockPlayer = mock(Player.class);
            World mockWorld = mock(World.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getWorld()).thenReturn(mockWorld);
            when(mockWorld.getTime()).thenReturn(0L);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should detect night time (>= 13000)")
        void shouldDetectNightTime() {
            condition.setup(new String[]{">=", "13000"});

            Player mockPlayer = mock(Player.class);
            World mockWorld = mock(World.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getWorld()).thenReturn(mockWorld);
            when(mockWorld.getTime()).thenReturn(18000L);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should detect daytime (< 13000)")
        void shouldDetectDaytime() {
            condition.setup(new String[]{"<", "13000"});

            Player mockPlayer = mock(Player.class);
            World mockWorld = mock(World.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getWorld()).thenReturn(mockWorld);
            when(mockWorld.getTime()).thenReturn(6000L);

            assertTrue(condition.match(mockData));
        }
    }
}
