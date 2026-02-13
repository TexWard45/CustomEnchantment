package com.bafmc.customenchantment.item.sigil;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CESigil Tests")
class CESigilTest {

    @Test
    @DisplayName("should create CESigil instance")
    void shouldCreateCESigilInstance() {
        ItemStack mockItem = mock(ItemStack.class);
        CESigil sigil = new CESigil(mockItem);
        assertNotNull(sigil);
    }
}
