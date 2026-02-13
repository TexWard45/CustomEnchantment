package com.bafmc.customenchantment.item.outfit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEOutfitGroupMap Tests")
class CEOutfitGroupMapTest {

    @Test
    @DisplayName("should create CEOutfitGroupMap instance")
    void shouldCreateCEOutfitGroupMapInstance() {
        CEOutfitGroupMap map = new CEOutfitGroupMap();
        assertNotNull(map);
    }
}
