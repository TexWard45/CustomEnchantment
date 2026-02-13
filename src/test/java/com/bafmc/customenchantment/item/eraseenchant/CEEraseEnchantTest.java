package com.bafmc.customenchantment.item.eraseenchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEEraseEnchant Tests")
class CEEraseEnchantTest {

    @Test
    @DisplayName("should create CEEraseEnchant instance")
    void shouldCreateCEEraseEnchantInstance() {
        CEEraseEnchant erase = new CEEraseEnchant();
        assertNotNull(erase);
    }
}
