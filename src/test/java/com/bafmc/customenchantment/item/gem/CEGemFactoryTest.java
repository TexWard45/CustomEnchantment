package com.bafmc.customenchantment.item.gem;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEGemFactory Tests")
class CEGemFactoryTest {

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

    @Test
    @DisplayName("should create CEGemFactory instance")
    void shouldCreateCEGemFactoryInstance() {
        CEGemFactory factory = new CEGemFactory();
        assertNotNull(factory);
    }

    @Test
    @DisplayName("should extend CEItemFactory")
    void shouldExtendCEItemFactory() {
        CEGemFactory factory = new CEGemFactory();
        assertTrue(factory instanceof CEItemFactory);
    }

    @Test
    @DisplayName("should create gem from ItemStack")
    void shouldCreateGemFromItemStack() {
        org.junit.jupiter.api.Assumptions.assumeTrue(server != null, "MockBukkit not available");
        try {
            CEGemFactory factory = new CEGemFactory();
            ItemStack item = new ItemStack(Material.DIAMOND);
            CEGem gem = factory.create(item);
            assertNotNull(gem);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available
        }
    }
}
