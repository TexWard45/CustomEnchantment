package com.bafmc.customenchantment.item.gemdrill;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEGemDrillSimple Tests")
class CEGemDrillSimpleTest {

    @Test
    @DisplayName("should create CEGemDrillSimple instance")
    void shouldCreateCEGemDrillSimpleInstance() {
        CEGemDrillSimple simple = new CEGemDrillSimple();
        assertNotNull(simple);
    }
}
