package com.bafmc.customenchantment.item.loreformat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CELoreFormat Tests")
class CELoreFormatTest {

    @Test
    @DisplayName("should create CELoreFormat instance")
    void shouldCreateCELoreFormatInstance() {
        CELoreFormat format = new CELoreFormat();
        assertNotNull(format);
    }
}
