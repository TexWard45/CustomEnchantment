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
 * Tests for ConditionEquipSlot - checks if item equip slot matches expected slot.
 */
@DisplayName("ConditionEquipSlot Tests")
class ConditionEquipSlotTest {

    private ConditionEquipSlot condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionEquipSlot();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return EQUIP_SLOT")
        void shouldReturnCorrectIdentifier() {
            assertEquals("EQUIP_SLOT", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should setup with valid EquipSlot argument")
        void shouldSetupWithValidArg() {
            assertDoesNotThrow(() -> condition.setup(new String[]{"HELMET"}));
        }

        @Test
        @DisplayName("should throw exception for invalid EquipSlot argument")
        void shouldThrowForInvalidArg() {
            assertThrows(IllegalArgumentException.class, () -> condition.setup(new String[]{"INVALID"}));
        }

        @Test
        @DisplayName("should throw exception for empty args")
        void shouldThrowForEmptyArgs() {
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> condition.setup(new String[]{}));
        }
    }

    @Nested
    @DisplayName("match Tests - Exact Match")
    class ExactMatchTests {

        @ParameterizedTest(name = "should match when both equip and current are {0}")
        @EnumSource(value = EquipSlot.class, names = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "MAINHAND", "OFFHAND"})
        @DisplayName("should match exact equip slot")
        void shouldMatchExactEquipSlot(EquipSlot slot) {
            condition.setup(new String[]{slot.name()});
            when(mockData.getEquipSlot()).thenReturn(slot);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should not match when slots differ")
        void shouldNotMatchWhenSlotsDiffer() {
            condition.setup(new String[]{"HELMET"});
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.BOOTS);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - HAND Group")
    class HandGroupTests {

        @Test
        @DisplayName("should match HAND with MAINHAND")
        void shouldMatchHandWithMainhand() {
            condition.setup(new String[]{"HAND"});
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.MAINHAND);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should match HAND with OFFHAND")
        void shouldMatchHandWithOffhand() {
            condition.setup(new String[]{"HAND"});
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.OFFHAND);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should not match HAND with HELMET")
        void shouldNotMatchHandWithHelmet() {
            condition.setup(new String[]{"HAND"});
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.HELMET);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - ARMOR Group")
    class ArmorGroupTests {

        @ParameterizedTest(name = "should match ARMOR with {0}")
        @EnumSource(value = EquipSlot.class, names = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"})
        @DisplayName("should match ARMOR with armor slots")
        void shouldMatchArmorWithArmorSlots(EquipSlot slot) {
            condition.setup(new String[]{"ARMOR"});
            when(mockData.getEquipSlot()).thenReturn(slot);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should not match ARMOR with MAINHAND")
        void shouldNotMatchArmorWithMainhand() {
            condition.setup(new String[]{"ARMOR"});
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.MAINHAND);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - HOTBAR Group")
    class HotbarGroupTests {

        @ParameterizedTest(name = "should match HOTBAR with {0}")
        @EnumSource(value = EquipSlot.class, names = {
                "HOTBAR_1", "HOTBAR_2", "HOTBAR_3", "HOTBAR_4", "HOTBAR_5",
                "HOTBAR_6", "HOTBAR_7", "HOTBAR_8", "HOTBAR_9"
        })
        @DisplayName("should match HOTBAR with hotbar slots")
        void shouldMatchHotbarWithHotbarSlots(EquipSlot slot) {
            condition.setup(new String[]{"HOTBAR"});
            when(mockData.getEquipSlot()).thenReturn(slot);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should not match HOTBAR with HELMET")
        void shouldNotMatchHotbarWithHelmet() {
            condition.setup(new String[]{"HOTBAR"});
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.HELMET);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - EXTRA_SLOT Group")
    class ExtraSlotGroupTests {

        @ParameterizedTest(name = "should match EXTRA_SLOT with {0}")
        @EnumSource(value = EquipSlot.class, names = {
                "EXTRA_SLOT_1", "EXTRA_SLOT_2", "EXTRA_SLOT_3", "EXTRA_SLOT_4", "EXTRA_SLOT_5",
                "EXTRA_SLOT_6", "EXTRA_SLOT_7", "EXTRA_SLOT_8", "EXTRA_SLOT_9"
        })
        @DisplayName("should match EXTRA_SLOT with extra slot variants")
        void shouldMatchExtraSlotWithVariants(EquipSlot slot) {
            condition.setup(new String[]{"EXTRA_SLOT"});
            when(mockData.getEquipSlot()).thenReturn(slot);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should not match EXTRA_SLOT with MAINHAND")
        void shouldNotMatchExtraSlotWithMainhand() {
            condition.setup(new String[]{"EXTRA_SLOT"});
            when(mockData.getEquipSlot()).thenReturn(EquipSlot.MAINHAND);

            assertFalse(condition.match(mockData));
        }
    }
}
