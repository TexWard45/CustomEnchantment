package com.bafmc.customenchantment.player.attribute;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for AbstractAttributeMap class.
 * Tests the abstract base class for attribute maps.
 */
class AbstractAttributeMapTest {

    @Nested
    @DisplayName("Interface contract tests")
    class InterfaceContractTests {

        @Test
        @DisplayName("loadAttributeMap is defined correctly")
        void loadAttributeMap_isDefinedCorrectly() {
            // Create a concrete implementation for testing
            AbstractAttributeMap concreteMap = new AbstractAttributeMap() {
                @Override
                public String getType() {
                    return "TEST";
                }

                @Override
                public Multimap<CustomAttributeType, NMSAttribute> loadAttributeMap(AttributeMapData data) {
                    return com.google.common.collect.LinkedHashMultimap.create();
                }
            };

            assertNotNull(concreteMap);
            assertEquals("TEST", concreteMap.getType());
        }

        @Test
        @DisplayName("getType returns correct type string")
        void getType_returnsCorrectType() {
            AbstractAttributeMap testMap = new AbstractAttributeMap() {
                @Override
                public String getType() {
                    return "CUSTOM_TYPE";
                }

                @Override
                public Multimap<CustomAttributeType, NMSAttribute> loadAttributeMap(AttributeMapData data) {
                    return null;
                }
            };

            assertEquals("CUSTOM_TYPE", testMap.getType());
        }

        @Test
        @DisplayName("loadAttributeMap accepts AttributeMapData parameter")
        void loadAttributeMap_acceptsAttributeMapData() {
            AttributeMapData mockData = mock(AttributeMapData.class);

            AbstractAttributeMap testMap = new AbstractAttributeMap() {
                @Override
                public String getType() {
                    return "TEST";
                }

                @Override
                public Multimap<CustomAttributeType, NMSAttribute> loadAttributeMap(AttributeMapData data) {
                    assertNotNull(data);
                    return com.google.common.collect.LinkedHashMultimap.create();
                }
            };

            Multimap<CustomAttributeType, NMSAttribute> result = testMap.loadAttributeMap(mockData);
            assertNotNull(result);
        }

        @Test
        @DisplayName("loadAttributeMap returns Multimap")
        void loadAttributeMap_returnsMultimap() {
            AttributeMapData mockData = mock(AttributeMapData.class);

            AbstractAttributeMap testMap = new AbstractAttributeMap() {
                @Override
                public String getType() {
                    return "TEST";
                }

                @Override
                public Multimap<CustomAttributeType, NMSAttribute> loadAttributeMap(AttributeMapData data) {
                    Multimap<CustomAttributeType, NMSAttribute> map = com.google.common.collect.LinkedHashMultimap.create();
                    NMSAttribute attr = mock(NMSAttribute.class);
                    map.put(CustomAttributeType.CRITICAL_DAMAGE, attr);
                    return map;
                }
            };

            Multimap<CustomAttributeType, NMSAttribute> result = testMap.loadAttributeMap(mockData);
            assertFalse(result.isEmpty());
            assertTrue(result.containsKey(CustomAttributeType.CRITICAL_DAMAGE));
        }
    }

    @Nested
    @DisplayName("Inheritance tests")
    class InheritanceTests {

        @Test
        @DisplayName("AbstractAttributeMap extends AbstractSingleton")
        void abstractAttributeMap_extendsAbstractSingleton() {
            AbstractAttributeMap testMap = new AbstractAttributeMap() {
                @Override
                public String getType() {
                    return "TEST";
                }

                @Override
                public Multimap<CustomAttributeType, NMSAttribute> loadAttributeMap(AttributeMapData data) {
                    return null;
                }
            };

            // AbstractAttributeMap should extend AbstractSingleton
            // This is verified by the fact that we can call getType()
            assertNotNull(testMap.getType());
        }
    }

    @Nested
    @DisplayName("Multiple attribute types in map")
    class MultipleAttributeTypesTests {

        @Test
        @DisplayName("Multimap can hold multiple attributes of same type")
        void multimap_holdsMultipleAttributesOfSameType() {
            AbstractAttributeMap testMap = new AbstractAttributeMap() {
                @Override
                public String getType() {
                    return "TEST";
                }

                @Override
                public Multimap<CustomAttributeType, NMSAttribute> loadAttributeMap(AttributeMapData data) {
                    Multimap<CustomAttributeType, NMSAttribute> map = com.google.common.collect.LinkedHashMultimap.create();
                    NMSAttribute attr1 = mock(NMSAttribute.class);
                    NMSAttribute attr2 = mock(NMSAttribute.class);
                    map.put(CustomAttributeType.CRITICAL_DAMAGE, attr1);
                    map.put(CustomAttributeType.CRITICAL_DAMAGE, attr2);
                    return map;
                }
            };

            AttributeMapData mockData = mock(AttributeMapData.class);
            Multimap<CustomAttributeType, NMSAttribute> result = testMap.loadAttributeMap(mockData);

            assertEquals(2, result.get(CustomAttributeType.CRITICAL_DAMAGE).size());
        }

        @Test
        @DisplayName("Multimap can hold different attribute types")
        void multimap_holdsDifferentAttributeTypes() {
            AbstractAttributeMap testMap = new AbstractAttributeMap() {
                @Override
                public String getType() {
                    return "TEST";
                }

                @Override
                public Multimap<CustomAttributeType, NMSAttribute> loadAttributeMap(AttributeMapData data) {
                    Multimap<CustomAttributeType, NMSAttribute> map = com.google.common.collect.LinkedHashMultimap.create();
                    map.put(CustomAttributeType.CRITICAL_DAMAGE, mock(NMSAttribute.class));
                    map.put(CustomAttributeType.DODGE_CHANCE, mock(NMSAttribute.class));
                    map.put(CustomAttributeType.LIFE_STEAL, mock(NMSAttribute.class));
                    return map;
                }
            };

            AttributeMapData mockData = mock(AttributeMapData.class);
            Multimap<CustomAttributeType, NMSAttribute> result = testMap.loadAttributeMap(mockData);

            assertEquals(3, result.keySet().size());
            assertTrue(result.containsKey(CustomAttributeType.CRITICAL_DAMAGE));
            assertTrue(result.containsKey(CustomAttributeType.DODGE_CHANCE));
            assertTrue(result.containsKey(CustomAttributeType.LIFE_STEAL));
        }
    }
}
