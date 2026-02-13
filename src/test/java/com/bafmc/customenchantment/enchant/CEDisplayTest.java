package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CEDisplay - holds enchant display information (display names, descriptions).
 */
@DisplayName("CEDisplay Tests")
class CEDisplayTest {

    private CEDisplay display;
    private List<String> description;
    private List<String> detailDescription;
    private List<String> appliesDescription;

    @BeforeEach
    void setUp() {
        description = Arrays.asList("Line 1", "Line 2");
        detailDescription = Arrays.asList("Detail 1", "Detail 2", "Detail 3");
        appliesDescription = Arrays.asList("Applies to swords");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with all parameters")
        void shouldCreateWithAllParams() {
            display = new CEDisplay("TestDisplay", "BookDisplay", null, false, description, detailDescription, appliesDescription);

            assertNotNull(display);
            assertEquals("TestDisplay", display.getDefaultDisplay());
        }

        @Test
        @DisplayName("should accept null customDisplayLore")
        void shouldAcceptNullCustomDisplayLore() {
            display = new CEDisplay("Display", "BookDisplay", null, false, description, detailDescription, appliesDescription);

            assertNull(display.getCustomDisplayFormat());
        }

        @Test
        @DisplayName("should accept empty descriptions")
        void shouldAcceptEmptyDescriptions() {
            display = new CEDisplay("Display", "BookDisplay", null, false,
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

            assertTrue(display.getDescription().isEmpty());
            assertTrue(display.getAppliesDescription().isEmpty());
        }
    }

    @Nested
    @DisplayName("getDefaultDisplay Tests")
    class GetDefaultDisplayTests {

        @Test
        @DisplayName("should return the display name")
        void shouldReturnDisplayName() {
            display = new CEDisplay("Sharpness", "BookDisplay", null, false, description, detailDescription, appliesDescription);

            assertEquals("Sharpness", display.getDefaultDisplay());
        }
    }

    @Nested
    @DisplayName("getDisplay Tests")
    class GetDisplayTests {

        @Test
        @DisplayName("should return default display when customDisplayLore is null")
        void shouldReturnDefaultWhenCustomNull() {
            display = new CEDisplay("Sharpness", "BookDisplay", null, false, description, detailDescription, appliesDescription);

            assertEquals("Sharpness", display.getDisplay());
        }

        @Test
        @DisplayName("should return custom default display when customDisplayLore has default key")
        void shouldReturnCustomDefaultDisplay() {
            Map<String, String> customMap = new HashMap<>();
            customMap.put("default", "CustomSharpness");
            customMap.put("other", "OtherDisplay");

            display = new CEDisplay("Sharpness", "BookDisplay", customMap, false, description, detailDescription, appliesDescription);

            assertEquals("CustomSharpness", display.getDisplay());
        }

        @Test
        @DisplayName("should return null when customDisplayLore exists but has no default key")
        void shouldReturnNullWhenNoDefaultKey() {
            Map<String, String> customMap = new HashMap<>();
            customMap.put("other", "OtherDisplay");

            display = new CEDisplay("Sharpness", "BookDisplay", customMap, false, description, detailDescription, appliesDescription);

            assertNull(display.getDisplay());
        }
    }

    @Nested
    @DisplayName("getBookDisplay Tests")
    class GetBookDisplayTests {

        @Test
        @DisplayName("should return book display name")
        void shouldReturnBookDisplay() {
            display = new CEDisplay("Display", "MyBookDisplay", null, false, description, detailDescription, appliesDescription);

            assertEquals("MyBookDisplay", display.getBookDisplay());
        }
    }

    @Nested
    @DisplayName("isDisableEnchantLore Tests")
    class DisableEnchantLoreTests {

        @Test
        @DisplayName("should return false when not disabled")
        void shouldReturnFalseWhenNotDisabled() {
            display = new CEDisplay("Display", "BookDisplay", null, false, description, detailDescription, appliesDescription);

            assertFalse(display.isDisableEnchantLore());
        }

        @Test
        @DisplayName("should return true when disabled")
        void shouldReturnTrueWhenDisabled() {
            display = new CEDisplay("Display", "BookDisplay", null, true, description, detailDescription, appliesDescription);

            assertTrue(display.isDisableEnchantLore());
        }
    }

    @Nested
    @DisplayName("getDescription Tests")
    class GetDescriptionTests {

        @Test
        @DisplayName("should return description list")
        void shouldReturnDescription() {
            display = new CEDisplay("Display", "BookDisplay", null, false, description, detailDescription, appliesDescription);

            assertEquals(description, display.getDescription());
            assertEquals(2, display.getDescription().size());
        }
    }

    @Nested
    @DisplayName("getDetailDescription Tests")
    class GetDetailDescriptionTests {

        @Test
        @DisplayName("should return detail description when not empty")
        void shouldReturnDetailWhenNotEmpty() {
            display = new CEDisplay("Display", "BookDisplay", null, false, description, detailDescription, appliesDescription);

            assertEquals(detailDescription, display.getDetailDescription());
            assertEquals(3, display.getDetailDescription().size());
        }

        @Test
        @DisplayName("should return regular description when detail is empty")
        void shouldReturnRegularWhenDetailEmpty() {
            display = new CEDisplay("Display", "BookDisplay", null, false, description, Collections.emptyList(), appliesDescription);

            assertEquals(description, display.getDetailDescription());
        }
    }

    @Nested
    @DisplayName("getAppliesDescription Tests")
    class GetAppliesDescriptionTests {

        @Test
        @DisplayName("should return applies description")
        void shouldReturnAppliesDescription() {
            display = new CEDisplay("Display", "BookDisplay", null, false, description, detailDescription, appliesDescription);

            assertEquals(appliesDescription, display.getAppliesDescription());
            assertEquals(1, display.getAppliesDescription().size());
        }
    }

    @Nested
    @DisplayName("DescriptionMap Tests")
    class DescriptionMapTests {

        @Test
        @DisplayName("should be null initially")
        void shouldBeNullInitially() {
            display = new CEDisplay("Display", "BookDisplay", null, false, description, detailDescription, appliesDescription);

            assertNull(display.getDescriptionMap());
        }

        @Test
        @DisplayName("should set and get description map")
        void shouldSetAndGetDescriptionMap() {
            display = new CEDisplay("Display", "BookDisplay", null, false, description, detailDescription, appliesDescription);

            HashMap<Integer, List<String>> descMap = new HashMap<>();
            descMap.put(1, Arrays.asList("Level 1 desc"));
            descMap.put(2, Arrays.asList("Level 2 desc"));

            display.setDescriptionMap(descMap);

            assertNotNull(display.getDescriptionMap());
            assertEquals(2, display.getDescriptionMap().size());
            assertEquals(Arrays.asList("Level 1 desc"), display.getDescriptionMap().get(1));
        }
    }

    @Nested
    @DisplayName("getCustomDisplayFormat Tests")
    class GetCustomDisplayFormatTests {

        @Test
        @DisplayName("should return custom display lore map")
        void shouldReturnCustomDisplayLoreMap() {
            Map<String, String> customMap = new HashMap<>();
            customMap.put("default", "CustomDisplay");
            customMap.put("level1", "Level1Display");

            display = new CEDisplay("Display", "BookDisplay", customMap, false, description, detailDescription, appliesDescription);

            assertEquals(customMap, display.getCustomDisplayFormat());
            assertEquals(2, display.getCustomDisplayFormat().size());
        }
    }
}
