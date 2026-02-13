package com.bafmc.customenchantment.item.removeenchantpoint;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CERemoveEnchantPoint Tests")
class CERemoveEnchantPointTest {

    @Test
    @DisplayName("should create CERemoveEnchantPoint instance")
    void shouldCreateCERemoveEnchantPointInstance() {
        ItemStack mockItem = mock(ItemStack.class);
        CERemoveEnchantPoint remove = new CERemoveEnchantPoint(mockItem);
        assertNotNull(remove);
    }
}
