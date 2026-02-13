package com.bafmc.customenchantment.item.banner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEBannerStorage Tests")
class CEBannerStorageTest {

    @Test
    @DisplayName("should create CEBannerStorage instance")
    void shouldCreateCEBannerStorageInstance() {
        CEBannerStorage storage = new CEBannerStorage();
        assertNotNull(storage);
    }
}
