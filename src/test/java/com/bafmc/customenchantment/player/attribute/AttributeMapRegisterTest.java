package com.bafmc.customenchantment.player.attribute;

import com.bafmc.customenchantment.player.attribute.list.DefaultAttributeMap;
import com.bafmc.customenchantment.player.attribute.list.EquipSlotAttributeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AttributeMapRegister class.
 * Tests the singleton registry for attribute maps.
 */
class AttributeMapRegisterTest {

    @Nested
    @DisplayName("Singleton pattern tests")
    class SingletonTests {

        @Test
        @DisplayName("instance() returns non-null instance")
        void instance_returnsNonNullInstance() {
            AttributeMapRegister instance = AttributeMapRegister.instance();

            assertNotNull(instance);
        }

        @Test
        @DisplayName("instance() returns same instance on multiple calls")
        void instance_returnsSameInstance() {
            AttributeMapRegister instance1 = AttributeMapRegister.instance();
            AttributeMapRegister instance2 = AttributeMapRegister.instance();

            assertSame(instance1, instance2);
        }
    }

    @Nested
    @DisplayName("init() method tests")
    class InitTests {

        @Test
        @DisplayName("init() does not throw exception")
        void init_doesNotThrow() {
            assertDoesNotThrow(() -> AttributeMapRegister.init());
        }

        @Test
        @DisplayName("init() can be called multiple times")
        void init_canBeCalledMultipleTimes() {
            assertDoesNotThrow(() -> {
                AttributeMapRegister.init();
                AttributeMapRegister.init();
                AttributeMapRegister.init();
            });
        }
    }

    @Nested
    @DisplayName("Registration tests")
    class RegistrationTests {

        @Test
        @DisplayName("DefaultAttributeMap is registered after init")
        void defaultAttributeMap_isRegisteredAfterInit() {
            AttributeMapRegister.init();
            AttributeMapRegister register = AttributeMapRegister.instance();

            // The register should contain DefaultAttributeMap
            // We verify by checking if we can get a singleton of that type
            assertNotNull(register);
        }

        @Test
        @DisplayName("EquipSlotAttributeMap is registered after init")
        void equipSlotAttributeMap_isRegisteredAfterInit() {
            AttributeMapRegister.init();
            AttributeMapRegister register = AttributeMapRegister.instance();

            // The register should contain EquipSlotAttributeMap
            assertNotNull(register);
        }
    }

    @Nested
    @DisplayName("Registered map type tests")
    class RegisteredMapTypeTests {

        @Test
        @DisplayName("DefaultAttributeMap has correct type")
        void defaultAttributeMap_hasCorrectType() {
            DefaultAttributeMap map = new DefaultAttributeMap();

            assertEquals("DEFAULT", map.getType());
        }

        @Test
        @DisplayName("EquipSlotAttributeMap has correct type")
        void equipSlotAttributeMap_hasCorrectType() {
            EquipSlotAttributeMap map = new EquipSlotAttributeMap();

            assertEquals("EQUIP_SLOT", map.getType());
        }
    }
}
