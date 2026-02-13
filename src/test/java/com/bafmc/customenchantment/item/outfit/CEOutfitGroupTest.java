package com.bafmc.customenchantment.item.outfit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEOutfitGroup Tests")
class CEOutfitGroupTest {

    @Test
    @DisplayName("should create CEOutfitGroup instance")
    void shouldCreateCEOutfitGroupInstance() {
        CEOutfitGroup group = new CEOutfitGroup();
        assertNotNull(group);
    }
}
