package com.bafmc.customenchantment.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VanillaItemFactory Tests")
class VanillaItemFactoryTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
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
        VanillaItemFactory factory = new VanillaItemFactory();
        ItemStack item = new ItemStack(Material.DIAMOND);

        VanillaItem vanillaItem = factory.create(item);
        assertNotNull(vanillaItem);
    }
}
