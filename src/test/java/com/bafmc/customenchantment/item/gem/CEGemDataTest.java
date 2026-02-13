package com.bafmc.customenchantment.item.gem;

import com.bafmc.customenchantment.item.CEItemData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEGemData Tests")
class CEGemDataTest {

    @Test
    @DisplayName("should extend CEItemData")
    void shouldExtendCEItemData() {
        CEGemData data = new CEGemData();
        assertTrue(data instanceof CEItemData);
    }

    @Test
    @DisplayName("should create CEGemData instance")
    void shouldCreateCEGemDataInstance() {
        CEGemData data = new CEGemData();
        assertNotNull(data);
    }

    @Test
    @DisplayName("should support cloning")
    void shouldSupportCloning() {
        CEGemData data = new CEGemData();
        CEGemData cloned = data.clone();

        assertNotNull(cloned);
        assertNotSame(data, cloned);
    }
}
