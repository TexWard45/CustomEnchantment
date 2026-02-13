package com.bafmc.customenchantment.item.protectdestroy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEProtectDestroy Tests")
class CEProtectDestroyTest {

    @Test
    @DisplayName("should create CEProtectDestroy instance")
    void shouldCreateCEProtectDestroyInstance() {
        CEProtectDestroy protect = new CEProtectDestroy();
        assertNotNull(protect);
    }
}
