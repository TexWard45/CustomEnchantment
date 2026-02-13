package com.bafmc.customenchantment.item.outfit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEOutfit Tests")
class CEOutfitTest {

    @Test
    @DisplayName("should create CEOutfit instance")
    void shouldCreateCEOutfitInstance() {
        CEOutfit outfit = new CEOutfit();
        assertNotNull(outfit);
    }
}
