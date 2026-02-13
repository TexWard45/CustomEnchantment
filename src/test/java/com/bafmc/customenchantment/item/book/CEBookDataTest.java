package com.bafmc.customenchantment.item.book;

import com.bafmc.customenchantment.item.CEItemData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEBookData Tests")
class CEBookDataTest {

    @Test
    @DisplayName("should extend CEItemData")
    void shouldExtendCEItemData() {
        CEBookData data = new CEBookData();
        assertTrue(data instanceof CEItemData);
    }

    @Test
    @DisplayName("should create CEBookData instance")
    void shouldCreateCEBookDataInstance() {
        CEBookData data = new CEBookData();
        assertNotNull(data);
    }
}
