package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.attribute.RangeAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CECallerResult - stores execution results with key-value map and option data.
 */
@DisplayName("CECallerResult Tests")
class CECallerResultTest {

    private CECallerResult result;

    @BeforeEach
    void setUp() {
        result = CECallerResult.instance();
    }

    @Nested
    @DisplayName("instance Tests")
    class InstanceTests {

        @Test
        @DisplayName("should create new instance each call")
        void shouldCreateNewInstanceEachCall() {
            CECallerResult result1 = CECallerResult.instance();
            CECallerResult result2 = CECallerResult.instance();

            assertNotSame(result1, result2);
        }

        @Test
        @DisplayName("should create with empty option data list")
        void shouldCreateWithEmptyOptionDataList() {
            assertTrue(result.getOptionDataList().isEmpty());
        }
    }

    @Nested
    @DisplayName("put and get Tests")
    class PutGetTests {

        @Test
        @DisplayName("should store and retrieve string value")
        void shouldStoreAndRetrieveString() {
            result.put("name", "test_value");

            assertEquals("test_value", result.get("name"));
        }

        @Test
        @DisplayName("should store and retrieve integer value")
        void shouldStoreAndRetrieveInteger() {
            result.put("count", 42);

            assertEquals(42, result.get("count"));
        }

        @Test
        @DisplayName("should store and retrieve boolean value")
        void shouldStoreAndRetrieveBoolean() {
            result.put("flag", true);

            assertEquals(true, result.get("flag"));
        }

        @Test
        @DisplayName("should return null for non-existent key")
        void shouldReturnNullForNonExistent() {
            assertNull(result.get("nonexistent"));
        }

        @Test
        @DisplayName("should overwrite existing value")
        void shouldOverwriteExistingValue() {
            result.put("key", "old");
            result.put("key", "new");

            assertEquals("new", result.get("key"));
        }

        @Test
        @DisplayName("should return this for chaining")
        void shouldReturnThisForChaining() {
            CECallerResult chained = result.put("a", 1).put("b", 2).put("c", 3);

            assertSame(result, chained);
            assertEquals(1, result.get("a"));
            assertEquals(2, result.get("b"));
            assertEquals(3, result.get("c"));
        }
    }

    @Nested
    @DisplayName("isSet Tests")
    class IsSetTests {

        @Test
        @DisplayName("should return true for existing key")
        void shouldReturnTrueForExistingKey() {
            result.put("exists", "value");

            assertTrue(result.isSet("exists"));
        }

        @Test
        @DisplayName("should return false for non-existing key")
        void shouldReturnFalseForNonExisting() {
            assertFalse(result.isSet("nonexistent"));
        }
    }

    @Nested
    @DisplayName("getOptionDataList Tests")
    class GetOptionDataListTests {

        @Test
        @DisplayName("should return empty list initially")
        void shouldReturnEmptyListInitially() {
            assertTrue(result.getOptionDataList().isEmpty());
        }

        @Test
        @DisplayName("should allow adding to option data list")
        void shouldAllowAdding() {
            RangeAttribute attr = mock(RangeAttribute.class);
            result.getOptionDataList().add(attr);

            assertEquals(1, result.getOptionDataList().size());
        }
    }

    @Nested
    @DisplayName("setOptionDataList Tests")
    class SetOptionDataListTests {

        @Test
        @DisplayName("should replace option data list")
        void shouldReplaceOptionDataList() {
            List<RangeAttribute> newList = new ArrayList<>();
            newList.add(mock(RangeAttribute.class));
            newList.add(mock(RangeAttribute.class));

            result.setOptionDataList(newList);

            assertEquals(2, result.getOptionDataList().size());
        }

        @Test
        @DisplayName("should return this for chaining")
        void shouldReturnThisForChaining() {
            CECallerResult chained = result.setOptionDataList(new ArrayList<>());

            assertSame(result, chained);
        }
    }
}
