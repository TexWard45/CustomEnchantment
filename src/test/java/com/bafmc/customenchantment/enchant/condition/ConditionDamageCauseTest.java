package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionDamageCause - checks if damage cause is in the allowed list.
 */
@DisplayName("ConditionDamageCause Tests")
class ConditionDamageCauseTest {

    private ConditionDamageCause condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionDamageCause();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return DAMAGE_CAUSE")
        void shouldReturnCorrectIdentifier() {
            assertEquals("DAMAGE_CAUSE", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("match Tests - Null Damage Cause")
    class NullDamageCauseTests {

        @Test
        @DisplayName("should return false when damage cause is null")
        void shouldReturnFalseWhenDamageCauseNull() {
            List<DamageCause> list = Arrays.asList(DamageCause.ENTITY_ATTACK);
            try {
                var field = ConditionDamageCause.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            when(mockData.getDamageCause()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - With Damage Cause")
    class WithDamageCauseTests {

        @Test
        @DisplayName("should return true when damage cause is in list")
        void shouldReturnTrueWhenCauseInList() {
            List<DamageCause> list = Arrays.asList(DamageCause.ENTITY_ATTACK, DamageCause.PROJECTILE);
            try {
                var field = ConditionDamageCause.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            when(mockData.getDamageCause()).thenReturn(DamageCause.ENTITY_ATTACK);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when damage cause is not in list")
        void shouldReturnFalseWhenCauseNotInList() {
            List<DamageCause> list = Arrays.asList(DamageCause.ENTITY_ATTACK, DamageCause.PROJECTILE);
            try {
                var field = ConditionDamageCause.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            when(mockData.getDamageCause()).thenReturn(DamageCause.FALL);

            assertFalse(condition.match(mockData));
        }

        @Test
        @DisplayName("should match FIRE damage cause")
        void shouldMatchFireDamageCause() {
            List<DamageCause> list = Arrays.asList(DamageCause.FIRE, DamageCause.FIRE_TICK);
            try {
                var field = ConditionDamageCause.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            when(mockData.getDamageCause()).thenReturn(DamageCause.FIRE);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should match single cause in list")
        void shouldMatchSingleCauseInList() {
            List<DamageCause> list = Arrays.asList(DamageCause.VOID);
            try {
                var field = ConditionDamageCause.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            when(mockData.getDamageCause()).thenReturn(DamageCause.VOID);

            assertTrue(condition.match(mockData));
        }
    }
}
