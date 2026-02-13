package com.bafmc.customenchantment.item.mask;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEMask Tests")
class CEMaskTest {

    @Test
    @DisplayName("should create CEMask instance")
    void shouldCreateCEMaskInstance() {
        CEMask mask = new CEMask();
        assertNotNull(mask);
    }
}
