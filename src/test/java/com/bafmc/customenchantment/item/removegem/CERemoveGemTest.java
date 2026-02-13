package com.bafmc.customenchantment.item.removegem;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CERemoveGem Tests")
class CERemoveGemTest {

    @Test
    @DisplayName("should create CERemoveGem instance")
    void shouldCreateCERemoveGemInstance() {
        ItemStack mockItem = mock(ItemStack.class);
        CERemoveGem remove = new CERemoveGem(mockItem);
        assertNotNull(remove);
    }
}
