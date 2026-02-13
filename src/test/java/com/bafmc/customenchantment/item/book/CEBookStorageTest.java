package com.bafmc.customenchantment.item.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEBookStorage Tests")
class CEBookStorageTest {

    @Test
    @DisplayName("should create CEBookStorage instance")
    void shouldCreateCEBookStorageInstance() {
        CEBookStorage storage = new CEBookStorage();
        assertNotNull(storage);
    }
}
