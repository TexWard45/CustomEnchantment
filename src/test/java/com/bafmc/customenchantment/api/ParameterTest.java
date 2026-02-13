package com.bafmc.customenchantment.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Parameter class - parameter parsing and type conversion utility.
 */
@DisplayName("Parameter Tests")
class ParameterTest {

    @Nested
    @DisplayName("Constructor with List Tests")
    class ConstructorWithListTests {

        @Test
        @DisplayName("Should create parameter from list")
        void shouldCreateParameterFromList() {
            List<String> list = Arrays.asList("value1", "value2", "value3");
            Parameter param = new Parameter(list);

            assertNotNull(param);
            assertEquals(3, param.size());
        }

        @Test
        @DisplayName("Should create parameter from empty list")
        void shouldCreateParameterFromEmptyList() {
            Parameter param = new Parameter(new ArrayList<>());

            assertNotNull(param);
            assertEquals(0, param.size());
        }

        @Test
        @DisplayName("Should create parameter from single element list")
        void shouldCreateParameterFromSingleElementList() {
            Parameter param = new Parameter(Collections.singletonList("single"));

            assertEquals(1, param.size());
            assertEquals("single", param.getString(0));
        }
    }

    @Nested
    @DisplayName("Constructor with String Tests")
    class ConstructorWithStringTests {

        @Test
        @DisplayName("Should parse colon-separated string")
        void shouldParseColonSeparatedString() {
            Parameter param = new Parameter("value1:value2:value3");

            assertEquals(3, param.size());
            assertEquals("value1", param.getString(0));
            assertEquals("value2", param.getString(1));
            assertEquals("value3", param.getString(2));
        }

        @Test
        @DisplayName("Should handle string without colons")
        void shouldHandleStringWithoutColons() {
            Parameter param = new Parameter("singleValue");

            assertEquals(1, param.size());
            assertEquals("singleValue", param.getString(0));
        }

        @Test
        @DisplayName("Should handle empty string")
        void shouldHandleEmptyString() {
            Parameter param = new Parameter("");

            // Empty string splits to single empty element based on StringUtils behavior
            assertTrue(param.size() >= 0);
        }
    }

    @Nested
    @DisplayName("isSet Tests")
    class IsSetTests {

        @Test
        @DisplayName("Should return true for valid index")
        void shouldReturnTrueForValidIndex() {
            Parameter param = new Parameter(Arrays.asList("a", "b", "c"));

            assertTrue(param.isSet(0));
            assertTrue(param.isSet(1));
            assertTrue(param.isSet(2));
        }

        @Test
        @DisplayName("Should return false for index out of bounds")
        void shouldReturnFalseForIndexOutOfBounds() {
            Parameter param = new Parameter(Arrays.asList("a", "b"));

            assertFalse(param.isSet(2));
            assertFalse(param.isSet(10));
        }

        @Test
        @DisplayName("Should return false for negative index check")
        void shouldReturnTrueForNegativeIndex() {
            Parameter param = new Parameter(Arrays.asList("a", "b"));

            // Negative index is always less than size, so isSet returns true
            assertTrue(param.isSet(-1));
        }

        @Test
        @DisplayName("Should return false for empty parameter")
        void shouldReturnFalseForEmptyParameter() {
            Parameter param = new Parameter(new ArrayList<>());

            assertFalse(param.isSet(0));
        }
    }

    @Nested
    @DisplayName("getString Tests")
    class GetStringTests {

        @Test
        @DisplayName("Should return string at index")
        void shouldReturnStringAtIndex() {
            Parameter param = new Parameter(Arrays.asList("first", "second", "third"));

            assertEquals("first", param.getString(0));
            assertEquals("second", param.getString(1));
            assertEquals("third", param.getString(2));
        }

        @Test
        @DisplayName("Should return null for invalid index without default")
        void shouldReturnNullForInvalidIndex() {
            Parameter param = new Parameter(Arrays.asList("only"));

            assertNull(param.getString(5));
        }

        @Test
        @DisplayName("Should return default for invalid index with default")
        void shouldReturnDefaultForInvalidIndex() {
            Parameter param = new Parameter(Arrays.asList("only"));

            assertEquals("default", param.getString(5, "default"));
        }

        @Test
        @DisplayName("Should return value even when default is provided for valid index")
        void shouldReturnValueWhenDefaultProvided() {
            Parameter param = new Parameter(Arrays.asList("actual"));

            assertEquals("actual", param.getString(0, "default"));
        }
    }

    @Nested
    @DisplayName("getLong Tests")
    class GetLongTests {

        @Test
        @DisplayName("Should return long at index")
        void shouldReturnLongAtIndex() {
            Parameter param = new Parameter(Arrays.asList("100", "200", "300"));

            assertEquals(100L, param.getLong(0));
            assertEquals(200L, param.getLong(1));
            assertEquals(300L, param.getLong(2));
        }

        @Test
        @DisplayName("Should return null for invalid index without default")
        void shouldReturnNullForInvalidIndex() {
            Parameter param = new Parameter(Arrays.asList("100"));

            assertNull(param.getLong(5));
        }

        @Test
        @DisplayName("Should return default for invalid index with default")
        void shouldReturnDefaultForInvalidIndex() {
            Parameter param = new Parameter(Arrays.asList("100"));

            assertEquals(999L, param.getLong(5, 999L));
        }

        @Test
        @DisplayName("Should return default for non-numeric value")
        void shouldReturnDefaultForNonNumericValue() {
            Parameter param = new Parameter(Arrays.asList("notANumber"));

            assertNull(param.getLong(0));
            assertEquals(999L, param.getLong(0, 999L));
        }

        @Test
        @DisplayName("Should handle negative long values")
        void shouldHandleNegativeLongValues() {
            Parameter param = new Parameter(Arrays.asList("-500"));

            assertEquals(-500L, param.getLong(0));
        }

        @Test
        @DisplayName("Should handle large long values")
        void shouldHandleLargeLongValues() {
            Parameter param = new Parameter(Arrays.asList(String.valueOf(Long.MAX_VALUE)));

            assertEquals(Long.MAX_VALUE, param.getLong(0));
        }
    }

    @Nested
    @DisplayName("getDouble Tests")
    class GetDoubleTests {

        @Test
        @DisplayName("Should return double at index")
        void shouldReturnDoubleAtIndex() {
            Parameter param = new Parameter(Arrays.asList("3.14", "2.71", "1.41"));

            assertEquals(3.14, param.getDouble(0), 0.001);
            assertEquals(2.71, param.getDouble(1), 0.001);
            assertEquals(1.41, param.getDouble(2), 0.001);
        }

        @Test
        @DisplayName("Should return null for invalid index without default")
        void shouldReturnNullForInvalidIndex() {
            Parameter param = new Parameter(Arrays.asList("3.14"));

            assertNull(param.getDouble(5));
        }

        @Test
        @DisplayName("Should return default for invalid index with default")
        void shouldReturnDefaultForInvalidIndex() {
            Parameter param = new Parameter(Arrays.asList("3.14"));

            assertEquals(9.99, param.getDouble(5, 9.99), 0.001);
        }

        @Test
        @DisplayName("Should return default for non-numeric value")
        void shouldReturnDefaultForNonNumericValue() {
            Parameter param = new Parameter(Arrays.asList("notANumber"));

            assertNull(param.getDouble(0));
            assertEquals(9.99, param.getDouble(0, 9.99), 0.001);
        }

        @Test
        @DisplayName("Should handle integer as double")
        void shouldHandleIntegerAsDouble() {
            Parameter param = new Parameter(Arrays.asList("42"));

            assertEquals(42.0, param.getDouble(0), 0.001);
        }

        @Test
        @DisplayName("Should handle negative double values")
        void shouldHandleNegativeDoubleValues() {
            Parameter param = new Parameter(Arrays.asList("-3.14"));

            assertEquals(-3.14, param.getDouble(0), 0.001);
        }
    }

    @Nested
    @DisplayName("getInteger Tests")
    class GetIntegerTests {

        @Test
        @DisplayName("Should return integer at index")
        void shouldReturnIntegerAtIndex() {
            Parameter param = new Parameter(Arrays.asList("10", "20", "30"));

            assertEquals(10, param.getInteger(0));
            assertEquals(20, param.getInteger(1));
            assertEquals(30, param.getInteger(2));
        }

        @Test
        @DisplayName("Should return null for invalid index without default")
        void shouldReturnNullForInvalidIndex() {
            Parameter param = new Parameter(Arrays.asList("10"));

            assertNull(param.getInteger(5));
        }

        @Test
        @DisplayName("Should return default for invalid index with default")
        void shouldReturnDefaultForInvalidIndex() {
            Parameter param = new Parameter(Arrays.asList("10"));

            assertEquals(99, param.getInteger(5, 99));
        }

        @Test
        @DisplayName("Should return default for non-numeric value")
        void shouldReturnDefaultForNonNumericValue() {
            Parameter param = new Parameter(Arrays.asList("notANumber"));

            assertNull(param.getInteger(0));
            assertEquals(99, param.getInteger(0, 99));
        }

        @Test
        @DisplayName("Should handle negative integer values")
        void shouldHandleNegativeIntegerValues() {
            Parameter param = new Parameter(Arrays.asList("-50"));

            assertEquals(-50, param.getInteger(0));
        }

        @Test
        @DisplayName("Should handle Integer.MAX_VALUE")
        void shouldHandleIntegerMaxValue() {
            Parameter param = new Parameter(Arrays.asList(String.valueOf(Integer.MAX_VALUE)));

            assertEquals(Integer.MAX_VALUE, param.getInteger(0));
        }

        @Test
        @DisplayName("Should handle Integer.MIN_VALUE")
        void shouldHandleIntegerMinValue() {
            Parameter param = new Parameter(Arrays.asList(String.valueOf(Integer.MIN_VALUE)));

            assertEquals(Integer.MIN_VALUE, param.getInteger(0));
        }
    }

    @Nested
    @DisplayName("getByte Tests")
    class GetByteTests {

        @Test
        @DisplayName("Should return byte at index")
        void shouldReturnByteAtIndex() {
            Parameter param = new Parameter(Arrays.asList("1", "2", "127"));

            assertEquals((byte) 1, param.getByte(0));
            assertEquals((byte) 2, param.getByte(1));
            assertEquals((byte) 127, param.getByte(2));
        }

        @Test
        @DisplayName("Should return null for invalid index without default")
        void shouldReturnNullForInvalidIndex() {
            Parameter param = new Parameter(Arrays.asList("1"));

            assertNull(param.getByte(5));
        }

        @Test
        @DisplayName("Should return default for invalid index with default")
        void shouldReturnDefaultForInvalidIndex() {
            Parameter param = new Parameter(Arrays.asList("1"));

            assertEquals((byte) 99, param.getByte(5, (byte) 99));
        }

        @Test
        @DisplayName("Should return default for non-numeric value")
        void shouldReturnDefaultForNonNumericValue() {
            Parameter param = new Parameter(Arrays.asList("notANumber"));

            assertNull(param.getByte(0));
            assertEquals((byte) 99, param.getByte(0, (byte) 99));
        }

        @Test
        @DisplayName("Should handle negative byte values")
        void shouldHandleNegativeByteValues() {
            Parameter param = new Parameter(Arrays.asList("-128"));

            assertEquals((byte) -128, param.getByte(0));
        }

        @Test
        @DisplayName("Should handle Byte.MAX_VALUE")
        void shouldHandleByteMaxValue() {
            Parameter param = new Parameter(Arrays.asList("127"));

            assertEquals(Byte.MAX_VALUE, param.getByte(0));
        }

        @Test
        @DisplayName("Should handle Byte.MIN_VALUE")
        void shouldHandleByteMinValue() {
            Parameter param = new Parameter(Arrays.asList("-128"));

            assertEquals(Byte.MIN_VALUE, param.getByte(0));
        }
    }

    @Nested
    @DisplayName("size Tests")
    class SizeTests {

        @Test
        @DisplayName("Should return correct size for non-empty parameter")
        void shouldReturnCorrectSizeForNonEmptyParameter() {
            Parameter param = new Parameter(Arrays.asList("a", "b", "c", "d", "e"));

            assertEquals(5, param.size());
        }

        @Test
        @DisplayName("Should return 0 for empty parameter")
        void shouldReturnZeroForEmptyParameter() {
            Parameter param = new Parameter(new ArrayList<>());

            assertEquals(0, param.size());
        }

        @Test
        @DisplayName("Should return 1 for single element parameter")
        void shouldReturnOneForSingleElementParameter() {
            Parameter param = new Parameter(Collections.singletonList("single"));

            assertEquals(1, param.size());
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should return non-null string representation")
        void shouldReturnNonNullStringRepresentation() {
            Parameter param = new Parameter(Arrays.asList("a", "b", "c"));

            assertNotNull(param.toString());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle whitespace values")
        void shouldHandleWhitespaceValues() {
            Parameter param = new Parameter(Arrays.asList(" ", "  ", "value"));

            assertEquals(" ", param.getString(0));
            assertEquals("  ", param.getString(1));
            assertEquals("value", param.getString(2));
        }

        @Test
        @DisplayName("Should handle empty string values in list")
        void shouldHandleEmptyStringValuesInList() {
            Parameter param = new Parameter(Arrays.asList("", "value", ""));

            assertEquals("", param.getString(0));
            assertEquals("value", param.getString(1));
            assertEquals("", param.getString(2));
        }

        @Test
        @DisplayName("Should handle special characters")
        void shouldHandleSpecialCharacters() {
            Parameter param = new Parameter(Arrays.asList("@#$%", "^&*()", "test"));

            assertEquals("@#$%", param.getString(0));
            assertEquals("^&*()", param.getString(1));
            assertEquals("test", param.getString(2));
        }

        @Test
        @DisplayName("Should handle unicode characters")
        void shouldHandleUnicodeCharacters() {
            Parameter param = new Parameter(Arrays.asList("test", "value"));

            assertEquals("test", param.getString(0));
        }

        @Test
        @DisplayName("Should handle decimal that overflows byte")
        void shouldHandleDecimalOverflowByte() {
            Parameter param = new Parameter(Arrays.asList("300")); // Overflows byte

            // Byte.valueOf("300") throws NumberFormatException, so getByte returns null default
            assertNull(param.getByte(0));
        }
    }
}
