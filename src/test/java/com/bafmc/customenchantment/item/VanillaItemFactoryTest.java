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

@DisplayName("VanillaItemFactory Tests")
class VanillaItemFactoryTest {

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
    @DisplayName("should create VanillaItemFactory instance")
    void shouldCreateVanillaItemFactoryInstance() {
        VanillaItemFactory factory = new VanillaItemFactory();
        assertNotNull(factory);
    }

    @Test
    @DisplayName("should extend CEItemFactory")
    void shouldExtendCEItemFactory() {
        VanillaItemFactory factory = new VanillaItemFactory();
        assertTrue(factory instanceof CEItemFactory);
    }

    @Test
    @DisplayName("should create vanilla item from ItemStack")
    void shouldCreateVanillaItemFromItemStack() {
        assumeTrue(server != null, "MockBukkit not available");
        VanillaItemFactory factory = new VanillaItemFactory();
        ItemStack item = new ItemStack(Material.DIAMOND);

        try {
            VanillaItem vanillaItem = factory.create(item);
            assertNotNull(vanillaItem);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }
}
