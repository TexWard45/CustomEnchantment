package com.bafmc.customenchantment.item.removeenchantpoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CERemoveEnchantPoint Tests")
class CERemoveEnchantPointTest {

    @Test
    @DisplayName("should create CERemoveEnchantPoint instance")
    void shouldCreateCERemoveEnchantPointInstance() {
        CERemoveEnchantPoint remove = new CERemoveEnchantPoint();
        assertNotNull(remove);
    }
}
