package com.bafmc.customenchantment.item.removegem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CERemoveGem Tests")
class CERemoveGemTest {

    @Test
    @DisplayName("should create CERemoveGem instance")
    void shouldCreateCERemoveGemInstance() {
        CERemoveGem remove = new CERemoveGem();
        assertNotNull(remove);
    }
}
