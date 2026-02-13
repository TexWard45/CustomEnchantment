package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VanillaItemStorage Tests")
class VanillaItemStorageTest {

    @Test
    @DisplayName("should create VanillaItemStorage instance")
    void shouldCreateVanillaItemStorageInstance() {
        VanillaItemStorage storage = new VanillaItemStorage();
        assertNotNull(storage);
    }
}
