package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CEType - defines enchant trigger types (AUTO, ATTACK, DEFENSE, etc.).
 * Uses static registry pattern with ConcurrentHashMap.
 */
@DisplayName("CEType Tests")
class CETypeTest {

    @Nested
    @DisplayName("Predefined Constants Tests")
    class PredefinedConstantsTests {

        @Test
        @DisplayName("AUTO should have type AUTO")
        void autoShouldHaveCorrectType() {
            assertEquals("AUTO", CEType.AUTO.getType());
        }

        @Test
        @DisplayName("ATTACK should have type ATTACK")
        void attackShouldHaveCorrectType() {
            assertEquals("ATTACK", CEType.ATTACK.getType());
        }

        @Test
        @DisplayName("FINAL_ATTACK should have type FINAL_ATTACK")
        void finalAttackShouldHaveCorrectType() {
            assertEquals("FINAL_ATTACK", CEType.FINAL_ATTACK.getType());
        }

        @Test
        @DisplayName("DEFENSE should have type DEFENSE")
        void defenseShouldHaveCorrectType() {
            assertEquals("DEFENSE", CEType.DEFENSE.getType());
        }

        @Test
        @DisplayName("UNKNOWN_DEFENSE should have type UNKNOWN_DEFENSE")
        void unknownDefenseShouldHaveCorrectType() {
            assertEquals("UNKNOWN_DEFENSE", CEType.UNKNOWN_DEFENSE.getType());
        }

        @Test
        @DisplayName("HURT should have type HURT")
        void hurtShouldHaveCorrectType() {
            assertEquals("HURT", CEType.HURT.getType());
        }

        @Test
        @DisplayName("SNEAK should have type SNEAK")
        void sneakShouldHaveCorrectType() {
            assertEquals("SNEAK", CEType.SNEAK.getType());
        }

        @Test
        @DisplayName("MINING should have type MINING")
        void miningShouldHaveCorrectType() {
            assertEquals("MINING", CEType.MINING.getType());
        }

        @Test
        @DisplayName("MOVE should have type MOVE")
        void moveShouldHaveCorrectType() {
            assertEquals("MOVE", CEType.MOVE.getType());
        }

        @Test
        @DisplayName("ITEM_CONSUME should have type ITEM_CONSUME")
        void itemConsumeShouldHaveCorrectType() {
            assertEquals("ITEM_CONSUME", CEType.ITEM_CONSUME.getType());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should convert type to uppercase")
        void shouldConvertToUppercase() {
            CEType type = new CEType("lowercase");

            assertEquals("LOWERCASE", type.getType());
        }

        @Test
        @DisplayName("should accept mixed case")
        void shouldAcceptMixedCase() {
            CEType type = new CEType("MiXeD");

            assertEquals("MIXED", type.getType());
        }
    }

    @Nested
    @DisplayName("register Tests")
    class RegisterTests {

        @Test
        @DisplayName("should throw when registering duplicate type")
        void shouldThrowForDuplicateRegistration() {
            // AUTO is already registered statically
            CEType duplicate = new CEType("AUTO");

            assertThrows(IllegalArgumentException.class, () -> duplicate.register());
        }

        @Test
        @DisplayName("should register new unique type")
        void shouldRegisterNewType() {
            String uniqueType = "TEST_UNIQUE_" + System.nanoTime();
            CEType newType = new CEType(uniqueType);

            assertDoesNotThrow(() -> newType.register());
            assertSame(newType, CEType.valueOf(uniqueType));
        }
    }

    @Nested
    @DisplayName("valueOf Tests")
    class ValueOfTests {

        @ParameterizedTest(name = "should resolve {0}")
        @ValueSource(strings = {"AUTO", "ATTACK", "DEFENSE", "MINING", "MOVE", "HOLD"})
        @DisplayName("should resolve predefined types by name")
        void shouldResolvePredefinedTypes(String name) {
            CEType type = CEType.valueOf(name);

            assertNotNull(type);
            assertEquals(name, type.getType());
        }

        @Test
        @DisplayName("should return null for unregistered type")
        void shouldReturnNullForUnregistered() {
            assertNull(CEType.valueOf("NONEXISTENT_TYPE_XYZ"));
        }
    }

    @Nested
    @DisplayName("values Tests")
    class ValuesTests {

        @Test
        @DisplayName("should return array of all registered types")
        void shouldReturnAllTypes() {
            CEType[] all = CEType.values();

            assertNotNull(all);
            assertTrue(all.length >= 25); // At least the predefined constants
        }

        @Test
        @DisplayName("should contain AUTO")
        void shouldContainAuto() {
            CEType[] all = CEType.values();
            boolean found = false;
            for (CEType type : all) {
                if ("AUTO".equals(type.getType())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Nested
    @DisplayName("equals Tests")
    class EqualsTests {

        @Test
        @DisplayName("should be equal when same type string")
        void shouldBeEqualSameType() {
            // Two references to the same type
            assertTrue(CEType.AUTO.equals(CEType.valueOf("AUTO")));
        }

        @Test
        @DisplayName("should not be equal with different type")
        void shouldNotBeEqualDifferentType() {
            assertFalse(CEType.AUTO.equals(CEType.ATTACK));
        }

        @Test
        @DisplayName("should return false for null")
        void shouldReturnFalseForNull() {
            assertFalse(CEType.AUTO.equals(null));
        }

        @Test
        @DisplayName("should return false for non-CEType object")
        void shouldReturnFalseForNonCEType() {
            assertFalse(CEType.AUTO.equals("AUTO"));
        }

        @Test
        @DisplayName("should be equal with new instance of same type string")
        void shouldBeEqualNewInstanceSameType() {
            CEType newType = new CEType("AUTO");

            assertTrue(CEType.AUTO.equals(newType));
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("should return the type string")
        void shouldReturnTypeString() {
            assertEquals("AUTO", CEType.AUTO.toString());
            assertEquals("ATTACK", CEType.ATTACK.toString());
        }
    }
}
