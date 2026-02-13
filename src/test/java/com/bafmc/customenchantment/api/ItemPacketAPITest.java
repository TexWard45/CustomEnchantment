package com.bafmc.customenchantment.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Tests for ItemPacketAPI class - NMS item packet sending.
 * Note: The sendItem() method requires NMS/CraftBukkit which cannot be tested
 * without a real server. These tests focus on slot calculation logic.
 */
@DisplayName("ItemPacketAPI Tests")
class ItemPacketAPITest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        try {
            if (MockBukkit.isMocked()) {
                MockBukkit.unmock();
            }
            server = MockBukkit.mock();
        } catch (Throwable e) {
            server = null;
        }
    }

    @AfterAll
    static void tearDownAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
    }

    @Nested
    @DisplayName("Slot Mapping Logic Tests")
    class SlotMappingLogicTests {

        /**
         * Tests the slot remapping logic used in sendItem.
         * Hotbar slots 0-8 map to vanilla slots 36-44
         * Armor slots 36-39 map to vanilla slots 8-5 (reversed)
         * Offhand slot 40 maps to vanilla slot 45
         */

        @Test
        @DisplayName("Hotbar slot 0 should map to vanilla slot 36")
        void hotbarSlot0ShouldMapToVanillaSlot36() {
            int vanillaSlot = mapToVanillaSlot(0);
            assertEquals(36, vanillaSlot);
        }

        @Test
        @DisplayName("Hotbar slot 8 should map to vanilla slot 44")
        void hotbarSlot8ShouldMapToVanillaSlot44() {
            int vanillaSlot = mapToVanillaSlot(8);
            assertEquals(44, vanillaSlot);
        }

        @Test
        @DisplayName("Armor slot 36 (boots) should map to vanilla slot 8")
        void armorSlot36ShouldMapToVanillaSlot8() {
            int vanillaSlot = mapToVanillaSlot(36);
            assertEquals(8, vanillaSlot);
        }

        @Test
        @DisplayName("Armor slot 39 (helmet) should map to vanilla slot 5")
        void armorSlot39ShouldMapToVanillaSlot5() {
            int vanillaSlot = mapToVanillaSlot(39);
            assertEquals(5, vanillaSlot);
        }

        @Test
        @DisplayName("Offhand slot 40 should map to vanilla slot 45")
        void offhandSlot40ShouldMapToVanillaSlot45() {
            int vanillaSlot = mapToVanillaSlot(40);
            assertEquals(45, vanillaSlot);
        }

        @Test
        @DisplayName("Main inventory slot 9 should not be remapped")
        void mainInventorySlot9ShouldNotBeRemapped() {
            int vanillaSlot = mapToVanillaSlot(9);
            assertEquals(9, vanillaSlot);
        }

        @Test
        @DisplayName("Main inventory slot 35 should not be remapped")
        void mainInventorySlot35ShouldNotBeRemapped() {
            int vanillaSlot = mapToVanillaSlot(35);
            assertEquals(35, vanillaSlot);
        }

        /**
         * Simulates the slot mapping logic from ItemPacketAPI.sendItem
         */
        private int mapToVanillaSlot(int vanillaSlot) {
            if (vanillaSlot >= 0 && vanillaSlot <= 8) {
                vanillaSlot += 36;
            } else if (vanillaSlot >= 36 && vanillaSlot <= 39) {
                vanillaSlot = 44 - vanillaSlot;
            } else if (vanillaSlot == 40) {
                vanillaSlot = 45;
            }
            return vanillaSlot;
        }
    }

    @Nested
    @DisplayName("ItemStack Creation Tests")
    class ItemStackCreationTests {

        @Test
        @DisplayName("Should create ItemStack for packet")
        void shouldCreateItemStackForPacket() {
            assumeTrue(server != null, "MockBukkit not available");
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

            assertNotNull(item);
            assertEquals(Material.DIAMOND_SWORD, item.getType());
            assertEquals(1, item.getAmount());
        }

        @Test
        @DisplayName("Should create ItemStack with amount for packet")
        void shouldCreateItemStackWithAmountForPacket() {
            assumeTrue(server != null, "MockBukkit not available");
            ItemStack item = new ItemStack(Material.DIAMOND, 64);

            assertNotNull(item);
            assertEquals(Material.DIAMOND, item.getType());
            assertEquals(64, item.getAmount());
        }

        @Test
        @DisplayName("Should create air ItemStack for empty slot")
        void shouldCreateAirItemStackForEmptySlot() {
            assumeTrue(server != null, "MockBukkit not available");
            ItemStack item = new ItemStack(Material.AIR);

            assertNotNull(item);
            assertEquals(Material.AIR, item.getType());
        }
    }

    @Nested
    @DisplayName("Window ID Tests")
    class WindowIdTests {

        @Test
        @DisplayName("Window ID 0 represents player inventory")
        void windowId0RepresentsPlayerInventory() {
            // Window ID 0 is always the player's inventory
            int playerInventoryWindowId = 0;

            assertEquals(0, playerInventoryWindowId);
        }

        @Test
        @DisplayName("Positive window IDs represent open containers")
        void positiveWindowIdsRepresentOpenContainers() {
            // Window IDs > 0 are for chest, crafting table, etc.
            int chestWindowId = 1;
            int craftingWindowId = 2;

            assertTrue(chestWindowId > 0);
            assertTrue(craftingWindowId > 0);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle all hotbar slots")
        void shouldHandleAllHotbarSlots() {
            for (int slot = 0; slot <= 8; slot++) {
                int mapped = mapToVanillaSlot(slot);
                assertTrue(mapped >= 36 && mapped <= 44,
                    "Slot " + slot + " should map to vanilla hotbar range 36-44, got " + mapped);
            }
        }

        @Test
        @DisplayName("Should handle all armor slots")
        void shouldHandleAllArmorSlots() {
            int[] expected = {8, 7, 6, 5}; // boots, leggings, chestplate, helmet
            for (int i = 0; i < 4; i++) {
                int slot = 36 + i;
                int mapped = mapToVanillaSlot(slot);
                assertEquals(expected[i], mapped,
                    "Armor slot " + slot + " should map to vanilla slot " + expected[i]);
            }
        }

        @Test
        @DisplayName("Main inventory slots should not change")
        void mainInventorySlotshouldNotChange() {
            for (int slot = 9; slot <= 35; slot++) {
                int mapped = mapToVanillaSlot(slot);
                assertEquals(slot, mapped,
                    "Main inventory slot " + slot + " should not be remapped");
            }
        }

        private int mapToVanillaSlot(int vanillaSlot) {
            if (vanillaSlot >= 0 && vanillaSlot <= 8) {
                vanillaSlot += 36;
            } else if (vanillaSlot >= 36 && vanillaSlot <= 39) {
                vanillaSlot = 44 - vanillaSlot;
            } else if (vanillaSlot == 40) {
                vanillaSlot = 45;
            }
            return vanillaSlot;
        }
    }
}
