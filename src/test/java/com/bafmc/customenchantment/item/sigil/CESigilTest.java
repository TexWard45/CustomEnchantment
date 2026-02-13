package com.bafmc.customenchantment.item.sigil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CESigil Tests")
class CESigilTest {

    @Test
    @DisplayName("should create CESigil instance")
    void shouldCreateCESigilInstance() {
        CESigil sigil = new CESigil();
        assertNotNull(sigil);
    }
}
