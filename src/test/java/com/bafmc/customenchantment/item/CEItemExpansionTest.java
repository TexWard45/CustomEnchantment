package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemExpansion Tests")
class CEItemExpansionTest {

    @Test
    @DisplayName("should create CEItemExpansion interface reference")
    void shouldCreateCEItemExpansionInterface() {
        CEItemExpansion expansion = new CEItemExpansion() {};
        assertNotNull(expansion);
    }

    @Test
    @DisplayName("should implement CEItemExpansion interface")
    void shouldImplementCEItemExpansionInterface() {
        CEItemExpansion expansion = new CEItemExpansion() {};
        assertTrue(expansion instanceof CEItemExpansion);
    }
}
