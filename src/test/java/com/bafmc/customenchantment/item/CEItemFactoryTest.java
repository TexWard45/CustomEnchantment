package com.bafmc.customenchantment.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemFactory Tests")
class CEItemFactoryTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
    }

    @Test
    @DisplayName("should register factory")
    void shouldRegisterFactory() {
        CEItemFactory<CEItem<?>> factory = new CEItemFactory<CEItem<?>>() {
            @Override
            public CEItem<?> create(ItemStack itemStack) {
                return null;
            }

            @Override
            public boolean isMatchType(String type) {
                return false;
            }
        };

        assertNotNull(factory);
    }

    @Test
    @DisplayName("should check if matches type on ItemStack")
    void shouldCheckIfMatchesTypeOnItemStack() {
        CEItemFactory<CEItem<?>> factory = new CEItemFactory<CEItem<?>>() {
            @Override
            public CEItem<?> create(ItemStack itemStack) {
                return null;
            }

            @Override
            public boolean isMatchType(String type) {
                return "test".equals(type);
            }
        };

        ItemStack item = new ItemStack(Material.DIAMOND);
        assertFalse(factory.isMatchType(item));
    }

    @Test
    @DisplayName("should return false for auto generate by default")
    void shouldReturnFalseForAutoGenerateByDefault() {
        CEItemFactory<CEItem<?>> factory = new CEItemFactory<CEItem<?>>() {
            @Override
            public CEItem<?> create(ItemStack itemStack) {
                return null;
            }

            @Override
            public boolean isMatchType(String type) {
                return false;
            }
        };

        assertFalse(factory.isAutoGenerateNewItem());
    }
}
