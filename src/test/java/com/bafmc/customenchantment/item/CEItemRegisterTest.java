package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemRegister Tests")
class CEItemRegisterTest {

    @Test
    @DisplayName("should provide instance")
    void shouldProvideInstance() {
        CEItemRegister register = CEItemRegister.instance();
        assertNotNull(register);
    }

    @Test
    @DisplayName("should return same instance on multiple calls")
    void shouldReturnSameInstanceOnMultipleCalls() {
        CEItemRegister register1 = CEItemRegister.instance();
        CEItemRegister register2 = CEItemRegister.instance();

        assertEquals(register1, register2);
    }
}
