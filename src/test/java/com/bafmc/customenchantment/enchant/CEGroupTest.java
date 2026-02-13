package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.utils.RandomRangeInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CEGroup - defines enchant group/rarity with display, prefix, and properties.
 * Note: getEnchantNameList() and getEnchantList() depend on CustomEnchantment.instance()
 * which requires plugin runtime, so those are not tested here.
 */
@DisplayName("CEGroup Tests")
class CEGroupTest {

    private CEGroup ceGroup;
    private RandomRangeInt success;
    private RandomRangeInt destroy;

    @BeforeEach
    void setUp() {
        success = new RandomRangeInt(80);
        destroy = new RandomRangeInt(20);

        ceGroup = new CEGroup(
                "Legendary",
                "&6Legendary",
                "&6%enchant_display% %enchant_level%",
                "&6Book: %enchant_display%",
                "&6[L] ",
                false,
                success,
                destroy,
                500,
                Priority.HIGHEST,
                true,
                true
        );
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with all parameters")
        void shouldCreateWithAllParams() {
            assertNotNull(ceGroup);
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("should return name")
        void shouldReturnName() {
            assertEquals("Legendary", ceGroup.getName());
        }

        @Test
        @DisplayName("should return display")
        void shouldReturnDisplay() {
            assertEquals("&6Legendary", ceGroup.getDisplay());
        }

        @Test
        @DisplayName("should return enchant display")
        void shouldReturnEnchantDisplay() {
            assertEquals("&6%enchant_display% %enchant_level%", ceGroup.getEnchantDisplay());
        }

        @Test
        @DisplayName("should return book display")
        void shouldReturnBookDisplay() {
            assertEquals("&6Book: %enchant_display%", ceGroup.getBookDisplay());
        }

        @Test
        @DisplayName("should return prefix")
        void shouldReturnPrefix() {
            assertEquals("&6[L] ", ceGroup.getPrefix());
        }

        @Test
        @DisplayName("should return disableEnchantLore")
        void shouldReturnDisableEnchantLore() {
            assertFalse(ceGroup.isDisableEnchantLore());
        }

        @Test
        @DisplayName("should return success range")
        void shouldReturnSuccess() {
            assertSame(success, ceGroup.getSuccess());
            assertEquals(80, ceGroup.getSuccess().getValue());
        }

        @Test
        @DisplayName("should return destroy range")
        void shouldReturnDestroy() {
            assertSame(destroy, ceGroup.getDestroy());
            assertEquals(20, ceGroup.getDestroy().getValue());
        }

        @Test
        @DisplayName("should return valuable")
        void shouldReturnValuable() {
            assertEquals(500, ceGroup.getValuable());
        }

        @Test
        @DisplayName("should return priority")
        void shouldReturnPriority() {
            assertEquals(Priority.HIGHEST, ceGroup.getPriority());
        }

        @Test
        @DisplayName("should return craft flag")
        void shouldReturnCraft() {
            assertTrue(ceGroup.isCraft());
        }

        @Test
        @DisplayName("should return filter flag")
        void shouldReturnFilter() {
            assertTrue(ceGroup.isFilter());
        }
    }

    @Nested
    @DisplayName("Different Configuration Tests")
    class DifferentConfigTests {

        @Test
        @DisplayName("should handle disabled enchant lore")
        void shouldHandleDisabledEnchantLore() {
            CEGroup group = new CEGroup("Common", "&7Common", "&7%enchant_display%", "&7Book", "&7",
                    true, new RandomRangeInt(100), new RandomRangeInt(0), 10, Priority.LOWEST, false, false);

            assertTrue(group.isDisableEnchantLore());
            assertFalse(group.isCraft());
            assertFalse(group.isFilter());
        }

        @Test
        @DisplayName("should handle zero valuable")
        void shouldHandleZeroValuable() {
            CEGroup group = new CEGroup("Free", "&f", "&f", "&f", "",
                    false, new RandomRangeInt(100), new RandomRangeInt(0), 0, Priority.NORMAL, true, true);

            assertEquals(0, group.getValuable());
        }

        @Test
        @DisplayName("should handle all priorities")
        void shouldHandleAllPriorities() {
            for (Priority priority : Priority.values()) {
                CEGroup group = new CEGroup("Test", "", "", "", "",
                        false, success, destroy, 0, priority, false, false);
                assertEquals(priority, group.getPriority());
            }
        }
    }
}
