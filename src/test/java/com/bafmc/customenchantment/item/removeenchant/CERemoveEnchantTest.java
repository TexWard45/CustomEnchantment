package com.bafmc.customenchantment.item.removeenchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CERemoveEnchant Tests")
class CERemoveEnchantTest {

    @Test
    @DisplayName("should create CERemoveEnchant instance")
    void shouldCreateCERemoveEnchantInstance() {
        CERemoveEnchant remove = new CERemoveEnchant();
        assertNotNull(remove);
    }
}
