package com.bafmc.customenchantment.player.attribute.list;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeOperation;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.attribute.AttributeMapData;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for DefaultAttributeMap class.
 * Tests loading attributes from player's custom attributes.
 */
class DefaultAttributeMapTest {

    private DefaultAttributeMap defaultAttributeMap;
    private CEPlayer mockPlayer;
    private AttributeMapData mockData;

    @BeforeEach
    void setUp() {
        defaultAttributeMap = new DefaultAttributeMap();
        mockPlayer = mock(CEPlayer.class, RETURNS_DEEP_STUBS);
        mockData = mock(AttributeMapData.class);
        when(mockData.getCePlayer()).thenReturn(mockPlayer);
    }

    @Nested
    @DisplayName("getType() tests")
    class GetTypeTests {

        @Test
        @DisplayName("Returns DEFAULT type")
        void getType_returnsDefault() {
            assertEquals("DEFAULT", defaultAttributeMap.getType());
        }
    }

    @Nested
    @DisplayName("loadAttributeMap() tests")
    class LoadAttributeMapTests {

        @Test
        @DisplayName("Returns empty map when player has no attributes")
        void loadAttributeMap_withNoAttributes_returnsEmptyMap() {
            when(mockPlayer.getCustomAttribute().getAttributeList()).thenReturn(new ArrayList<>());

            Multimap<CustomAttributeType, NMSAttribute> result = defaultAttributeMap.loadAttributeMap(mockData);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Loads single CustomAttributeType attribute")
        void loadAttributeMap_withSingleAttribute_loadsCorrectly() {
            NMSAttribute attr = mock(NMSAttribute.class);
            when(attr.getAttributeType()).thenReturn(CustomAttributeType.CRITICAL_DAMAGE);

            List<NMSAttribute> attributes = List.of(attr);
            when(mockPlayer.getCustomAttribute().getAttributeList()).thenReturn(attributes);

            Multimap<CustomAttributeType, NMSAttribute> result = defaultAttributeMap.loadAttributeMap(mockData);

            assertEquals(1, result.size());
            assertTrue(result.containsKey(CustomAttributeType.CRITICAL_DAMAGE));
            assertTrue(result.get(CustomAttributeType.CRITICAL_DAMAGE).contains(attr));
        }

        @Test
        @DisplayName("Loads multiple attributes of same type")
        void loadAttributeMap_withMultipleSameType_loadsAll() {
            NMSAttribute attr1 = mock(NMSAttribute.class);
            NMSAttribute attr2 = mock(NMSAttribute.class);
            when(attr1.getAttributeType()).thenReturn(CustomAttributeType.DODGE_CHANCE);
            when(attr2.getAttributeType()).thenReturn(CustomAttributeType.DODGE_CHANCE);

            List<NMSAttribute> attributes = Arrays.asList(attr1, attr2);
            when(mockPlayer.getCustomAttribute().getAttributeList()).thenReturn(attributes);

            Multimap<CustomAttributeType, NMSAttribute> result = defaultAttributeMap.loadAttributeMap(mockData);

            assertEquals(2, result.get(CustomAttributeType.DODGE_CHANCE).size());
        }

        @Test
        @DisplayName("Loads multiple different attribute types")
        void loadAttributeMap_withDifferentTypes_loadsAll() {
            NMSAttribute attr1 = mock(NMSAttribute.class);
            NMSAttribute attr2 = mock(NMSAttribute.class);
            NMSAttribute attr3 = mock(NMSAttribute.class);
            when(attr1.getAttributeType()).thenReturn(CustomAttributeType.CRITICAL_DAMAGE);
            when(attr2.getAttributeType()).thenReturn(CustomAttributeType.DODGE_CHANCE);
            when(attr3.getAttributeType()).thenReturn(CustomAttributeType.LIFE_STEAL);

            List<NMSAttribute> attributes = Arrays.asList(attr1, attr2, attr3);
            when(mockPlayer.getCustomAttribute().getAttributeList()).thenReturn(attributes);

            Multimap<CustomAttributeType, NMSAttribute> result = defaultAttributeMap.loadAttributeMap(mockData);

            assertEquals(3, result.keySet().size());
            assertTrue(result.containsKey(CustomAttributeType.CRITICAL_DAMAGE));
            assertTrue(result.containsKey(CustomAttributeType.DODGE_CHANCE));
            assertTrue(result.containsKey(CustomAttributeType.LIFE_STEAL));
        }

        @Test
        @DisplayName("Skips non-CustomAttributeType attributes")
        void loadAttributeMap_withNonCustomType_skipsAttribute() {
            NMSAttributeType nonCustomType = mock(NMSAttributeType.class);
            NMSAttribute attr1 = mock(NMSAttribute.class);
            NMSAttribute attr2 = mock(NMSAttribute.class);
            when(attr1.getAttributeType()).thenReturn(nonCustomType);
            when(attr2.getAttributeType()).thenReturn(CustomAttributeType.CRITICAL_DAMAGE);

            List<NMSAttribute> attributes = Arrays.asList(attr1, attr2);
            when(mockPlayer.getCustomAttribute().getAttributeList()).thenReturn(attributes);

            Multimap<CustomAttributeType, NMSAttribute> result = defaultAttributeMap.loadAttributeMap(mockData);

            assertEquals(1, result.size());
            assertTrue(result.containsKey(CustomAttributeType.CRITICAL_DAMAGE));
        }

        @Test
        @DisplayName("Skips attributes with null type")
        void loadAttributeMap_withNullType_skipsAttribute() {
            NMSAttribute attr1 = mock(NMSAttribute.class);
            NMSAttribute attr2 = mock(NMSAttribute.class);
            when(attr1.getAttributeType()).thenReturn(null);
            when(attr2.getAttributeType()).thenReturn(CustomAttributeType.CRITICAL_DAMAGE);

            List<NMSAttribute> attributes = Arrays.asList(attr1, attr2);
            when(mockPlayer.getCustomAttribute().getAttributeList()).thenReturn(attributes);

            Multimap<CustomAttributeType, NMSAttribute> result = defaultAttributeMap.loadAttributeMap(mockData);

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Integration tests")
    class IntegrationTests {

        @Test
        @DisplayName("Can be instantiated independently")
        void canBeInstantiatedIndependently() {
            DefaultAttributeMap map = new DefaultAttributeMap();

            assertNotNull(map);
            assertEquals("DEFAULT", map.getType());
        }

        @Test
        @DisplayName("Works with all custom attribute types")
        void worksWithAllCustomAttributeTypes() {
            List<NMSAttribute> attributes = new ArrayList<>();

            // Add one attribute for each CustomAttributeType
            for (CustomAttributeType type : CustomAttributeType.getValues()) {
                NMSAttribute attr = mock(NMSAttribute.class);
                when(attr.getAttributeType()).thenReturn(type);
                attributes.add(attr);
            }

            when(mockPlayer.getCustomAttribute().getAttributeList()).thenReturn(attributes);

            Multimap<CustomAttributeType, NMSAttribute> result = defaultAttributeMap.loadAttributeMap(mockData);

            assertEquals(CustomAttributeType.getValues().length, result.keySet().size());
        }
    }
}
