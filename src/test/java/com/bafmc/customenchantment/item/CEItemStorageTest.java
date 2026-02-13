package com.bafmc.customenchantment.item;

import com.bafmc.customenchantment.api.Parameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemStorage Tests")
class CEItemStorageTest {

    @Test
    @DisplayName("should create CEItemStorage subclass instance")
    void shouldCreateCEItemStorageInstance() {
        CEItemStorage<?> storage = new CEItemStorage<>() {
            @Override
            public CEItem<?> getByParameter(Parameter parameter) {
                return null;
            }
        };
        assertNotNull(storage);
    }
}
