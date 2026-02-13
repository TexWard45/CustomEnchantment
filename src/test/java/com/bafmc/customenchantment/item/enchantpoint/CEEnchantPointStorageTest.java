package com.bafmc.customenchantment.item.enchantpoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEEnchantPointStorage Tests")
class CEEnchantPointStorageTest {

    @Test
    @DisplayName("should create CEEnchantPointStorage instance")
    void shouldCreateCEEnchantPointStorageInstance() {
        CEEnchantPointStorage storage = new CEEnchantPointStorage();
        assertNotNull(storage);
    }
}
