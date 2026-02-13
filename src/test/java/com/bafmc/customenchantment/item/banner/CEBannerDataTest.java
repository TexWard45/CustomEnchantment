package com.bafmc.customenchantment.item.banner;

import com.bafmc.customenchantment.item.CEItemData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEBannerData Tests")
class CEBannerDataTest {

    @Test
    @DisplayName("should extend CEItemData")
    void shouldExtendCEItemData() {
        CEBannerData data = new CEBannerData();
        assertTrue(data instanceof CEItemData);
    }

    @Test
    @DisplayName("should create CEBannerData instance")
    void shouldCreateCEBannerDataInstance() {
        CEBannerData data = new CEBannerData();
        assertNotNull(data);
    }
}
