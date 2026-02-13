package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemSimple Tests")
class CEItemSimpleTest {

    @Test
    @DisplayName("should create CEItemSimple interface reference")
    void shouldCreateCEItemSimpleInterface() {
        CEItemSimple simple = new CEItemSimple() {};
        assertNotNull(simple);
    }

    @Test
    @DisplayName("should implement CEItemSimple interface")
    void shouldImplementCEItemSimpleInterface() {
        CEItemSimple simple = new CEItemSimple() {};
        assertTrue(simple instanceof CEItemSimple);
    }
}
