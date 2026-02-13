package com.bafmc.customenchantment.player;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerTemporaryStorage")
@ExtendWith(MockitoExtension.class)
class PlayerTemporaryStorageTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerTemporaryStorage storage;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        storage = new PlayerTemporaryStorage(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(storage);
            assertSame(mockCEPlayer, storage.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(storage instanceof CEPlayerExpansion);
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should not throw")
        void onJoinShouldNotThrow() {
            assertDoesNotThrow(() -> storage.onJoin());
        }

        @Test
        @DisplayName("onQuit should not throw")
        void onQuitShouldNotThrow() {
            assertDoesNotThrow(() -> storage.onQuit());
        }
    }

    @Nested
    @DisplayName("set and get Tests")
    class SetAndGetTests {

        @Test
        @DisplayName("Should store and retrieve a string value")
        void shouldStoreAndRetrieveStringValue() {
            storage.set("key", "value");
            assertEquals("value", storage.get("key"));
        }

        @Test
        @DisplayName("Should store and retrieve an integer value")
        void shouldStoreAndRetrieveIntValue() {
            storage.set("number", 42);
            assertEquals(42, storage.get("number"));
        }

        @Test
        @DisplayName("Should overwrite existing value")
        void shouldOverwriteExistingValue() {
            storage.set("key", "first");
            storage.set("key", "second");
            assertEquals("second", storage.get("key"));
        }

        @Test
        @DisplayName("Should remove key when value is null")
        void shouldRemoveKeyWhenValueIsNull() {
            storage.set("key", "value");
            storage.set("key", null);
            assertNull(storage.get("key"));
            assertFalse(storage.isSet("key"));
        }

        @Test
        @DisplayName("Should return null for non-existing key")
        void shouldReturnNullForNonExistingKey() {
            assertNull(storage.get("nonexistent"));
        }

        @Test
        @DisplayName("get with default value should return default when key not found")
        void getWithDefaultShouldReturnDefaultWhenKeyNotFound() {
            assertEquals("default", storage.get("missing", "default"));
        }

        @Test
        @DisplayName("get with default value should return stored value when key exists")
        void getWithDefaultShouldReturnStoredValueWhenKeyExists() {
            storage.set("key", "actual");
            assertEquals("actual", storage.get("key", "default"));
        }
    }

    @Nested
    @DisplayName("unset Tests")
    class UnsetTests {

        @Test
        @DisplayName("Should remove existing key")
        void shouldRemoveExistingKey() {
            storage.set("key", "value");
            storage.unset("key");
            assertNull(storage.get("key"));
        }

        @Test
        @DisplayName("Should not throw when removing non-existing key")
        void shouldNotThrowWhenRemovingNonExistingKey() {
            assertDoesNotThrow(() -> storage.unset("nonexistent"));
        }
    }

    @Nested
    @DisplayName("isSet Tests")
    class IsSetTests {

        @Test
        @DisplayName("Should return true for existing key")
        void shouldReturnTrueForExistingKey() {
            storage.set("key", "value");
            assertTrue(storage.isSet("key"));
        }

        @Test
        @DisplayName("Should return false for non-existing key")
        void shouldReturnFalseForNonExistingKey() {
            assertFalse(storage.isSet("key"));
        }
    }

    @Nested
    @DisplayName("getString Tests")
    class GetStringTests {

        @Test
        @DisplayName("Should return string value")
        void shouldReturnStringValue() {
            storage.set("key", "hello");
            assertEquals("hello", storage.getString("key"));
        }

        @Test
        @DisplayName("Should return null for missing key without default")
        void shouldReturnNullForMissingKey() {
            assertNull(storage.getString("missing"));
        }

        @Test
        @DisplayName("Should return default value for missing key")
        void shouldReturnDefaultForMissingKey() {
            assertEquals("default", storage.getString("missing", "default"));
        }

        @Test
        @DisplayName("Should convert non-string values to string")
        void shouldConvertNonStringToString() {
            storage.set("number", 42);
            assertEquals("42", storage.getString("number"));
        }
    }

    @Nested
    @DisplayName("getInt Tests")
    class GetIntTests {

        @Test
        @DisplayName("Should return integer value")
        void shouldReturnIntValue() {
            storage.set("key", "42");
            assertEquals(42, storage.getInt("key"));
        }

        @Test
        @DisplayName("Should return 0 for missing key without default")
        void shouldReturnZeroForMissingKey() {
            assertEquals(0, storage.getInt("missing"));
        }

        @Test
        @DisplayName("Should return default value for missing key")
        void shouldReturnDefaultForMissingKey() {
            assertEquals(99, storage.getInt("missing", 99));
        }

        @Test
        @DisplayName("Should return default for non-parseable value")
        void shouldReturnDefaultForNonParseableValue() {
            storage.set("key", "not_a_number");
            assertEquals(0, storage.getInt("key"));
        }

        @ParameterizedTest
        @CsvSource({"0, 0", "1, 1", "-1, -1", "2147483647, 2147483647"})
        @DisplayName("Should handle various integer values")
        void shouldHandleVariousIntValues(String input, int expected) {
            storage.set("key", input);
            assertEquals(expected, storage.getInt("key"));
        }
    }

    @Nested
    @DisplayName("getDouble Tests")
    class GetDoubleTests {

        @Test
        @DisplayName("Should return double value")
        void shouldReturnDoubleValue() {
            storage.set("key", "3.14");
            assertEquals(3.14, storage.getDouble("key"), 0.001);
        }

        @Test
        @DisplayName("Should return 0.0 for missing key without default")
        void shouldReturnZeroForMissingKey() {
            assertEquals(0.0, storage.getDouble("missing"), 0.001);
        }

        @Test
        @DisplayName("Should return default for missing key")
        void shouldReturnDefaultForMissingKey() {
            assertEquals(1.5, storage.getDouble("missing", 1.5), 0.001);
        }

        @Test
        @DisplayName("Should return default for non-parseable value")
        void shouldReturnDefaultForNonParseableValue() {
            storage.set("key", "abc");
            assertEquals(0.0, storage.getDouble("key"), 0.001);
        }
    }

    @Nested
    @DisplayName("getLong Tests")
    class GetLongTests {

        @Test
        @DisplayName("Should return long value")
        void shouldReturnLongValue() {
            storage.set("key", "9999999999");
            assertEquals(9999999999L, storage.getLong("key"));
        }

        @Test
        @DisplayName("Should return 0 for missing key")
        void shouldReturnZeroForMissingKey() {
            assertEquals(0, storage.getLong("missing"));
        }

        @Test
        @DisplayName("Should return default for non-parseable value")
        void shouldReturnDefaultForNonParseable() {
            storage.set("key", "not_a_long");
            assertEquals(0, storage.getLong("key"));
        }

        @Test
        @DisplayName("Should return default value for missing key")
        void shouldReturnDefaultForMissingKey() {
            assertEquals(100L, storage.getLong("missing", 100L));
        }
    }

    @Nested
    @DisplayName("getByte Tests")
    class GetByteTests {

        @Test
        @DisplayName("Should return byte value")
        void shouldReturnByteValue() {
            storage.set("key", "127");
            assertEquals((byte) 127, storage.getByte("key"));
        }

        @Test
        @DisplayName("Should return 0 for missing key")
        void shouldReturnZeroForMissingKey() {
            assertEquals((byte) 0, storage.getByte("missing"));
        }

        @Test
        @DisplayName("Should return default for missing key")
        void shouldReturnDefaultForMissingKey() {
            assertEquals((byte) 5, storage.getByte("missing", (byte) 5));
        }
    }

    @Nested
    @DisplayName("getFloat Tests")
    class GetFloatTests {

        @Test
        @DisplayName("Should return float value")
        void shouldReturnFloatValue() {
            storage.set("key", "1.5");
            assertEquals(1.5f, storage.getFloat("key"), 0.001f);
        }

        @Test
        @DisplayName("Should return 0 for missing key")
        void shouldReturnZeroForMissingKey() {
            assertEquals(0f, storage.getFloat("missing"), 0.001f);
        }

        @Test
        @DisplayName("Should return default for missing key")
        void shouldReturnDefaultForMissingKey() {
            assertEquals(2.5f, storage.getFloat("missing", 2.5f), 0.001f);
        }
    }

    @Nested
    @DisplayName("getBoolean Tests")
    class GetBooleanTests {

        @Test
        @DisplayName("Should return true for 'true' string")
        void shouldReturnTrueForTrueString() {
            storage.set("key", "true");
            assertTrue(storage.getBoolean("key"));
        }

        @Test
        @DisplayName("Should return false for 'false' string")
        void shouldReturnFalseForFalseString() {
            storage.set("key", "false");
            assertFalse(storage.getBoolean("key"));
        }

        @Test
        @DisplayName("Should return false for missing key")
        void shouldReturnFalseForMissingKey() {
            assertFalse(storage.getBoolean("missing"));
        }

        @Test
        @DisplayName("Should return default for missing key")
        void shouldReturnDefaultForMissingKey() {
            assertTrue(storage.getBoolean("missing", true));
        }
    }

    @Nested
    @DisplayName("isBoolean Tests")
    class IsBooleanTests {

        @Test
        @DisplayName("Should return true for 'true' string value")
        void shouldReturnTrueForTrueString() {
            storage.set("key", "true");
            assertTrue(storage.isBoolean("key"));
        }

        @Test
        @DisplayName("Should return false for missing key")
        void shouldReturnFalseForMissingKey() {
            assertFalse(storage.isBoolean("missing"));
        }

        @Test
        @DisplayName("Should return default value when key is missing")
        void shouldReturnDefaultWhenKeyMissing() {
            assertTrue(storage.isBoolean("missing", true));
        }
    }

    @Nested
    @DisplayName("getKeys Tests")
    class GetKeysTests {

        @Test
        @DisplayName("Should return empty list when no keys set")
        void shouldReturnEmptyListWhenNoKeys() {
            assertTrue(storage.getKeys().isEmpty());
        }

        @Test
        @DisplayName("Should return all set keys")
        void shouldReturnAllKeys() {
            storage.set("a", 1);
            storage.set("b", 2);
            storage.set("c", 3);

            List<String> keys = storage.getKeys();
            assertEquals(3, keys.size());
            assertTrue(keys.contains("a"));
            assertTrue(keys.contains("b"));
            assertTrue(keys.contains("c"));
        }
    }

    @Nested
    @DisplayName("removeStartsWith Tests")
    class RemoveStartsWithTests {

        @Test
        @DisplayName("Should remove keys starting with prefix")
        void shouldRemoveKeysStartingWithPrefix() {
            storage.set("set_armor_1", 1);
            storage.set("set_armor_2", 2);
            storage.set("other_key", 3);

            storage.removeStartsWith("set_");

            assertFalse(storage.isSet("set_armor_1"));
            assertFalse(storage.isSet("set_armor_2"));
            assertTrue(storage.isSet("other_key"));
        }

        @Test
        @DisplayName("Should handle empty storage")
        void shouldHandleEmptyStorage() {
            assertDoesNotThrow(() -> storage.removeStartsWith("prefix"));
        }

        @Test
        @DisplayName("Should handle no matching keys")
        void shouldHandleNoMatchingKeys() {
            storage.set("key1", "val1");
            storage.set("key2", "val2");

            storage.removeStartsWith("nonexistent");

            assertTrue(storage.isSet("key1"));
            assertTrue(storage.isSet("key2"));
        }
    }

    @Nested
    @DisplayName("setAll Tests")
    class SetAllTests {

        @Test
        @DisplayName("Should set all entries from map")
        void shouldSetAllEntriesFromMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("k1", "v1");
            map.put("k2", 42);
            map.put("k3", true);

            storage.setAll(map);

            assertEquals("v1", storage.get("k1"));
            assertEquals(42, storage.get("k2"));
            assertEquals(true, storage.get("k3"));
        }

        @Test
        @DisplayName("Should handle empty map")
        void shouldHandleEmptyMap() {
            assertDoesNotThrow(() -> storage.setAll(new HashMap<>()));
        }

        @Test
        @DisplayName("Should overwrite existing values")
        void shouldOverwriteExistingValues() {
            storage.set("key", "old");

            Map<String, Object> map = new HashMap<>();
            map.put("key", "new");
            storage.setAll(map);

            assertEquals("new", storage.get("key"));
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should use ConcurrentHashMap internally")
        void shouldUseConcurrentHashMap() throws Exception {
            var field = PlayerTemporaryStorage.class.getDeclaredField("map");
            field.setAccessible(true);
            assertTrue(field.get(storage) instanceof java.util.concurrent.ConcurrentHashMap);
        }
    }
}
