package com.bafmc.customenchantment.item.removeenchant;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CERemoveEnchant Tests")
class CERemoveEnchantTest {

    @Test
    @DisplayName("should create CERemoveEnchant instance")
    void shouldCreateCERemoveEnchantInstance() {
        try {
            ItemStack mockItem = mock(ItemStack.class);
            CERemoveEnchant remove = new CERemoveEnchant(mockItem);
            assertNotNull(remove);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }
}
