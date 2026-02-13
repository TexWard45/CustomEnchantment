package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.item.CEWeaponAbstract;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerEquipment")
@ExtendWith(MockitoExtension.class)
class PlayerEquipmentTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private PlayerSet mockPlayerSet;

    private PlayerEquipment playerEquipment;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockCEPlayer.getSet()).thenReturn(mockPlayerSet);
        playerEquipment = new PlayerEquipment(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerEquipment);
            assertSame(mockCEPlayer, playerEquipment.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerEquipment instanceof CEPlayerExpansion);
        }

        @Test
        @DisplayName("Should not have wings initially")
        void shouldNotHaveWingsInitially() {
            assertFalse(playerEquipment.hasWings());
        }

        @Test
        @DisplayName("Should not have offhand item initially")
        void shouldNotHaveOffhandItemInitially() {
            assertFalse(playerEquipment.hasOffhandItemStack());
        }
    }

    @Nested
    @DisplayName("Slot Management Tests")
    class SlotManagementTests {

        @Test
        @DisplayName("getSlot should return null for empty slot")
        void getSlotShouldReturnNullForEmptySlot() {
            assertNull(playerEquipment.getSlot(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("setSlot should store weapon in slot")
        void setSlotShouldStoreWeapon() {
            CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
            playerEquipment.setSlot(EquipSlot.MAINHAND, mockWeapon);

            assertSame(mockWeapon, playerEquipment.getSlot(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("setSlot with null should remove weapon from slot")
        void setSlotWithNullShouldRemoveWeapon() {
            CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
            playerEquipment.setSlot(EquipSlot.MAINHAND, mockWeapon);
            playerEquipment.setSlot(EquipSlot.MAINHAND, null);

            assertNull(playerEquipment.getSlot(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("setSlot should call onUpdate on PlayerSet")
        void setSlotShouldCallOnUpdateOnPlayerSet() {
            CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
            playerEquipment.setSlot(EquipSlot.MAINHAND, mockWeapon);

            verify(mockPlayerSet).onUpdate();
        }
    }

    @Nested
    @DisplayName("Disable Slot Tests")
    class DisableSlotTests {

        @Test
        @DisplayName("Should disable slot")
        void shouldDisableSlot() {
            playerEquipment.setDisableSlot(EquipSlot.MAINHAND, true);
            assertTrue(playerEquipment.isDisableSlot(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("Should enable slot")
        void shouldEnableSlot() {
            playerEquipment.setDisableSlot(EquipSlot.MAINHAND, true);
            playerEquipment.setDisableSlot(EquipSlot.MAINHAND, false);
            assertFalse(playerEquipment.isDisableSlot(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("getSlot should return null for disabled slot with check=true")
        void getSlotShouldReturnNullForDisabledSlot() {
            CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
            playerEquipment.setSlot(EquipSlot.MAINHAND, mockWeapon);
            playerEquipment.setDisableSlot(EquipSlot.MAINHAND, true);

            assertNull(playerEquipment.getSlot(EquipSlot.MAINHAND, true));
        }

        @Test
        @DisplayName("getSlot should return weapon for disabled slot with check=false")
        void getSlotShouldReturnWeaponForDisabledSlotWithNoCheck() {
            CEWeaponAbstract mockWeapon = mock(CEWeaponAbstract.class);
            playerEquipment.setSlot(EquipSlot.MAINHAND, mockWeapon);
            playerEquipment.setDisableSlot(EquipSlot.MAINHAND, true);

            assertSame(mockWeapon, playerEquipment.getSlot(EquipSlot.MAINHAND, false));
        }
    }

    @Nested
    @DisplayName("getSlotMap Tests")
    class GetSlotMapTests {

        @Test
        @DisplayName("Should return empty map when no slots set")
        void shouldReturnEmptyMapWhenNoSlots() {
            Map<EquipSlot, CEWeaponAbstract> map = playerEquipment.getSlotMap();
            assertTrue(map.isEmpty());
        }

        @Test
        @DisplayName("Should exclude disabled slots with check=true")
        void shouldExcludeDisabledSlotsWithCheck() {
            CEWeaponAbstract mockWeapon1 = mock(CEWeaponAbstract.class);
            CEWeaponAbstract mockWeapon2 = mock(CEWeaponAbstract.class);
            playerEquipment.setSlot(EquipSlot.MAINHAND, mockWeapon1);
            playerEquipment.setSlot(EquipSlot.OFFHAND, mockWeapon2);
            playerEquipment.setDisableSlot(EquipSlot.MAINHAND, true);

            Map<EquipSlot, CEWeaponAbstract> map = playerEquipment.getSlotMap(true);
            assertEquals(1, map.size());
            assertFalse(map.containsKey(EquipSlot.MAINHAND));
            assertTrue(map.containsKey(EquipSlot.OFFHAND));
        }

        @Test
        @DisplayName("Should include all slots with check=false")
        void shouldIncludeAllSlotsWithNoCheck() {
            CEWeaponAbstract mockWeapon1 = mock(CEWeaponAbstract.class);
            CEWeaponAbstract mockWeapon2 = mock(CEWeaponAbstract.class);
            playerEquipment.setSlot(EquipSlot.MAINHAND, mockWeapon1);
            playerEquipment.setSlot(EquipSlot.OFFHAND, mockWeapon2);
            playerEquipment.setDisableSlot(EquipSlot.MAINHAND, true);

            Map<EquipSlot, CEWeaponAbstract> map = playerEquipment.getSlotMap(false);
            assertEquals(2, map.size());
        }

        @Test
        @DisplayName("getSlotMap should return a new copy each time")
        void getSlotMapShouldReturnCopy() {
            Map<EquipSlot, CEWeaponAbstract> map1 = playerEquipment.getSlotMap();
            Map<EquipSlot, CEWeaponAbstract> map2 = playerEquipment.getSlotMap();
            assertNotSame(map1, map2);
        }
    }

    @Nested
    @DisplayName("CEItem Cache Tests")
    class CEItemCacheTests {

        @Test
        @DisplayName("Should set and get CEItem cache")
        void shouldSetAndGetCEItemCache() {
            com.bafmc.customenchantment.item.CEItem mockCEItem = mock(com.bafmc.customenchantment.item.CEItem.class);
            playerEquipment.setCEItemCache(5, mockCEItem);

            assertSame(mockCEItem, playerEquipment.getCEItemCache(5));
        }

        @Test
        @DisplayName("Should return null for non-cached slot index")
        void shouldReturnNullForNonCachedSlotIndex() {
            assertNull(playerEquipment.getCEItemCache(99));
        }

        @Test
        @DisplayName("Should remove CEItem cache")
        void shouldRemoveCEItemCache() {
            com.bafmc.customenchantment.item.CEItem mockCEItem = mock(com.bafmc.customenchantment.item.CEItem.class);
            playerEquipment.setCEItemCache(5, mockCEItem);
            playerEquipment.removeCEItemCache(5);

            assertNull(playerEquipment.getCEItemCache(5));
        }
    }

    @Nested
    @DisplayName("Wings Tests")
    class WingsTests {

        @Test
        @DisplayName("hasWings should return false when wings is null")
        void hasWingsShouldReturnFalseWhenNull() {
            assertFalse(playerEquipment.hasWings());
        }

        @Test
        @DisplayName("hasWings should return true when wings is set")
        void hasWingsShouldReturnTrueWhenSet() {
            CEWeaponAbstract mockWings = mock(CEWeaponAbstract.class);
            playerEquipment.setWings(mockWings);
            assertTrue(playerEquipment.hasWings());
        }

        @Test
        @DisplayName("getWings should return stored wings")
        void getWingsShouldReturnStoredWings() {
            CEWeaponAbstract mockWings = mock(CEWeaponAbstract.class);
            playerEquipment.setWings(mockWings);
            assertSame(mockWings, playerEquipment.getWings());
        }
    }

    @Nested
    @DisplayName("Offhand Tests")
    class OffhandTests {

        @Test
        @DisplayName("hasOffhandItemStack should return false when null")
        void hasOffhandShouldReturnFalseWhenNull() {
            assertFalse(playerEquipment.hasOffhandItemStack());
        }

        @Test
        @DisplayName("hasOffhandItemStack should return false for AIR")
        void hasOffhandShouldReturnFalseForAir() {
            ItemStack airItem = mock(ItemStack.class);
            when(airItem.getType()).thenReturn(Material.AIR);
            playerEquipment.setOffhandItemStack(airItem);

            assertFalse(playerEquipment.hasOffhandItemStack());
        }

        @Test
        @DisplayName("hasOffhandItemStack should return true for non-air item")
        void hasOffhandShouldReturnTrueForNonAirItem() {
            ItemStack diamondItem = mock(ItemStack.class);
            when(diamondItem.getType()).thenReturn(Material.DIAMOND);
            playerEquipment.setOffhandItemStack(diamondItem);

            assertTrue(playerEquipment.hasOffhandItemStack());
        }

        @Test
        @DisplayName("getActualOffhandItemStack should return offhand when set")
        void getActualOffhandShouldReturnOffhandWhenSet() {
            ItemStack item = mock(ItemStack.class);
            when(item.getType()).thenReturn(Material.SHIELD);
            playerEquipment.setOffhandItemStack(item);

            assertSame(item, playerEquipment.getActualOffhandItemStack());
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("slotMap should be ConcurrentHashMap")
        void slotMapShouldBeConcurrentHashMap() throws Exception {
            var field = PlayerEquipment.class.getDeclaredField("slotMap");
            field.setAccessible(true);
            assertTrue(field.get(playerEquipment) instanceof ConcurrentHashMap);
        }

        @Test
        @DisplayName("disableSlotMap should be ConcurrentHashMap")
        void disableSlotMapShouldBeConcurrentHashMap() throws Exception {
            var field = PlayerEquipment.class.getDeclaredField("disableSlotMap");
            field.setAccessible(true);
            assertTrue(field.get(playerEquipment) instanceof ConcurrentHashMap);
        }
    }
}
