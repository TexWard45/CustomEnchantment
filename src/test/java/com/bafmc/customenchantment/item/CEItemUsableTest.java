package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemUsable Tests")
class CEItemUsableTest {

    @Test
    @DisplayName("should create CEItemUsable interface reference")
    void shouldCreateCEItemUsableInterface() {
        CEItemUsable usable = new CEItemUsable() {};
        assertNotNull(usable);
    }

    @Test
    @DisplayName("should implement CEItemUsable interface")
    void shouldImplementCEItemUsableInterface() {
        CEItemUsable usable = new CEItemUsable() {};
        assertTrue(usable instanceof CEItemUsable);
    }
}
