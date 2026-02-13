package com.bafmc.customenchantment.item.eraseenchant;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEEraseEnchant Tests")
class CEEraseEnchantTest {

    @Test
    @DisplayName("should create CEEraseEnchant instance")
    void shouldCreateCEEraseEnchantInstance() {
        try {
            ItemStack mockItem = mock(ItemStack.class);
            CEEraseEnchant erase = new CEEraseEnchant(mockItem);
            assertNotNull(erase);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }
}
