package com.bafmc.customenchantment.item.enchantpoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEEnchantPointSimple Tests")
class CEEnchantPointSimpleTest {

    @Test
    @DisplayName("should create CEEnchantPointSimple instance")
    void shouldCreateCEEnchantPointSimpleInstance() {
        CEEnchantPointSimple simple = new CEEnchantPointSimple("test-pattern", 0);
        assertNotNull(simple);
    }
}
