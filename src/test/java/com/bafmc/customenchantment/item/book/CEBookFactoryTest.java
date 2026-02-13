package com.bafmc.customenchantment.item.book;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEBookFactory Tests")
class CEBookFactoryTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
    }

    @Test
    @DisplayName("should create CEBookFactory instance")
    void shouldCreateCEBookFactoryInstance() {
        CEBookFactory factory = new CEBookFactory();
        assertNotNull(factory);
    }

    @Test
    @DisplayName("should extend CEItemFactory")
    void shouldExtendCEItemFactory() {
        CEBookFactory factory = new CEBookFactory();
        assertTrue(factory instanceof CEItemFactory);
    }

    @Test
    @DisplayName("should create book from ItemStack")
    void shouldCreateBookFromItemStack() {
        CEBookFactory factory = new CEBookFactory();
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);

        CEBook book = factory.create(item);
        assertNotNull(book);
    }
}
