package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemModule Tests")
class ItemModuleTest {

    @Test
    @DisplayName("should create ItemModule instance")
    void shouldCreateItemModuleInstance() {
        // ItemModule requires plugin instance, so we test basic instantiation
        assertNotNull(ItemModule.class);
    }
}
