package com.bafmc.customenchantment.item.gemdrill;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEGemDrillFactory Tests")
class CEGemDrillFactoryTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
    }

    @Test
    @DisplayName("should create CEGemDrillFactory instance")
    void shouldCreateCEGemDrillFactoryInstance() {
        CEGemDrillFactory factory = new CEGemDrillFactory();
        assertNotNull(factory);
    }

    @Test
    @DisplayName("should extend CEItemFactory")
    void shouldExtendCEItemFactory() {
        CEGemDrillFactory factory = new CEGemDrillFactory();
        assertTrue(factory instanceof CEItemFactory);
    }

    @Test
    @DisplayName("should create gem drill from ItemStack")
    void shouldCreateGemDrillFromItemStack() {
        CEGemDrillFactory factory = new CEGemDrillFactory();
        ItemStack item = new ItemStack(Material.DIAMOND);

        CEGemDrill drill = factory.create(item);
        assertNotNull(drill);
    }
}
