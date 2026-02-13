package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEItemOptimizeLoader Tests")
class CEItemOptimizeLoaderTest {

    @Test
    @DisplayName("should create CEItemOptimizeLoader instance")
    void shouldCreateCEItemOptimizeLoaderInstance() {
        CEItemOptimizeLoader loader = new CEItemOptimizeLoader();
        assertNotNull(loader);
    }
}
