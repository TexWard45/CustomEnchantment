package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionInCombat - checks if player is in combat via CEAPI.
 */
@DisplayName("ConditionInCombat Tests")
class ConditionInCombatTest {

    private ConditionInCombat condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionInCombat();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return IN_COMBAT")
        void shouldReturnCorrectIdentifier() {
            assertEquals("IN_COMBAT", condition.getIdentify());
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
        @DisplayName("should return true when player is in combat")
        void shouldReturnTrueWhenInCombat() {
            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.isInCombat(mockPlayer)).thenReturn(true);

                assertTrue(condition.match(mockData));
            }
        }

        @Test
        @DisplayName("should return false when player is not in combat")
        void shouldReturnFalseWhenNotInCombat() {
            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.isInCombat(mockPlayer)).thenReturn(false);

                assertFalse(condition.match(mockData));
            }
        }
    }
}
