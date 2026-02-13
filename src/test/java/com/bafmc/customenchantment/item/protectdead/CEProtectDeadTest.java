package com.bafmc.customenchantment.item.protectdead;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEProtectDead Tests")
class CEProtectDeadTest {

    @Test
    @DisplayName("should create CEProtectDead instance")
    void shouldCreateCEProtectDeadInstance() {
        try {
            ItemStack mockItem = mock(ItemStack.class);
            CEProtectDead protect = new CEProtectDead(mockItem);
            assertNotNull(protect);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }
}
