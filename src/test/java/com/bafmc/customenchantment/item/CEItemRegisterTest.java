package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemRegister Tests")
class CEItemRegisterTest {

    @Test
    @DisplayName("should have static list field")
    void shouldHaveStaticListField() {
        assertNotNull(CEItemRegister.list);
    }

    @Test
    @DisplayName("should return null for null ItemStack")
    void shouldReturnNullForNullItemStack() {
        assertNull(CEItemRegister.getCEItem(null));
    }
}
