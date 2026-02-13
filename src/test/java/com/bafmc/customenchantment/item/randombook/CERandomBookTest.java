package com.bafmc.customenchantment.item.randombook;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CERandomBook Tests")
class CERandomBookTest {

    @Test
    @DisplayName("should create CERandomBook instance")
    void shouldCreateCERandomBookInstance() {
        ItemStack mockItem = mock(ItemStack.class);
        CERandomBook book = new CERandomBook(mockItem);
        assertNotNull(book);
    }
}
