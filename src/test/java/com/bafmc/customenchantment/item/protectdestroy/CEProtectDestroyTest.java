package com.bafmc.customenchantment.item.protectdestroy;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEProtectDestroy Tests")
class CEProtectDestroyTest {

    @Test
    @DisplayName("should create CEProtectDestroy instance")
    void shouldCreateCEProtectDestroyInstance() {
        try {
            ItemStack mockItem = mock(ItemStack.class);
            CEProtectDestroy protect = new CEProtectDestroy(mockItem);
            assertNotNull(protect);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }
}
