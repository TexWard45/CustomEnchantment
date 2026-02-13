package com.bafmc.customenchantment.item.increaseratebook;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEIncreaseRateBook Tests")
class CEIncreaseRateBookTest {

    @Test
    @DisplayName("should create CEIncreaseRateBook instance")
    void shouldCreateCEIncreaseRateBookInstance() {
        ItemStack mockItem = mock(ItemStack.class);
        CEIncreaseRateBook book = new CEIncreaseRateBook(mockItem);
        assertNotNull(book);
    }
}
