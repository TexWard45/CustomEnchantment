package com.bafmc.customenchantment.item.randombook;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CERandomBook Tests")
class CERandomBookTest {

    @Test
    @DisplayName("should create CERandomBook instance")
    void shouldCreateCERandomBookInstance() {
        CERandomBook book = new CERandomBook();
        assertNotNull(book);
    }
}
