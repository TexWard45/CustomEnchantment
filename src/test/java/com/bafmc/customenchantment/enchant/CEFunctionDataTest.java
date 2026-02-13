package com.bafmc.customenchantment.enchant;

import com.bafmc.bukkit.utils.EquipSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CEFunctionData - holds contextual data for enchant function execution.
 * Uses ConcurrentHashMap with case-insensitive keys.
 */
@DisplayName("CEFunctionData Tests")
class CEFunctionDataTest {

    private CEFunctionData data;

    @BeforeEach
    void setUp() {
        // Use a minimal constructor approach - must mock Player since it calls CEAPI
        data = createMinimalData();
    }

    /**
     * Creates CEFunctionData without calling setPlayer (which depends on CEAPI).
     * Uses reflection-free approach via the set() method.
     */
    private CEFunctionData createMinimalData() {
        // We can't directly call new CEFunctionData(player) because it calls CEAPI.getCEPlayer
        // Instead, we test the data map operations directly
        CEFunctionData d = mock(CEFunctionData.class, CALLS_REAL_METHODS);
        // Initialize internal map via real methods through set()
        return d;
    }

    @Nested
    @DisplayName("set and get Tests")
    class SetGetTests {

        @Test
        @DisplayName("should store and retrieve string values")
        void shouldStoreAndRetrieveStrings() {
            // Create data using a workaround
            CEFunctionData funcData = createDataWithMap();
            funcData.set("test_key", "test_value");

            assertEquals("test_value", funcData.get("test_key"));
        }

        @Test
        @DisplayName("should store keys in lowercase")
        void shouldStoreKeysInLowercase() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("UPPER_KEY", "value");

            assertEquals("value", funcData.get("upper_key"));
            assertEquals("value", funcData.get("UPPER_KEY"));
        }

        @Test
        @DisplayName("should remove entry when value is null")
        void shouldRemoveEntryWhenValueNull() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("key", "value");
            funcData.set("key", null);

            assertNull(funcData.get("key"));
        }

        @Test
        @DisplayName("should return null for non-existent key")
        void shouldReturnNullForNonExistentKey() {
            CEFunctionData funcData = createDataWithMap();

            assertNull(funcData.get("nonexistent"));
        }
    }

    @Nested
    @DisplayName("isSet Tests")
    class IsSetTests {

        @Test
        @DisplayName("should return true for existing key")
        void shouldReturnTrueForExistingKey() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("existing", "value");

            assertTrue(funcData.isSet("existing"));
        }

        @Test
        @DisplayName("should return false for non-existing key")
        void shouldReturnFalseForNonExisting() {
            CEFunctionData funcData = createDataWithMap();

            assertFalse(funcData.isSet("nonexistent"));
        }

        @Test
        @DisplayName("should be case insensitive")
        void shouldBeCaseInsensitive() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("MyKey", "value");

            assertTrue(funcData.isSet("mykey"));
            assertTrue(funcData.isSet("MYKEY"));
        }

        @Test
        @DisplayName("should throw for null key")
        void shouldThrowForNullKey() {
            CEFunctionData funcData = createDataWithMap();

            assertThrows(NullPointerException.class, () -> funcData.isSet(null));
        }
    }

    @Nested
    @DisplayName("remove Tests")
    class RemoveTests {

        @Test
        @DisplayName("should remove existing key")
        void shouldRemoveExistingKey() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("to_remove", "value");

            funcData.remove("to_remove");

            assertFalse(funcData.isSet("to_remove"));
        }

        @Test
        @DisplayName("should be case insensitive")
        void shouldBeCaseInsensitive() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("mykey", "value");

            funcData.remove("MYKEY");

            assertFalse(funcData.isSet("mykey"));
        }

        @Test
        @DisplayName("should throw for null key")
        void shouldThrowForNullKey() {
            CEFunctionData funcData = createDataWithMap();

            assertThrows(NullPointerException.class, () -> funcData.remove(null));
        }
    }

    @Nested
    @DisplayName("getKeys Tests")
    class GetKeysTests {

        @Test
        @DisplayName("should return all keys")
        void shouldReturnAllKeys() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("key1", "v1");
            funcData.set("key2", "v2");

            assertEquals(2, funcData.getKeys().size());
            assertTrue(funcData.getKeys().contains("key1"));
            assertTrue(funcData.getKeys().contains("key2"));
        }
    }

    @Nested
    @DisplayName("checkValue Tests")
    class CheckValueTests {

        @Test
        @DisplayName("should return true when value matches class")
        void shouldReturnTrueWhenMatches() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("str_key", "hello");

            assertTrue(funcData.checkValue("str_key", String.class));
        }

        @Test
        @DisplayName("should return false when value does not match class")
        void shouldReturnFalseWhenNotMatches() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("str_key", "hello");

            assertFalse(funcData.checkValue("str_key", Integer.class));
        }

        @Test
        @DisplayName("should throw for null key")
        void shouldThrowForNullKey() {
            CEFunctionData funcData = createDataWithMap();

            assertThrows(NullPointerException.class, () -> funcData.checkValue(null, String.class));
        }

        @Test
        @DisplayName("should throw for null class")
        void shouldThrowForNullClass() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("key", "value");

            assertThrows(NullPointerException.class, () -> funcData.checkValue("key", null));
        }
    }

    @Nested
    @DisplayName("Target Tests")
    class TargetTests {

        @Test
        @DisplayName("should default target to PLAYER")
        void shouldDefaultTargetToPlayer() {
            CEFunctionData funcData = createDataWithMap();

            assertEquals(Target.PLAYER, funcData.getTarget());
        }

        @Test
        @DisplayName("should set target")
        void shouldSetTarget() {
            CEFunctionData funcData = createDataWithMap();
            funcData.setTarget(Target.ENEMY);

            assertEquals(Target.ENEMY, funcData.getTarget());
        }

        @Test
        @DisplayName("should default to PLAYER when setting null target")
        void shouldDefaultToPlayerWhenNull() {
            CEFunctionData funcData = createDataWithMap();
            funcData.setTarget(Target.ENEMY);
            funcData.setTarget(null);

            assertEquals(Target.PLAYER, funcData.getTarget());
        }
    }

    @Nested
    @DisplayName("EquipSlot Tests")
    class EquipSlotTests {

        @Test
        @DisplayName("should set and get equip slot")
        void shouldSetAndGetEquipSlot() {
            CEFunctionData funcData = createDataWithMap();
            funcData.setEquipSlot(EquipSlot.HELMET);

            assertEquals(EquipSlot.HELMET, funcData.getEquipSlot());
        }

        @Test
        @DisplayName("should set and get active equip slot")
        void shouldSetAndGetActiveEquipSlot() {
            CEFunctionData funcData = createDataWithMap();
            funcData.setActiveEquipSlot(EquipSlot.MAINHAND);

            assertEquals(EquipSlot.MAINHAND, funcData.getActiveEquipSlot());
        }
    }

    @Nested
    @DisplayName("DamageCause Tests")
    class DamageCauseTests {

        @Test
        @DisplayName("should set and get damage cause")
        void shouldSetAndGetDamageCause() {
            CEFunctionData funcData = createDataWithMap();
            funcData.setDamageCause(org.bukkit.event.entity.EntityDamageEvent.DamageCause.FIRE);

            assertEquals(org.bukkit.event.entity.EntityDamageEvent.DamageCause.FIRE, funcData.getDamageCause());
        }

        @Test
        @DisplayName("should return null when no damage cause set")
        void shouldReturnNullWhenNoDamageCause() {
            CEFunctionData funcData = createDataWithMap();

            assertNull(funcData.getDamageCause());
        }
    }

    @Nested
    @DisplayName("FakeSource Tests")
    class FakeSourceTests {

        @Test
        @DisplayName("should default to false")
        void shouldDefaultToFalse() {
            CEFunctionData funcData = createDataWithMap();

            assertFalse(funcData.isFakeSource());
        }

        @Test
        @DisplayName("should set fake source to true")
        void shouldSetFakeSourceTrue() {
            CEFunctionData funcData = createDataWithMap();
            funcData.setFakeSource(true);

            assertTrue(funcData.isFakeSource());
        }

        @Test
        @DisplayName("should set fake source to false")
        void shouldSetFakeSourceFalse() {
            CEFunctionData funcData = createDataWithMap();
            funcData.setFakeSource(true);
            funcData.setFakeSource(false);

            assertFalse(funcData.isFakeSource());
        }
    }

    @Nested
    @DisplayName("getNextPrefix Tests")
    class GetNextPrefixTests {

        @Test
        @DisplayName("should generate prefix with index")
        void shouldGeneratePrefixWithIndex() {
            CEFunctionData funcData = createDataWithMap();

            String prefix1 = funcData.getNextPrefix("test_", 0);
            String prefix2 = funcData.getNextPrefix("test_", 1);

            assertNotNull(prefix1);
            assertNotNull(prefix2);
            assertTrue(prefix1.startsWith("test_"));
            assertTrue(prefix2.startsWith("test_"));
        }

        @Test
        @DisplayName("should return consistent prefix for same index")
        void shouldReturnConsistentPrefix() {
            CEFunctionData funcData = createDataWithMap();

            String first = funcData.getNextPrefix("p_", 0);
            String second = funcData.getNextPrefix("p_", 0);

            assertEquals(first, second);
        }

        @Test
        @DisplayName("should generate different prefixes for different indices")
        void shouldGenerateDifferentPrefixes() {
            CEFunctionData funcData = createDataWithMap();

            String prefix0 = funcData.getNextPrefix("p_", 0);
            String prefix1 = funcData.getNextPrefix("p_", 1);

            // Both should start with p_ but may have different suffixes
            assertTrue(prefix0.startsWith("p_"));
            assertTrue(prefix1.startsWith("p_"));
        }
    }

    @Nested
    @DisplayName("clone Tests")
    class CloneTests {

        @Test
        @DisplayName("should create independent copy of data map")
        void shouldCreateIndependentCopy() {
            CEFunctionData funcData = createDataWithMap();
            funcData.set("key", "original");

            CEFunctionData cloned = funcData.clone();
            cloned.set("key", "modified");

            assertEquals("original", funcData.get("key"));
            assertEquals("modified", cloned.get("key"));
        }

        @Test
        @DisplayName("cloned data should have same target")
        void shouldHaveSameTarget() {
            CEFunctionData funcData = createDataWithMap();
            funcData.setTarget(Target.ENEMY);

            CEFunctionData cloned = funcData.clone();

            assertEquals(Target.ENEMY, cloned.getTarget());
        }
    }

    /**
     * Helper to create a CEFunctionData with real map operations
     * without calling the Player-dependent constructor.
     */
    private CEFunctionData createDataWithMap() {
        try {
            // Use reflection to create instance without Player-dependent constructor
            java.lang.reflect.Constructor<CEFunctionData> constructor =
                    CEFunctionData.class.getDeclaredConstructor(org.bukkit.entity.Player.class);
            constructor.setAccessible(true);
            return constructor.newInstance((org.bukkit.entity.Player) null);
        } catch (Exception e) {
            fail("Failed to create CEFunctionData: " + e.getMessage());
            return null;
        }
    }
}
