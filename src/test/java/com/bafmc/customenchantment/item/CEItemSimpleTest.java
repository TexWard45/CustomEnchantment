package com.bafmc.customenchantment.item;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEItemSimple Tests")
class CEItemSimpleTest {

    @Test
    @DisplayName("should create CEItemSimple instance")
    void shouldCreateCEItemSimpleInterface() {
        ItemStack mockItem = mock(ItemStack.class);
        CEItemSimple simple = new CEItemSimple(mockItem, "test-type", "test-pattern");
        assertNotNull(simple);
    }

    @Test
    @DisplayName("should be instance of CEItemSimple")
    void shouldImplementCEItemSimpleInterface() {
        ItemStack mockItem = mock(ItemStack.class);
        CEItemSimple simple = new CEItemSimple(mockItem, "test-type", "test-pattern");
        assertTrue(simple instanceof CEItemSimple);
    }
}
