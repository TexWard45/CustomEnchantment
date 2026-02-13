package com.bafmc.customenchantment.item.banner;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEBannerFactory Tests")
class CEBannerFactoryTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
    }

    @Test
    @DisplayName("should create CEBannerFactory instance")
    void shouldCreateCEBannerFactoryInstance() {
        CEBannerFactory factory = new CEBannerFactory();
        assertNotNull(factory);
    }

    @Test
    @DisplayName("should extend CEItemFactory")
    void shouldExtendCEItemFactory() {
        CEBannerFactory factory = new CEBannerFactory();
        assertTrue(factory instanceof CEItemFactory);
    }

    @Test
    @DisplayName("should create banner from ItemStack")
    void shouldCreateBannerFromItemStack() {
        CEBannerFactory factory = new CEBannerFactory();
        ItemStack item = new ItemStack(Material.DIAMOND_HELMET);

        CEBanner banner = factory.create(item);
        assertNotNull(banner);
    }
}
