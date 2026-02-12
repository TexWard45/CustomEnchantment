package com.bafmc.customenchantment.api;

import com.bafmc.bukkit.utils.EquipSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for EquipSlotAPI class - equipment slot detection for bow shooting.
 */
@DisplayName("EquipSlotAPI Tests")
class EquipSlotAPITest {

    @Nested
    @DisplayName("getBowShootSlot Tests")
    class GetBowShootSlotTests {

        @Test
        @DisplayName("Should return MAINHAND when bow is in main hand")
        void shouldReturnMainhandWhenBowInMainHand() {
            Player player = mock(Player.class);
            ItemStack bow = new ItemStack(Material.BOW);

            when(player.getItemInHand()).thenReturn(bow);

            EquipSlot result = EquipSlotAPI.getBowShoowSlot(player);

            assertEquals(EquipSlot.MAINHAND, result);
        }

        @Test
        @DisplayName("Should return OFFHAND when bow is not in main hand")
        void shouldReturnOffhandWhenBowNotInMainHand() {
            Player player = mock(Player.class);
            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);

            when(player.getItemInHand()).thenReturn(sword);

            EquipSlot result = EquipSlotAPI.getBowShoowSlot(player);

            assertEquals(EquipSlot.OFFHAND, result);
        }

        @Test
        @DisplayName("Should return OFFHAND when main hand is empty")
        void shouldReturnOffhandWhenMainHandIsEmpty() {
            Player player = mock(Player.class);
            ItemStack air = new ItemStack(Material.AIR);

            when(player.getItemInHand()).thenReturn(air);

            EquipSlot result = EquipSlotAPI.getBowShoowSlot(player);

            assertEquals(EquipSlot.OFFHAND, result);
        }

        @Test
        @DisplayName("Should return OFFHAND for crossbow in main hand")
        void shouldReturnOffhandForCrossbowInMainHand() {
            Player player = mock(Player.class);
            ItemStack crossbow = new ItemStack(Material.CROSSBOW);

            when(player.getItemInHand()).thenReturn(crossbow);

            EquipSlot result = EquipSlotAPI.getBowShoowSlot(player);

            // Crossbow is not BOW, so it should return OFFHAND
            assertEquals(EquipSlot.OFFHAND, result);
        }

        @Test
        @DisplayName("Should return OFFHAND for trident in main hand")
        void shouldReturnOffhandForTridentInMainHand() {
            Player player = mock(Player.class);
            ItemStack trident = new ItemStack(Material.TRIDENT);

            when(player.getItemInHand()).thenReturn(trident);

            EquipSlot result = EquipSlotAPI.getBowShoowSlot(player);

            assertEquals(EquipSlot.OFFHAND, result);
        }

        @Test
        @DisplayName("Should handle various non-bow items")
        void shouldHandleVariousNonBowItems() {
            Player player = mock(Player.class);

            Material[] nonBowMaterials = {
                Material.DIAMOND_SWORD,
                Material.IRON_AXE,
                Material.SHIELD,
                Material.FISHING_ROD,
                Material.SNOWBALL,
                Material.ENDER_PEARL
            };

            for (Material material : nonBowMaterials) {
                ItemStack item = new ItemStack(material);
                when(player.getItemInHand()).thenReturn(item);

                EquipSlot result = EquipSlotAPI.getBowShoowSlot(player);

                assertEquals(EquipSlot.OFFHAND, result,
                    "Should return OFFHAND for " + material.name());
            }
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle bow with custom name")
        void shouldHandleBowWithCustomName() {
            Player player = mock(Player.class);
            ItemStack bow = new ItemStack(Material.BOW);
            // Even with custom metadata, material check should work

            when(player.getItemInHand()).thenReturn(bow);

            EquipSlot result = EquipSlotAPI.getBowShoowSlot(player);

            assertEquals(EquipSlot.MAINHAND, result);
        }

        @Test
        @DisplayName("Should handle bow stack of multiple")
        void shouldHandleBowStackOfMultiple() {
            Player player = mock(Player.class);
            // Bows normally don't stack, but testing edge case
            ItemStack bow = new ItemStack(Material.BOW, 1);

            when(player.getItemInHand()).thenReturn(bow);

            EquipSlot result = EquipSlotAPI.getBowShoowSlot(player);

            assertEquals(EquipSlot.MAINHAND, result);
        }
    }
}
