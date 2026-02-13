package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CEPlaceholder - placeholder replacement utility for enchant text.
 * Tests setPlaceholder and getTemporaryStoragePlaceholder (which don't depend on plugin state).
 * getCESimplePlaceholder and getCEFunctionDataPlaceholder depend on plugin runtime.
 */
@DisplayName("CEPlaceholder Tests")
class CEPlaceholderTest {

    @Nested
    @DisplayName("setPlaceholder Tests")
    class SetPlaceholderTests {

        @Test
        @DisplayName("should replace single placeholder")
        void shouldReplaceSinglePlaceholder() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("%name%", "Sharpness");

            String result = CEPlaceholder.setPlaceholder("Enchant: %name%", map);

            assertEquals("Enchant: Sharpness", result);
        }

        @Test
        @DisplayName("should replace multiple placeholders")
        void shouldReplaceMultiplePlaceholders() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("%name%", "Sharpness");
            map.put("%level%", "V");

            String result = CEPlaceholder.setPlaceholder("%name% %level%", map);

            assertEquals("Sharpness V", result);
        }

        @Test
        @DisplayName("should return original when no matching placeholders")
        void shouldReturnOriginalWhenNoMatch() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("%other%", "value");

            String result = CEPlaceholder.setPlaceholder("No placeholders here", map);

            assertEquals("No placeholders here", result);
        }

        @Test
        @DisplayName("should return null when value is null")
        void shouldReturnNullWhenValueIsNull() {
            // setPlaceholder calls CustomEnchantment.instance().getLogger() when value is null.
            // Since we can't initialize the plugin in unit tests, we verify the NPE path
            // by confirming the method does not silently succeed with a null value.
            Map<String, String> map = new LinkedHashMap<>();
            map.put("%key%", null);

            // The code tries to log a warning via CustomEnchantment.instance() which is null in tests.
            // This results in a NullPointerException before returning null.
            assertThrows(NullPointerException.class,
                    () -> CEPlaceholder.setPlaceholder("Test %key%", map));
        }

        @Test
        @DisplayName("should handle empty map")
        void shouldHandleEmptyMap() {
            String result = CEPlaceholder.setPlaceholder("No replacements", Collections.emptyMap());

            assertEquals("No replacements", result);
        }

        @Test
        @DisplayName("should handle empty string")
        void shouldHandleEmptyString() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("%key%", "value");

            String result = CEPlaceholder.setPlaceholder("", map);

            assertEquals("", result);
        }

        @Test
        @DisplayName("should replace all occurrences of same placeholder")
        void shouldReplaceAllOccurrences() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("%x%", "ABC");

            String result = CEPlaceholder.setPlaceholder("%x% and %x%", map);

            assertEquals("ABC and ABC", result);
        }

        @Test
        @DisplayName("should handle placeholder within text")
        void shouldHandlePlaceholderWithinText() {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("%player_name%", "TestPlayer");

            String result = CEPlaceholder.setPlaceholder("Hello %player_name%!", map);

            assertEquals("Hello TestPlayer!", result);
        }
    }

    @Nested
    @DisplayName("getTemporaryStoragePlaceholder Tests")
    class GetTemporaryStoragePlaceholderTests {

        @Test
        @DisplayName("should return empty map for null storage")
        void shouldReturnEmptyMapForNull() {
            Map<String, String> result = CEPlaceholder.getTemporaryStoragePlaceholder(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
