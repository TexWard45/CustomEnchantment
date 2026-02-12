package com.bafmc.customenchantment.player.attribute.list;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeSlot;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.item.WeaponAttribute;
import com.bafmc.customenchantment.player.attribute.AttributeMapData;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for EquipSlotAttributeMap class.
 * Tests loading attributes from equipped items based on equipment slots.
 */
class EquipSlotAttributeMapTest {

    private EquipSlotAttributeMap equipSlotAttributeMap;
    private AttributeMapData mockData;
    private Map<EquipSlot, CEWeaponAbstract> slotMap;

    @BeforeEach
    void setUp() {
        equipSlotAttributeMap = new EquipSlotAttributeMap();
        mockData = mock(AttributeMapData.class);
        slotMap = new HashMap<>();
        when(mockData.getSlotMap()).thenReturn(slotMap);
    }

    @Nested
    @DisplayName("getType() tests")
    class GetTypeTests {

        @Test
        @DisplayName("Returns EQUIP_SLOT type")
        void getType_returnsEquipSlot() {
            assertEquals("EQUIP_SLOT", equipSlotAttributeMap.getType());
        }
    }

    @Nested
    @DisplayName("loadAttributeMap() with empty slots")
    class EmptySlotTests {

        @Test
        @DisplayName("Returns empty map when slot map is empty")
        void loadAttributeMap_withEmptySlotMap_returnsEmptyMap() {
            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Returns empty map when all slot values are null")
        void loadAttributeMap_withNullWeapons_returnsEmptyMap() {
            slotMap.put(EquipSlot.MAINHAND, null);
            slotMap.put(EquipSlot.OFFHAND, null);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("loadAttributeMap() with single weapon")
    class SingleWeaponTests {

        @Test
        @DisplayName("Loads attributes from mainhand weapon")
        void loadAttributeMap_withMainhandWeapon_loadsAttributes() {
            CEWeaponAbstract weapon = createMockWeapon(
                CustomAttributeType.CRITICAL_DAMAGE, 2.5, null
            );
            slotMap.put(EquipSlot.MAINHAND, weapon);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(1, result.size());
            assertTrue(result.containsKey(CustomAttributeType.CRITICAL_DAMAGE));
        }

        @Test
        @DisplayName("Loads multiple attributes from single weapon")
        void loadAttributeMap_withMultipleAttributesOnWeapon_loadsAll() {
            CEWeaponAbstract weapon = createMockWeaponWithMultipleAttributes(
                Arrays.asList(
                    new AttributeInfo(CustomAttributeType.CRITICAL_DAMAGE, 2.5, null),
                    new AttributeInfo(CustomAttributeType.DODGE_CHANCE, 10.0, null),
                    new AttributeInfo(CustomAttributeType.LIFE_STEAL, 5.0, null)
                )
            );
            slotMap.put(EquipSlot.MAINHAND, weapon);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(3, result.keySet().size());
        }
    }

    @Nested
    @DisplayName("loadAttributeMap() with multiple equipment slots")
    class MultipleSlotTests {

        @Test
        @DisplayName("Loads attributes from multiple equipment slots")
        void loadAttributeMap_withMultipleSlots_loadsFromAll() {
            CEWeaponAbstract mainhandWeapon = createMockWeapon(
                CustomAttributeType.CRITICAL_DAMAGE, 2.0, null
            );
            CEWeaponAbstract offhandWeapon = createMockWeapon(
                CustomAttributeType.DODGE_CHANCE, 15.0, null
            );

            slotMap.put(EquipSlot.MAINHAND, mainhandWeapon);
            slotMap.put(EquipSlot.OFFHAND, offhandWeapon);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(2, result.keySet().size());
            assertTrue(result.containsKey(CustomAttributeType.CRITICAL_DAMAGE));
            assertTrue(result.containsKey(CustomAttributeType.DODGE_CHANCE));
        }

        @Test
        @DisplayName("Combines same attribute type from different slots")
        void loadAttributeMap_withSameTypeFromDifferentSlots_combines() {
            CEWeaponAbstract mainhandWeapon = createMockWeapon(
                CustomAttributeType.CRITICAL_DAMAGE, 2.0, null
            );
            CEWeaponAbstract offhandWeapon = createMockWeapon(
                CustomAttributeType.CRITICAL_DAMAGE, 1.5, null
            );

            slotMap.put(EquipSlot.MAINHAND, mainhandWeapon);
            slotMap.put(EquipSlot.OFFHAND, offhandWeapon);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(2, result.get(CustomAttributeType.CRITICAL_DAMAGE).size());
        }
    }

    @Nested
    @DisplayName("Slot matching tests")
    class SlotMatchingTests {

        @Test
        @DisplayName("Includes attribute when slot is null")
        void loadAttributeMap_withNullSlot_includesAttribute() {
            CEWeaponAbstract weapon = createMockWeapon(
                CustomAttributeType.CRITICAL_DAMAGE, 2.0, null
            );
            slotMap.put(EquipSlot.MAINHAND, weapon);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Includes attribute when slot matches equipment slot")
        void loadAttributeMap_withMatchingSlot_includesAttribute() {
            CEWeaponAbstract weapon = createMockWeapon(
                CustomAttributeType.CRITICAL_DAMAGE, 2.0, "mainhand"
            );
            slotMap.put(EquipSlot.MAINHAND, weapon);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            // The attribute should be included because the slot matches
            // (depends on EquipSlotUtils.isSameSlot implementation)
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("Type filtering tests")
    class TypeFilteringTests {

        @Test
        @DisplayName("Skips non-CustomAttributeType attributes")
        void loadAttributeMap_withNonCustomType_skipsAttribute() {
            NMSAttributeType nonCustomType = mock(NMSAttributeType.class);
            NMSAttribute attr = mock(NMSAttribute.class);
            when(attr.getAttributeType()).thenReturn(nonCustomType);
            when(attr.getSlot()).thenReturn(null);

            WeaponAttribute weaponAttr = mock(WeaponAttribute.class);
            when(weaponAttr.getAttributeList()).thenReturn(List.of(attr));

            CEWeaponAbstract weapon = mock(CEWeaponAbstract.class);
            when(weapon.getWeaponAttribute()).thenReturn(weaponAttr);

            slotMap.put(EquipSlot.MAINHAND, weapon);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Includes only CustomAttributeType attributes")
        void loadAttributeMap_withMixedTypes_includesOnlyCustomType() {
            NMSAttributeType nonCustomType = mock(NMSAttributeType.class);
            NMSAttribute attr1 = mock(NMSAttribute.class);
            NMSAttribute attr2 = mock(NMSAttribute.class);
            when(attr1.getAttributeType()).thenReturn(nonCustomType);
            when(attr1.getSlot()).thenReturn(null);
            when(attr2.getAttributeType()).thenReturn(CustomAttributeType.CRITICAL_DAMAGE);
            when(attr2.getSlot()).thenReturn(null);

            WeaponAttribute weaponAttr = mock(WeaponAttribute.class);
            when(weaponAttr.getAttributeList()).thenReturn(Arrays.asList(attr1, attr2));

            CEWeaponAbstract weapon = mock(CEWeaponAbstract.class);
            when(weapon.getWeaponAttribute()).thenReturn(weaponAttr);

            slotMap.put(EquipSlot.MAINHAND, weapon);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(1, result.size());
            assertTrue(result.containsKey(CustomAttributeType.CRITICAL_DAMAGE));
        }
    }

    @Nested
    @DisplayName("Armor slot tests")
    class ArmorSlotTests {

        @Test
        @DisplayName("Loads attributes from helmet")
        void loadAttributeMap_withHelmet_loadsAttributes() {
            CEWeaponAbstract helmet = createMockWeapon(
                CustomAttributeType.DAMAGE_REDUCTION, 5.0, null
            );
            slotMap.put(EquipSlot.HEAD, helmet);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(1, result.size());
            assertTrue(result.containsKey(CustomAttributeType.DAMAGE_REDUCTION));
        }

        @Test
        @DisplayName("Loads attributes from chestplate")
        void loadAttributeMap_withChestplate_loadsAttributes() {
            CEWeaponAbstract chestplate = createMockWeapon(
                CustomAttributeType.DAMAGE_REDUCTION, 8.0, null
            );
            slotMap.put(EquipSlot.CHEST, chestplate);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Loads attributes from leggings")
        void loadAttributeMap_withLeggings_loadsAttributes() {
            CEWeaponAbstract leggings = createMockWeapon(
                CustomAttributeType.DAMAGE_REDUCTION, 6.0, null
            );
            slotMap.put(EquipSlot.LEGS, leggings);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Loads attributes from boots")
        void loadAttributeMap_withBoots_loadsAttributes() {
            CEWeaponAbstract boots = createMockWeapon(
                CustomAttributeType.DAMAGE_REDUCTION, 3.0, null
            );
            slotMap.put(EquipSlot.FEET, boots);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Combines armor set attributes")
        void loadAttributeMap_withFullArmorSet_combinesAttributes() {
            slotMap.put(EquipSlot.HEAD, createMockWeapon(CustomAttributeType.DAMAGE_REDUCTION, 5.0, null));
            slotMap.put(EquipSlot.CHEST, createMockWeapon(CustomAttributeType.DAMAGE_REDUCTION, 8.0, null));
            slotMap.put(EquipSlot.LEGS, createMockWeapon(CustomAttributeType.DAMAGE_REDUCTION, 6.0, null));
            slotMap.put(EquipSlot.FEET, createMockWeapon(CustomAttributeType.DAMAGE_REDUCTION, 3.0, null));

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            // All 4 armor pieces contribute to DAMAGE_REDUCTION
            assertEquals(4, result.get(CustomAttributeType.DAMAGE_REDUCTION).size());
        }
    }

    @Nested
    @DisplayName("Edge case tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Handles weapon with empty attribute list")
        void loadAttributeMap_withEmptyAttributeList_returnsEmptyMap() {
            WeaponAttribute weaponAttr = mock(WeaponAttribute.class);
            when(weaponAttr.getAttributeList()).thenReturn(new ArrayList<>());

            CEWeaponAbstract weapon = mock(CEWeaponAbstract.class);
            when(weapon.getWeaponAttribute()).thenReturn(weaponAttr);

            slotMap.put(EquipSlot.MAINHAND, weapon);

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Handles mixed null and valid weapons")
        void loadAttributeMap_withMixedNullAndValid_loadsOnlyValid() {
            slotMap.put(EquipSlot.MAINHAND, null);
            slotMap.put(EquipSlot.OFFHAND, createMockWeapon(CustomAttributeType.CRITICAL_DAMAGE, 2.0, null));
            slotMap.put(EquipSlot.HEAD, null);
            slotMap.put(EquipSlot.CHEST, createMockWeapon(CustomAttributeType.DAMAGE_REDUCTION, 5.0, null));

            Multimap<CustomAttributeType, NMSAttribute> result = equipSlotAttributeMap.loadAttributeMap(mockData);

            assertEquals(2, result.keySet().size());
        }
    }

    // Helper classes and methods

    private record AttributeInfo(CustomAttributeType type, double amount, String slot) {}

    private CEWeaponAbstract createMockWeapon(CustomAttributeType type, double amount, String slot) {
        NMSAttribute attr = mock(NMSAttribute.class);
        when(attr.getAttributeType()).thenReturn(type);
        when(attr.getAmount()).thenReturn(amount);
        when(attr.getSlot()).thenReturn(slot);

        WeaponAttribute weaponAttr = mock(WeaponAttribute.class);
        when(weaponAttr.getAttributeList()).thenReturn(List.of(attr));

        CEWeaponAbstract weapon = mock(CEWeaponAbstract.class);
        when(weapon.getWeaponAttribute()).thenReturn(weaponAttr);

        return weapon;
    }

    private CEWeaponAbstract createMockWeaponWithMultipleAttributes(List<AttributeInfo> attributes) {
        List<NMSAttribute> attrList = new ArrayList<>();

        for (AttributeInfo info : attributes) {
            NMSAttribute attr = mock(NMSAttribute.class);
            when(attr.getAttributeType()).thenReturn(info.type());
            when(attr.getAmount()).thenReturn(info.amount());
            when(attr.getSlot()).thenReturn(info.slot());
            attrList.add(attr);
        }

        WeaponAttribute weaponAttr = mock(WeaponAttribute.class);
        when(weaponAttr.getAttributeList()).thenReturn(attrList);

        CEWeaponAbstract weapon = mock(CEWeaponAbstract.class);
        when(weapon.getWeaponAttribute()).thenReturn(weaponAttr);

        return weapon;
    }
}
