package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VanillaItemData Tests")
class VanillaItemDataTest {

    @Test
    @DisplayName("should extend CEItemData")
    void shouldExtendCEItemData() {
        VanillaItemData data = new VanillaItemData(false, false);
        assertTrue(data instanceof CEItemData);
    }

    @Test
    @DisplayName("should create VanillaItemData instance")
    void shouldCreateVanillaItemDataInstance() {
        VanillaItemData data = new VanillaItemData(false, false);
        assertNotNull(data);
    }
}
