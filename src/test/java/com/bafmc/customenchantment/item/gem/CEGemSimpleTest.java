package com.bafmc.customenchantment.item.gem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEGemSimple Tests")
class CEGemSimpleTest {

    @Test
    @DisplayName("should create CEGemSimple with pattern and level")
    void shouldCreateCEGemSimpleWithPatternAndLevel() {
        CEGemSimple gem = new CEGemSimple("ruby", 1);
        assertNotNull(gem);
    }

    @Test
    @DisplayName("should store pattern")
    void shouldStorePattern() {
        CEGemSimple gem = new CEGemSimple("sapphire", 2);
        assertEquals("sapphire", gem.getName());
    }

    @Test
    @DisplayName("should store level")
    void shouldStoreLevel() {
        CEGemSimple gem = new CEGemSimple("emerald", 3);
        assertEquals(3, gem.getLevel());
    }

    @Test
    @DisplayName("should handle level 1")
    void shouldHandleLevel1() {
        CEGemSimple gem = new CEGemSimple("diamond", 1);
        assertEquals(1, gem.getLevel());
    }

    @Test
    @DisplayName("should handle higher levels")
    void shouldHandleHigherLevels() {
        CEGemSimple gem = new CEGemSimple("ruby", 10);
        assertEquals(10, gem.getLevel());
    }

    @Test
    @DisplayName("should handle null pattern")
    void shouldHandleNullPattern() {
        CEGemSimple gem = new CEGemSimple(null, 1);
        assertNull(gem.getName());
    }
}
