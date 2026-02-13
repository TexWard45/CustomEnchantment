package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionHold - checks if player holds a specific material.
 */
@DisplayName("ConditionHold Tests")
class ConditionHoldTest {

    private ConditionHold condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionHold();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return HOLD")
        void shouldReturnCorrectIdentifier() {
            assertEquals("HOLD", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("match Tests - Null Player")
    class NullPlayerTests {

        @Test
        @DisplayName("should return false when player is null")
        void shouldReturnFalseWhenPlayerNull() {
            // Manually set up the condition with a MaterialList to avoid setup dependency
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND_SWORD));

            // Use reflection or create the condition in a way that has a list set
            // Since setup() requires StringUtils from framework, test match() null path directly
            when(mockData.getPlayer()).thenReturn(null);

            // The match method checks player == null first, so this should return false
            // We need the list to be set up, so we test via the public API
            ConditionHold holdCondition = new ConditionHold();
            // Set list via reflection since setup requires framework
            try {
                var field = ConditionHold.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(holdCondition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            assertFalse(holdCondition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - With Player")
    class WithPlayerTests {

        @Test
        @DisplayName("should return true when player holds matching material")
        void shouldReturnTrueWhenHoldingMatchingMaterial() {
            Player mockPlayer = mock(Player.class);
            ItemStack mockItem = mock(ItemStack.class);

            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getItemInHand()).thenReturn(mockItem);
            when(mockItem.getType()).thenReturn(Material.DIAMOND_SWORD);

            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND_SWORD));

            try {
                var field = ConditionHold.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when player holds non-matching material")
        void shouldReturnFalseWhenHoldingNonMatchingMaterial() {
            Player mockPlayer = mock(Player.class);
            ItemStack mockItem = mock(ItemStack.class);

            when(mockData.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getItemInHand()).thenReturn(mockItem);
            when(mockItem.getType()).thenReturn(Material.STONE);

            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND_SWORD));

            try {
                var field = ConditionHold.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            assertFalse(condition.match(mockData));
        }
    }
}
