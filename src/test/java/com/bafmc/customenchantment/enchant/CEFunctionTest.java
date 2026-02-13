package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.EquipSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CEFunction - defines a single function within an enchant level,
 * containing type, chance, cooldown, conditions, effects, and break flags.
 */
@DisplayName("CEFunction Tests")
class CEFunctionTest {

    private CEFunction ceFunction;
    private Chance mockChance;
    private Cooldown mockCooldown;
    private Condition mockCondition;
    private Condition mockTargetCondition;
    private Effect mockEffect;
    private Effect mockTargetEffect;
    private Option mockOption;
    private Option mockTargetOption;
    private TargetFilter mockTargetFilter;

    @BeforeEach
    void setUp() {
        mockChance = mock(Chance.class);
        mockCooldown = mock(Cooldown.class);
        mockCondition = mock(Condition.class);
        mockTargetCondition = mock(Condition.class);
        mockEffect = mock(Effect.class);
        mockTargetEffect = mock(Effect.class);
        mockOption = mock(Option.class);
        mockTargetOption = mock(Option.class);
        mockTargetFilter = mock(TargetFilter.class);

        ceFunction = new CEFunction(
                "test_function",
                CEType.ATTACK,
                mockChance,
                mockCooldown,
                Arrays.asList(EquipSlot.MAINHAND),
                Arrays.asList(EquipSlot.MAINHAND, EquipSlot.OFFHAND),
                Arrays.asList(EquipSlot.MAINHAND),
                mockTargetFilter,
                mockTargetCondition,
                mockTargetOption,
                mockTargetEffect,
                mockCondition,
                mockOption,
                mockEffect,
                false,
                false, true,
                false, true,
                false, true
        );
    }

    @Nested
    @DisplayName("Constructor and Getter Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should return name")
        void shouldReturnName() {
            assertEquals("test_function", ceFunction.getName());
        }

        @Test
        @DisplayName("should return CEType")
        void shouldReturnCEType() {
            assertSame(CEType.ATTACK, ceFunction.getCEType());
        }

        @Test
        @DisplayName("should return chance")
        void shouldReturnChance() {
            assertSame(mockChance, ceFunction.getChance());
        }

        @Test
        @DisplayName("should return cooldown")
        void shouldReturnCooldown() {
            assertSame(mockCooldown, ceFunction.getCooldown());
        }

        @Test
        @DisplayName("should return condition")
        void shouldReturnCondition() {
            assertSame(mockCondition, ceFunction.getCondition());
        }

        @Test
        @DisplayName("should return effect")
        void shouldReturnEffect() {
            assertSame(mockEffect, ceFunction.getEffect());
        }

        @Test
        @DisplayName("should return option")
        void shouldReturnOption() {
            assertSame(mockOption, ceFunction.getOption());
        }

        @Test
        @DisplayName("should return target filter")
        void shouldReturnTargetFilter() {
            assertSame(mockTargetFilter, ceFunction.getTargetFilter());
        }

        @Test
        @DisplayName("should return target condition")
        void shouldReturnTargetCondition() {
            assertSame(mockTargetCondition, ceFunction.getTargetCondition());
        }

        @Test
        @DisplayName("should return target effect")
        void shouldReturnTargetEffect() {
            assertSame(mockTargetEffect, ceFunction.getTargetEffect());
        }

        @Test
        @DisplayName("should return target option")
        void shouldReturnTargetOption() {
            assertSame(mockTargetOption, ceFunction.getTargetOption());
        }
    }

    @Nested
    @DisplayName("Slot List Tests")
    class SlotListTests {

        @Test
        @DisplayName("should return defensive copy of chance slot")
        void shouldReturnDefensiveCopyOfChanceSlot() {
            List<EquipSlot> slots = ceFunction.getChanceSlot();

            assertEquals(1, slots.size());
            assertEquals(EquipSlot.MAINHAND, slots.get(0));

            // Should be a new list instance (defensive copy)
            slots.add(EquipSlot.OFFHAND);
            assertEquals(1, ceFunction.getChanceSlot().size());
        }

        @Test
        @DisplayName("should return defensive copy of cooldown slot")
        void shouldReturnDefensiveCopyOfCooldownSlot() {
            List<EquipSlot> slots = ceFunction.getCooldownSlot();

            assertEquals(2, slots.size());
            slots.clear();
            assertEquals(2, ceFunction.getCooldownSlot().size());
        }

        @Test
        @DisplayName("should return defensive copy of active slot")
        void shouldReturnDefensiveCopyOfActiveSlot() {
            List<EquipSlot> slots = ceFunction.getActiveSlot();

            assertEquals(1, slots.size());
            assertEquals(EquipSlot.MAINHAND, slots.get(0));
            slots.clear();
            assertEquals(1, ceFunction.getActiveSlot().size());
        }
    }

    @Nested
    @DisplayName("Boolean Flag Tests")
    class BooleanFlagTests {

        @Test
        @DisplayName("should return effectNow flag")
        void shouldReturnEffectNow() {
            assertFalse(ceFunction.isEffectNow());
        }

        @Test
        @DisplayName("should return trueChanceBreak flag")
        void shouldReturnTrueChanceBreak() {
            assertFalse(ceFunction.isTrueChanceBreak());
        }

        @Test
        @DisplayName("should return falseChanceBreak flag")
        void shouldReturnFalseChanceBreak() {
            assertTrue(ceFunction.isFalseChanceBreak());
        }

        @Test
        @DisplayName("should return timeoutCooldownBreak flag")
        void shouldReturnTimeoutCooldownBreak() {
            assertFalse(ceFunction.isTimeoutCooldownBreak());
        }

        @Test
        @DisplayName("should return inCooldownBreak flag")
        void shouldReturnInCooldownBreak() {
            assertTrue(ceFunction.isInCooldownBreak());
        }

        @Test
        @DisplayName("should return trueConditionBreak flag")
        void shouldReturnTrueConditionBreak() {
            assertFalse(ceFunction.isTrueConditionBreak());
        }

        @Test
        @DisplayName("should return falseConditionBreak flag")
        void shouldReturnFalseConditionBreak() {
            assertTrue(ceFunction.isFalseConditionBreak());
        }
    }

    @Nested
    @DisplayName("All Flags True Tests")
    class AllFlagsTrueTests {

        @Test
        @DisplayName("should handle all break flags set to true")
        void shouldHandleAllFlagsTrue() {
            CEFunction allTrue = new CEFunction(
                    "all_true", CEType.AUTO, mockChance, mockCooldown,
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                    mockTargetFilter, mockTargetCondition, mockTargetOption, mockTargetEffect,
                    mockCondition, mockOption, mockEffect, true,
                    true, true, true, true, true, true
            );

            assertTrue(allTrue.isEffectNow());
            assertTrue(allTrue.isTrueChanceBreak());
            assertTrue(allTrue.isFalseChanceBreak());
            assertTrue(allTrue.isTimeoutCooldownBreak());
            assertTrue(allTrue.isInCooldownBreak());
            assertTrue(allTrue.isTrueConditionBreak());
            assertTrue(allTrue.isFalseConditionBreak());
        }
    }

    @Nested
    @DisplayName("Empty Slot Lists Tests")
    class EmptySlotListsTests {

        @Test
        @DisplayName("should handle empty chance slot list")
        void shouldHandleEmptyChanceSlots() {
            CEFunction emptySlots = new CEFunction(
                    "empty", CEType.AUTO, mockChance, mockCooldown,
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                    mockTargetFilter, mockTargetCondition, mockTargetOption, mockTargetEffect,
                    mockCondition, mockOption, mockEffect, false,
                    false, false, false, false, false, false
            );

            assertTrue(emptySlots.getChanceSlot().isEmpty());
            assertTrue(emptySlots.getCooldownSlot().isEmpty());
            assertTrue(emptySlots.getActiveSlot().isEmpty());
        }
    }
}
