package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.api.MaterialData;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionItemConsume - checks if consumed item material is in the allowed list.
 */
@DisplayName("ConditionItemConsume Tests")
class ConditionItemConsumeTest {

    private ConditionItemConsume condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionItemConsume();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return ITEM_CONSUME")
        void shouldReturnCorrectIdentifier() {
            assertEquals("ITEM_CONSUME", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("match Tests - Null ItemConsume")
    class NullItemConsumeTests {

        @Test
        @DisplayName("should return false when item consume is null")
        void shouldReturnFalseWhenItemConsumeNull() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.GOLDEN_APPLE));

            try {
                var field = ConditionItemConsume.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            when(mockData.getItemConsume()).thenReturn(null);

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - With ItemConsume")
    class WithItemConsumeTests {

        @Test
        @DisplayName("should return true when consumed item is in list")
        void shouldReturnTrueWhenItemInList() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.GOLDEN_APPLE));
            list.add(new MaterialData(Material.ENCHANTED_GOLDEN_APPLE));

            try {
                var field = ConditionItemConsume.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            ItemStack mockItem = mock(ItemStack.class);
            when(mockItem.getType()).thenReturn(Material.GOLDEN_APPLE);
            when(mockData.getItemConsume()).thenReturn(mockItem);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when consumed item is not in list")
        void shouldReturnFalseWhenItemNotInList() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.GOLDEN_APPLE));

            try {
                var field = ConditionItemConsume.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            ItemStack mockItem = mock(ItemStack.class);
            when(mockItem.getType()).thenReturn(Material.BREAD);
            when(mockData.getItemConsume()).thenReturn(mockItem);

            assertFalse(condition.match(mockData));
        }

        @Test
        @DisplayName("should match POTION item type")
        void shouldMatchPotionItemType() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.POTION));

            try {
                var field = ConditionItemConsume.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            ItemStack mockItem = mock(ItemStack.class);
            when(mockItem.getType()).thenReturn(Material.POTION);
            when(mockData.getItemConsume()).thenReturn(mockItem);

            assertTrue(condition.match(mockData));
        }
    }
}
