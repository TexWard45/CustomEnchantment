package com.bafmc.customenchantment.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VanillaItem Tests")
class VanillaItemTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
    }

    @Test
    @DisplayName("should create VanillaItem instance")
    void shouldCreateVanillaItemInstance() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        VanillaItem vanillaItem = new VanillaItem(item);
        assertNotNull(vanillaItem);
    }

    @Test
    @DisplayName("should extend CEItem")
    void shouldExtendCEItem() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        VanillaItem vanillaItem = new VanillaItem(item);
        assertTrue(vanillaItem instanceof CEItem);
    }
}
