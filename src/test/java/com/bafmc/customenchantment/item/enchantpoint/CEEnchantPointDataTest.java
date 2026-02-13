package com.bafmc.customenchantment.item.enchantpoint;

import com.bafmc.customenchantment.item.CEItemData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEEnchantPointData Tests")
class CEEnchantPointDataTest {

    @Test
    @DisplayName("should extend CEItemData")
    void shouldExtendCEItemData() {
        CEEnchantPointData data = new CEEnchantPointData();
        assertTrue(data instanceof CEItemData);
    }

    @Test
    @DisplayName("should create CEEnchantPointData instance")
    void shouldCreateCEEnchantPointDataInstance() {
        CEEnchantPointData data = new CEEnchantPointData();
        assertNotNull(data);
    }
}
