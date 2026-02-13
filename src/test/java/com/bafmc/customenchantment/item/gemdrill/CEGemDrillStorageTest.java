package com.bafmc.customenchantment.item.gemdrill;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEGemDrillStorage Tests")
class CEGemDrillStorageTest {

    @Test
    @DisplayName("should create CEGemDrillStorage instance")
    void shouldCreateCEGemDrillStorageInstance() {
        CEGemDrillStorage storage = new CEGemDrillStorage();
        assertNotNull(storage);
    }
}
