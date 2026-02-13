package com.bafmc.customenchantment.item.protectdead;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEProtectDead Tests")
class CEProtectDeadTest {

    @Test
    @DisplayName("should create CEProtectDead instance")
    void shouldCreateCEProtectDeadInstance() {
        CEProtectDead protect = new CEProtectDead();
        assertNotNull(protect);
    }
}
