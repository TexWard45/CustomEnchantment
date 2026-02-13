package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionHasDamageCause - checks if damage cause exists in data.
 */
@DisplayName("ConditionHasDamageCause Tests")
class ConditionHasDamageCauseTest {

    private ConditionHasDamageCause condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionHasDamageCause();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return HAS_DAMAGE_CAUSE")
        void shouldReturnCorrectIdentifier() {
            assertEquals("HAS_DAMAGE_CAUSE", condition.getIdentify());
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

        @ParameterizedTest(name = "should return true when damage cause is {0}")
        @EnumSource(value = DamageCause.class, names = {"ENTITY_ATTACK", "PROJECTILE", "FALL", "FIRE", "VOID"})
        @DisplayName("should return true when damage cause is not null")
        void shouldReturnTrueWhenDamageCauseExists(DamageCause cause) {
            when(mockData.getDamageCause()).thenReturn(cause);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when damage cause is null")
        void shouldReturnFalseWhenNoDamageCause() {
            when(mockData.getDamageCause()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }
}
