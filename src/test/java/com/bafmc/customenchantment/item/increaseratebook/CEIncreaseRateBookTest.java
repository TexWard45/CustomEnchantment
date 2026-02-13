package com.bafmc.customenchantment.item.increaseratebook;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEIncreaseRateBook Tests")
class CEIncreaseRateBookTest {

    @Test
    @DisplayName("should create CEIncreaseRateBook instance")
    void shouldCreateCEIncreaseRateBookInstance() {
        CEIncreaseRateBook book = new CEIncreaseRateBook();
        assertNotNull(book);
    }
}
