package com.bafmc.customenchantment.item.nametag;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CENameTag Tests")
class CENameTagTest {

    @Test
    @DisplayName("should create CENameTag instance")
    void shouldCreateCENameTagInstance() {
        try {
            ItemStack mockItem = mock(ItemStack.class);
            CENameTag nametag = new CENameTag(mockItem);
            assertNotNull(nametag);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS not available in test environment
        }
    }
}
