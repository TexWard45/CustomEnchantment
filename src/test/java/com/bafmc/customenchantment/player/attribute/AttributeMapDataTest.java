package com.bafmc.customenchantment.player.attribute;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import com.bafmc.customenchantment.player.CEPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for AttributeMapData class.
 * Tests the data container for attribute maps.
 */
class AttributeMapDataTest {

    private CEPlayer mockPlayer;
    private Map<EquipSlot, CEWeaponAbstract> slotMap;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(CEPlayer.class);
        slotMap = new HashMap<>();
    }

    @Nested
    @DisplayName("Builder tests")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates instance with all fields")
        void builder_createsInstanceWithAllFields() {
            slotMap.put(EquipSlot.MAINHAND, mock(CEWeaponAbstract.class));

            AttributeMapData data = AttributeMapData.builder()
                .cePlayer(mockPlayer)
                .slotMap(slotMap)
                .build();

            assertNotNull(data);
            assertEquals(mockPlayer, data.getCePlayer());
            assertEquals(slotMap, data.getSlotMap());
        }

        @Test
        @DisplayName("Builder works with empty slot map")
        void builder_worksWithEmptySlotMap() {
            AttributeMapData data = AttributeMapData.builder()
                .cePlayer(mockPlayer)
                .slotMap(new HashMap<>())
                .build();

            assertNotNull(data);
            assertTrue(data.getSlotMap().isEmpty());
        }

        @Test
        @DisplayName("Builder accepts null player")
        void builder_acceptsNullPlayer() {
            AttributeMapData data = AttributeMapData.builder()
                .cePlayer(null)
                .slotMap(slotMap)
                .build();

            assertNull(data.getCePlayer());
        }

        @Test
        @DisplayName("Builder accepts null slot map")
        void builder_acceptsNullSlotMap() {
            AttributeMapData data = AttributeMapData.builder()
                .cePlayer(mockPlayer)
                .slotMap(null)
                .build();

            assertNull(data.getSlotMap());
        }
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("AllArgsConstructor creates instance correctly")
        void allArgsConstructor_createsInstanceCorrectly() {
            AttributeMapData data = new AttributeMapData(mockPlayer, slotMap);

            assertEquals(mockPlayer, data.getCePlayer());
            assertEquals(slotMap, data.getSlotMap());
        }
    }

    @Nested
    @DisplayName("Getter tests")
    class GetterTests {

        @Test
        @DisplayName("getCePlayer returns correct player")
        void getCePlayer_returnsCorrectPlayer() {
            AttributeMapData data = new AttributeMapData(mockPlayer, slotMap);

            assertEquals(mockPlayer, data.getCePlayer());
        }

        @Test
        @DisplayName("getSlotMap returns correct map")
        void getSlotMap_returnsCorrectMap() {
            CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
            slotMap.put(EquipSlot.MAINHAND, mockWeapon);

            AttributeMapData data = new AttributeMapData(mockPlayer, slotMap);

            assertEquals(slotMap, data.getSlotMap());
            assertEquals(mockWeapon, data.getSlotMap().get(EquipSlot.MAINHAND));
        }
    }

    @Nested
    @DisplayName("Slot map content tests")
    class SlotMapContentTests {

        @Test
        @DisplayName("Slot map can contain MAINHAND weapon")
        void slotMap_canContainMainhandWeapon() {
            CEWeaponAbstract mainhandWeapon = mock(CEWeaponAbstract.class);
            slotMap.put(EquipSlot.MAINHAND, mainhandWeapon);

            AttributeMapData data = new AttributeMapData(mockPlayer, slotMap);

            assertTrue(data.getSlotMap().containsKey(EquipSlot.MAINHAND));
            assertEquals(mainhandWeapon, data.getSlotMap().get(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("Slot map can contain OFFHAND weapon")
        void slotMap_canContainOffhandWeapon() {
            CEWeaponAbstract offhandWeapon = mock(CEWeaponAbstract.class);
            slotMap.put(EquipSlot.OFFHAND, offhandWeapon);

            AttributeMapData data = new AttributeMapData(mockPlayer, slotMap);

            assertTrue(data.getSlotMap().containsKey(EquipSlot.OFFHAND));
        }

        @Test
        @DisplayName("Slot map can contain multiple equipment slots")
        void slotMap_canContainMultipleSlots() {
            slotMap.put(EquipSlot.MAINHAND, mock(CEWeaponAbstract.class));
            slotMap.put(EquipSlot.OFFHAND, mock(CEWeaponAbstract.class));
            slotMap.put(EquipSlot.HELMET, mock(CEWeaponAbstract.class));
            slotMap.put(EquipSlot.CHESTPLATE, mock(CEWeaponAbstract.class));
            slotMap.put(EquipSlot.LEGGINGS, mock(CEWeaponAbstract.class));
            slotMap.put(EquipSlot.BOOTS, mock(CEWeaponAbstract.class));

            AttributeMapData data = new AttributeMapData(mockPlayer, slotMap);

            assertEquals(6, data.getSlotMap().size());
        }

        @Test
        @DisplayName("Slot map can have null values for slots")
        void slotMap_canHaveNullValues() {
            slotMap.put(EquipSlot.MAINHAND, null);

            AttributeMapData data = new AttributeMapData(mockPlayer, slotMap);

            assertTrue(data.getSlotMap().containsKey(EquipSlot.MAINHAND));
            assertNull(data.getSlotMap().get(EquipSlot.MAINHAND));
        }
    }

    @Nested
    @DisplayName("Immutability tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Returned slot map is same reference")
        void getSlotMap_returnsSameReference() {
            AttributeMapData data = new AttributeMapData(mockPlayer, slotMap);

            assertSame(slotMap, data.getSlotMap());
        }

        @Test
        @DisplayName("Modifying original map affects data object")
        void modifyingOriginalMap_affectsDataObject() {
            AttributeMapData data = new AttributeMapData(mockPlayer, slotMap);
            CEWeaponAbstract newWeapon = mock(CEWeaponAbstract.class);

            slotMap.put(EquipSlot.MAINHAND, newWeapon);

            assertEquals(newWeapon, data.getSlotMap().get(EquipSlot.MAINHAND));
        }
    }
}
