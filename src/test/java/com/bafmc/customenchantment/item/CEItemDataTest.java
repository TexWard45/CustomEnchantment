package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemData Tests")
class CEItemDataTest {

    @Test
    @DisplayName("should create CEItemData instance")
    void shouldCreateCEItemDataInstance() {
        CEItemData data = new CEItemData() {};
        assertNotNull(data);
    }

    @Test
    @DisplayName("should be instance of CEItemData")
    void shouldBeInstanceOfCEItemData() {
        CEItemData data = new CEItemData() {};
        assertTrue(data instanceof CEItemData);
    }

    @Test
    @DisplayName("should support multiple implementations")
    void shouldSupportMultipleImplementations() {
        CEItemData data1 = new CEItemData() {};
        CEItemData data2 = new CEItemData() {};

        assertNotNull(data1);
        assertNotNull(data2);
        assertNotSame(data1, data2);
    }
}
