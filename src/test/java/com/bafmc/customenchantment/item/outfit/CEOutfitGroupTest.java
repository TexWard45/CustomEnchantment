package com.bafmc.customenchantment.item.outfit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEOutfitGroup Tests")
class CEOutfitGroupTest {

    @Test
    @DisplayName("should create CEOutfitGroup instance via builder")
    void shouldCreateCEOutfitGroupInstance() {
        CEOutfitGroup group = CEOutfitGroup.builder()
                .name("test")
                .display("Test Display")
                .itemDisplay("Test Item Display")
                .itemLore(new ArrayList<>())
                .build();
        assertNotNull(group);
    }
}
