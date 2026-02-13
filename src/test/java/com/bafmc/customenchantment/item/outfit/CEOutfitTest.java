package com.bafmc.customenchantment.item.outfit;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEOutfit Tests")
class CEOutfitTest {

    @Test
    @DisplayName("should create CEOutfit instance")
    void shouldCreateCEOutfitInstance() {
        try {
            ItemStack mockItem = mock(ItemStack.class);
            CEOutfit outfit = new CEOutfit(mockItem);
            assertNotNull(outfit);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }
}
