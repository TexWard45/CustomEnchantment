package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionOnlyActiveEquip - checks if equip slot matches active equip slot.
 */
@DisplayName("ConditionOnlyActiveEquip Tests")
class ConditionOnlyActiveEquipTest {

    private ConditionOnlyActiveEquip condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionOnlyActiveEquip();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return ONLY_ACTIVE_EQUIP")
        void shouldReturnCorrectIdentifier() {
            assertEquals("ONLY_ACTIVE_EQUIP", condition.getIdentify());
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

        @Test
        @DisplayName("should accept any args without error since setup is empty")
        void shouldAcceptAnyArgs() {
            assertDoesNotThrow(() -> condition.setup(new String[]{"anything"}));
        }
    }

    @Nested
    @DisplayName("match Tests")
    class MatchTests {

        @Test
        @DisplayName("should return true when equip slot equals active equip slot")
        void shouldReturnTrueWhenSlotsMatch() {
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.MAINHAND);
            when(mockData.getActiveEquipSlot()).thenReturn(EquipSlot.MAINHAND);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when equip slot differs from active equip slot")
        void shouldReturnFalseWhenSlotsDiffer() {
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.MAINHAND);
            when(mockData.getActiveEquipSlot()).thenReturn(EquipSlot.OFFHAND);

            assertFalse(condition.match(mockData));
        }

        @Test
        @DisplayName("should return true when both slots are HELMET")
        void shouldReturnTrueForMatchingArmorSlots() {
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.HELMET);
            when(mockData.getActiveEquipSlot()).thenReturn(EquipSlot.HELMET);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when equip is HAND group but active is specific")
        void shouldReturnFalseForGroupVsSpecific() {
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.HAND);
            when(mockData.getActiveEquipSlot()).thenReturn(EquipSlot.MAINHAND);

            assertFalse(condition.match(mockData));
        }

        @Test
        @DisplayName("should return true when both are null")
        void shouldReturnTrueWhenBothNull() {
            when(mockData.getEquipSlot()).thenReturn(null);
            when(mockData.getActiveEquipSlot()).thenReturn(null);

            assertTrue(condition.match(mockData));
        }
    }
}
