package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemStorage Tests")
class CEItemStorageTest {

    @Test
    @DisplayName("should create CEItemStorage instance")
    void shouldCreateCEItemStorageInstance() {
        CEItemStorage storage = new CEItemStorage();
        assertNotNull(storage);
    }
}
