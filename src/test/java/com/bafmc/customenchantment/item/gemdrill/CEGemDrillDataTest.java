package com.bafmc.customenchantment.item.gemdrill;

import com.bafmc.customenchantment.item.CEItemData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEGemDrillData Tests")
class CEGemDrillDataTest {

    @Test
    @DisplayName("should extend CEItemData")
    void shouldExtendCEItemData() {
        CEGemDrillData data = new CEGemDrillData();
        assertTrue(data instanceof CEItemData);
    }

    @Test
    @DisplayName("should create CEGemDrillData instance")
    void shouldCreateCEGemDrillDataInstance() {
        CEGemDrillData data = new CEGemDrillData();
        assertNotNull(data);
    }
}
