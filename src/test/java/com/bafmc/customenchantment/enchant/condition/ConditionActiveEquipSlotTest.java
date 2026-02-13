package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionActiveEquipSlot - checks if item active equip slot matches expected slot.
 */
@DisplayName("ConditionActiveEquipSlot Tests")
class ConditionActiveEquipSlotTest {

    private ConditionActiveEquipSlot condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionActiveEquipSlot();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return ACTIVE_EQUIP_SLOT")
        void shouldReturnCorrectIdentifier() {
            assertEquals("ACTIVE_EQUIP_SLOT", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should setup with valid EquipSlot argument")
        void shouldSetupWithValidArg() {
            assertDoesNotThrow(() -> condition.setup(new String[]{"MAINHAND"}));
        }

        @Test
        @DisplayName("should throw exception for invalid EquipSlot argument")
        void shouldThrowForInvalidArg() {
            assertThrows(IllegalArgumentException.class, () -> condition.setup(new String[]{"INVALID_SLOT"}));
        }
    }

    @Nested
    @DisplayName("match Tests - Exact Match")
    class ExactMatchTests {

        @ParameterizedTest(name = "should match when active equip slot is {0}")
        @EnumSource(value = EquipSlot.class, names = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "MAINHAND", "OFFHAND"})
        @DisplayName("should match exact active equip slot")
        void shouldMatchExactActiveEquipSlot(EquipSlot slot) {
            condition.setup(new String[]{slot.name()});
            when(mockData.getActiveEquipSlot()).thenReturn(slot);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should not match when active slots differ")
        void shouldNotMatchWhenSlotsDiffer() {
            condition.setup(new String[]{"HELMET"});
            when(mockData.getActiveEquipSlot()).thenReturn(EquipSlot.BOOTS);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - HAND Group")
    class HandGroupTests {

        @Test
        @DisplayName("should match HAND with MAINHAND active")
        void shouldMatchHandWithMainhand() {
            condition.setup(new String[]{"HAND"});
            when(mockData.getActiveEquipSlot()).thenReturn(EquipSlot.MAINHAND);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should match HAND with OFFHAND active")
        void shouldMatchHandWithOffhand() {
            condition.setup(new String[]{"HAND"});
            when(mockData.getActiveEquipSlot()).thenReturn(EquipSlot.OFFHAND);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should not match HAND with HELMET active")
        void shouldNotMatchHandWithNonHand() {
            condition.setup(new String[]{"HAND"});
            when(mockData.getActiveEquipSlot()).thenReturn(EquipSlot.HELMET);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - ARMOR Group")
    class ArmorGroupTests {

        @ParameterizedTest(name = "should match ARMOR with active {0}")
        @EnumSource(value = EquipSlot.class, names = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"})
        @DisplayName("should match ARMOR with armor slot active")
        void shouldMatchArmorWithArmorSlots(EquipSlot slot) {
            condition.setup(new String[]{"ARMOR"});
            when(mockData.getActiveEquipSlot()).thenReturn(slot);

            assertTrue(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - HOTBAR Group")
    class HotbarGroupTests {

        @ParameterizedTest(name = "should match HOTBAR with active {0}")
        @EnumSource(value = EquipSlot.class, names = {
                "HOTBAR_1", "HOTBAR_2", "HOTBAR_3", "HOTBAR_4", "HOTBAR_5",
                "HOTBAR_6", "HOTBAR_7", "HOTBAR_8", "HOTBAR_9"
        })
        @DisplayName("should match HOTBAR with hotbar slot active")
        void shouldMatchHotbarWithHotbarSlots(EquipSlot slot) {
            condition.setup(new String[]{"HOTBAR"});
            when(mockData.getActiveEquipSlot()).thenReturn(slot);

            assertTrue(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - EXTRA_SLOT Group")
    class ExtraSlotGroupTests {

        @ParameterizedTest(name = "should match EXTRA_SLOT with active {0}")
        @EnumSource(value = EquipSlot.class, names = {
                "EXTRA_SLOT_1", "EXTRA_SLOT_2", "EXTRA_SLOT_3", "EXTRA_SLOT_4", "EXTRA_SLOT_5",
                "EXTRA_SLOT_6", "EXTRA_SLOT_7", "EXTRA_SLOT_8", "EXTRA_SLOT_9"
        })
        @DisplayName("should match EXTRA_SLOT with extra slot active")
        void shouldMatchExtraSlotWithVariants(EquipSlot slot) {
            condition.setup(new String[]{"EXTRA_SLOT"});
            when(mockData.getActiveEquipSlot()).thenReturn(slot);

            assertTrue(condition.match(mockData));
        }
    }
}
