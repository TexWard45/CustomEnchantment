package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionAllowFlight - checks if player has flight allowed.
 */
@DisplayName("ConditionAllowFlight Tests")
class ConditionAllowFlightTest {

    private ConditionAllowFlight condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionAllowFlight();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return ALLOW_FLIGHT")
        void shouldReturnCorrectIdentifier() {
            assertEquals("ALLOW_FLIGHT", condition.getIdentify());
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
        @DisplayName("should return true when player has flight allowed")
        void shouldReturnTrueWhenFlightAllowed() {
            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getAllowFlight()).thenReturn(true);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when player does not have flight allowed")
        void shouldReturnFalseWhenFlightNotAllowed() {
            Player mockPlayer = mock(Player.class);
            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getAllowFlight()).thenReturn(false);

            assertFalse(condition.match(mockData));
        }
    }
}
