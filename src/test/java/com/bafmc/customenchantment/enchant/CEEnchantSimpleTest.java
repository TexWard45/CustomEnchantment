package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.utils.RandomRangeInt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CEEnchantSimple - simple enchant representation with name, level, success, destroy, xp.
 * Implements ILine for serialization/deserialization via colon-delimited strings.
 */
@DisplayName("CEEnchantSimple Tests")
class CEEnchantSimpleTest {

    @Nested
    @DisplayName("Constructor(String, int) Tests")
    class TwoArgConstructorTests {

        @Test
        @DisplayName("should create with name and level")
        void shouldCreateWithNameAndLevel() {
            CEEnchantSimple simple = new CEEnchantSimple("Sharpness", 3);

            assertEquals("Sharpness", simple.getName());
            assertEquals(3, simple.getLevel());
        }

        @Test
        @DisplayName("should default success to 100")
        void shouldDefaultSuccessTo100() {
            CEEnchantSimple simple = new CEEnchantSimple("Sharpness", 1);

            assertEquals(100, simple.getSuccess().getValue());
        }

        @Test
        @DisplayName("should default destroy to 0")
        void shouldDefaultDestroyTo0() {
            CEEnchantSimple simple = new CEEnchantSimple("Sharpness", 1);

            assertEquals(0, simple.getDestroy().getValue());
        }
    }

    @Nested
    @DisplayName("Constructor(String, int, RandomRangeInt, RandomRangeInt) Tests")
    class FourArgRangeConstructorTests {

        @Test
        @DisplayName("should create with custom RandomRangeInt values")
        void shouldCreateWithRandomRanges() {
            RandomRangeInt success = new RandomRangeInt(75);
            RandomRangeInt destroy = new RandomRangeInt(25);

            CEEnchantSimple simple = new CEEnchantSimple("Protection", 2, success, destroy);

            assertEquals("Protection", simple.getName());
            assertEquals(2, simple.getLevel());
            assertEquals(75, simple.getSuccess().getValue());
            assertEquals(25, simple.getDestroy().getValue());
        }
    }

    @Nested
    @DisplayName("Constructor(String, int, int, int) Tests")
    class FourArgIntConstructorTests {

        @Test
        @DisplayName("should create with integer success and destroy")
        void shouldCreateWithIntValues() {
            CEEnchantSimple simple = new CEEnchantSimple("Fire", 1, 80, 20);

            assertEquals("Fire", simple.getName());
            assertEquals(1, simple.getLevel());
            assertEquals(80, simple.getSuccess().getValue());
            assertEquals(20, simple.getDestroy().getValue());
        }
    }

    @Nested
    @DisplayName("Constructor(String, int, int, int, int) Tests")
    class FiveArgConstructorTests {

        @Test
        @DisplayName("should create with xp value")
        void shouldCreateWithXp() {
            CEEnchantSimple simple = new CEEnchantSimple("Haste", 3, 90, 10, 500);

            assertEquals("Haste", simple.getName());
            assertEquals(3, simple.getLevel());
            assertEquals(90, simple.getSuccess().getValue());
            assertEquals(10, simple.getDestroy().getValue());
            assertEquals(500, simple.getXp());
        }
    }

    @Nested
    @DisplayName("Constructor(String line) - fromLine Tests")
    class FromLineConstructorTests {

        @ParameterizedTest(name = "should parse \"{0}\"")
        @CsvSource({
                "'Sharpness:3:80:20', Sharpness, 3, 80, 20, 0",
                "'Protection:1:100:0', Protection, 1, 100, 0, 0",
                "'Speed:5:50:50:100', Speed, 5, 50, 50, 100"
        })
        @DisplayName("should parse colon-delimited string")
        void shouldParseColonDelimitedString(String line, String name, int level, int success, int destroy, int xp) {
            CEEnchantSimple simple = new CEEnchantSimple(line);

            assertEquals(name, simple.getName());
            assertEquals(level, simple.getLevel());
            assertEquals(success, simple.getSuccess().getValue());
            assertEquals(destroy, simple.getDestroy().getValue());
            assertEquals(xp, simple.getXp());
        }

        @Test
        @DisplayName("should default level to 1 when missing")
        void shouldDefaultLevelTo1() {
            CEEnchantSimple simple = new CEEnchantSimple("JustName");

            assertEquals("JustName", simple.getName());
            assertEquals(1, simple.getLevel());
        }

        @Test
        @DisplayName("should default success to 100 when missing")
        void shouldDefaultSuccessTo100() {
            CEEnchantSimple simple = new CEEnchantSimple("Name:2");

            assertEquals(100, simple.getSuccess().getValue());
        }

        @Test
        @DisplayName("should default destroy to 0 when missing")
        void shouldDefaultDestroyTo0() {
            CEEnchantSimple simple = new CEEnchantSimple("Name:2:80");

            assertEquals(0, simple.getDestroy().getValue());
        }
    }

    @Nested
    @DisplayName("toLine Tests")
    class ToLineTests {

        @Test
        @DisplayName("should serialize to colon-delimited string without xp")
        void shouldSerializeWithoutXp() {
            CEEnchantSimple simple = new CEEnchantSimple("Sharpness", 3, 80, 20);

            String line = simple.toLine();

            assertEquals("Sharpness:3:80:20", line);
        }

        @Test
        @DisplayName("should serialize to colon-delimited string with xp")
        void shouldSerializeWithXp() {
            CEEnchantSimple simple = new CEEnchantSimple("Haste", 5, 90, 10, 250);

            String line = simple.toLine();

            assertEquals("Haste:5:90:10:250", line);
        }

        @Test
        @DisplayName("should not include xp when xp is 0")
        void shouldNotIncludeZeroXp() {
            CEEnchantSimple simple = new CEEnchantSimple("Test", 1, 100, 0, 0);

            String line = simple.toLine();

            assertEquals("Test:1:100:0", line);
        }
    }

    @Nested
    @DisplayName("fromLine Tests")
    class FromLineTests {

        @Test
        @DisplayName("should overwrite existing values")
        void shouldOverwriteExistingValues() {
            CEEnchantSimple simple = new CEEnchantSimple("Original", 1);

            simple.fromLine("NewName:5:70:30:200");

            assertEquals("NewName", simple.getName());
            assertEquals(5, simple.getLevel());
            assertEquals(70, simple.getSuccess().getValue());
            assertEquals(30, simple.getDestroy().getValue());
            assertEquals(200, simple.getXp());
        }
    }

    @Nested
    @DisplayName("Roundtrip Tests")
    class RoundtripTests {

        @Test
        @DisplayName("should roundtrip without xp")
        void shouldRoundtripWithoutXp() {
            CEEnchantSimple original = new CEEnchantSimple("Sharpness", 3, 80, 20);
            String line = original.toLine();
            CEEnchantSimple deserialized = new CEEnchantSimple(line);

            assertEquals(original.getName(), deserialized.getName());
            assertEquals(original.getLevel(), deserialized.getLevel());
            assertEquals(original.getSuccess().getValue(), deserialized.getSuccess().getValue());
            assertEquals(original.getDestroy().getValue(), deserialized.getDestroy().getValue());
        }

        @Test
        @DisplayName("should roundtrip with xp")
        void shouldRoundtripWithXp() {
            CEEnchantSimple original = new CEEnchantSimple("Haste", 5, 90, 10, 300);
            String line = original.toLine();
            CEEnchantSimple deserialized = new CEEnchantSimple(line);

            assertEquals(original.getName(), deserialized.getName());
            assertEquals(original.getLevel(), deserialized.getLevel());
            assertEquals(original.getSuccess().getValue(), deserialized.getSuccess().getValue());
            assertEquals(original.getDestroy().getValue(), deserialized.getDestroy().getValue());
            assertEquals(original.getXp(), deserialized.getXp());
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("should contain all fields")
        void shouldContainAllFields() {
            CEEnchantSimple simple = new CEEnchantSimple("Test", 2, 80, 20, 100);

            String str = simple.toString();

            assertTrue(str.contains("name=Test"));
            assertTrue(str.contains("level=2"));
            assertTrue(str.contains("xp=100"));
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("should set name via setter")
        void shouldSetName() {
            CEEnchantSimple simple = new CEEnchantSimple("Old", 1);
            simple.setName("New");

            assertEquals("New", simple.getName());
        }

        @Test
        @DisplayName("should set level via setter")
        void shouldSetLevel() {
            CEEnchantSimple simple = new CEEnchantSimple("Test", 1);
            simple.setLevel(10);

            assertEquals(10, simple.getLevel());
        }

        @Test
        @DisplayName("should set success via setter")
        void shouldSetSuccess() {
            CEEnchantSimple simple = new CEEnchantSimple("Test", 1);
            simple.setSuccess(new RandomRangeInt(50));

            assertEquals(50, simple.getSuccess().getValue());
        }

        @Test
        @DisplayName("should set destroy via setter")
        void shouldSetDestroy() {
            CEEnchantSimple simple = new CEEnchantSimple("Test", 1);
            simple.setDestroy(new RandomRangeInt(75));

            assertEquals(75, simple.getDestroy().getValue());
        }

        @Test
        @DisplayName("should set xp via setter")
        void shouldSetXp() {
            CEEnchantSimple simple = new CEEnchantSimple("Test", 1);
            simple.setXp(999);

            assertEquals(999, simple.getXp());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle zero level")
        void shouldHandleZeroLevel() {
            CEEnchantSimple simple = new CEEnchantSimple("Test", 0);

            assertEquals(0, simple.getLevel());
        }

        @Test
        @DisplayName("should handle max success rate")
        void shouldHandleMaxSuccessRate() {
            CEEnchantSimple simple = new CEEnchantSimple("Test", 1, 100, 0);

            assertEquals(100, simple.getSuccess().getValue());
        }

        @Test
        @DisplayName("should handle zero success rate")
        void shouldHandleZeroSuccessRate() {
            CEEnchantSimple simple = new CEEnchantSimple("Test", 1, 0, 100);

            assertEquals(0, simple.getSuccess().getValue());
        }
    }
}
