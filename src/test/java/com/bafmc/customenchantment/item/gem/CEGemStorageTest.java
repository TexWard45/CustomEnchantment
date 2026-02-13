package com.bafmc.customenchantment.item.gem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEGemStorage Tests")
class CEGemStorageTest {

    @Test
    @DisplayName("should create CEGemStorage instance")
    void shouldCreateCEGemStorageInstance() {
        CEGemStorage storage = new CEGemStorage();
        assertNotNull(storage);
    }
}
