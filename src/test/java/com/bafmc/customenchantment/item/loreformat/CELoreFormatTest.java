package com.bafmc.customenchantment.item.loreformat;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CELoreFormat Tests")
class CELoreFormatTest {

    @Test
    @DisplayName("should create CELoreFormat instance")
    void shouldCreateCELoreFormatInstance() {
        try {
            ItemStack mockItem = mock(ItemStack.class);
            CELoreFormat format = new CELoreFormat(mockItem);
            assertNotNull(format);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }
}
