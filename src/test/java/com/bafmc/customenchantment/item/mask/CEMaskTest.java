package com.bafmc.customenchantment.item.mask;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEMask Tests")
class CEMaskTest {

    @Test
    @DisplayName("should create CEMask instance")
    void shouldCreateCEMaskInstance() {
        ItemStack mockItem = mock(ItemStack.class);
        CEMask mask = new CEMask(mockItem);
        assertNotNull(mask);
    }
}
