package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEItemOptimizeLoader Tests")
class CEItemOptimizeLoaderTest {

    @Test
    @DisplayName("should create CEItemOptimizeLoader instance")
    void shouldCreateCEItemOptimizeLoaderInstance() {
        ItemStack mockItem = mock(ItemStack.class);
        CEItemOptimizeLoader loader = new CEItemOptimizeLoader(mockItem);
        assertNotNull(loader);
    }
}
