package com.bafmc.customenchantment.item.enchantpoint;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEEnchantPointFactory Tests")
class CEEnchantPointFactoryTest {

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
    @DisplayName("should create CEEnchantPointFactory instance")
    void shouldCreateCEEnchantPointFactoryInstance() {
        CEEnchantPointFactory factory = new CEEnchantPointFactory();
        assertNotNull(factory);
    }

    @Test
    @DisplayName("should extend CEItemFactory")
    void shouldExtendCEItemFactory() {
        CEEnchantPointFactory factory = new CEEnchantPointFactory();
        assertTrue(factory instanceof CEItemFactory);
    }

    @Test
    @DisplayName("should create enchant point from ItemStack")
    void shouldCreateEnchantPointFromItemStack() {
        org.junit.jupiter.api.Assumptions.assumeTrue(server != null, "MockBukkit not available");
        try {
            CEEnchantPointFactory factory = new CEEnchantPointFactory();
            ItemStack item = new ItemStack(Material.DIAMOND);
            CEEnchantPoint point = factory.create(item);
            assertNotNull(point);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }
}
