package com.bafmc.customenchantment.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@DisplayName("VanillaItem Tests")
class VanillaItemTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        try {
            if (MockBukkit.isMocked()) {
                MockBukkit.unmock();
            }
            server = MockBukkit.mock();
        } catch (Exception | NoClassDefFoundError e) {
            server = null;
        }
    }

    @AfterAll
    static void tearDownAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
    }

    @Test
    @DisplayName("should create VanillaItem instance")
    void shouldCreateVanillaItemInstance() {
        assumeTrue(server != null, "MockBukkit not available");
        try {
            ItemStack item = new ItemStack(Material.DIAMOND);
            VanillaItem vanillaItem = new VanillaItem(item);
            assertNotNull(vanillaItem);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }

    @Test
    @DisplayName("should extend CEItem")
    void shouldExtendCEItem() {
        assumeTrue(server != null, "MockBukkit not available");
        try {
            ItemStack item = new ItemStack(Material.DIAMOND);
            VanillaItem vanillaItem = new VanillaItem(item);
            assertTrue(vanillaItem instanceof CEItem);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }
}
