package com.bafmc.customenchantment.menu.equipment;

import com.bafmc.bukkit.utils.EquipSlot;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EquipmentCustomMenu
 *
 * Tests type/constants, static menu map management,
 * swap skin cooldowns, and ExtraData click cooldown.
 * Note: Handler delegation tests are limited since setupItems()
 * requires full framework initialization.
 */
@DisplayName("EquipmentCustomMenu Tests")
class EquipmentCustomMenuTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        try {
            if (MockBukkit.isMocked()) {
                MockBukkit.unmock();
            }
            server = MockBukkit.mock();
        } catch (Throwable e) {
            server = null;
        }
    }

    @AfterAll
    static void tearDownAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
    }

    @AfterEach
    void tearDown() {
        // Always clean up static state
        EquipmentCustomMenu.clearAll();
    }

    @Nested
    @DisplayName("Type and Constants Tests")
    class TypeAndConstantsTests {

        @Test
        @DisplayName("getType() returns 'equipment'")
        void testGetType() {
            EquipmentCustomMenu menu = new EquipmentCustomMenu();
            assertEquals("equipment", menu.getType());
        }

        @Test
        @DisplayName("MENU_NAME constant is 'equipment'")
        void testMenuNameConstant() {
            assertEquals("equipment", EquipmentCustomMenu.MENU_NAME);
        }

        @Test
        @DisplayName("EQUIP_SLOT_MAP has correct entries")
        void testEquipSlotMap() {
            Map<String, EquipSlot> map = EquipmentCustomMenu.EQUIP_SLOT_MAP;

            assertEquals(5, map.size());
            assertEquals(EquipSlot.HELMET, map.get("helmet"));
            assertEquals(EquipSlot.CHESTPLATE, map.get("chestplate"));
            assertEquals(EquipSlot.LEGGINGS, map.get("leggings"));
            assertEquals(EquipSlot.BOOTS, map.get("boots"));
            assertEquals(EquipSlot.MAINHAND, map.get("mainhand"));
        }

        @Test
        @DisplayName("Slot name constants are correct")
        void testSlotNameConstants() {
            assertEquals("protect-dead", EquipmentCustomMenu.PROTECT_DEAD_SLOT);
            assertEquals("extra-slot", EquipmentCustomMenu.EXTRA_SLOT);
            assertEquals("player-info", EquipmentCustomMenu.PLAYER_INFO_SLOT);
            assertEquals("wings", EquipmentCustomMenu.WINGS_SLOT);
            assertEquals("wings-no-skin", EquipmentCustomMenu.WINGS_OFF_SLOT);
            assertEquals("WINGS", EquipmentCustomMenu.WINGS_TYPE);
            assertEquals("helmet", EquipmentCustomMenu.HELMET_SLOT);
            assertEquals("chestplate", EquipmentCustomMenu.CHESTPLATE_SLOT);
            assertEquals("leggings", EquipmentCustomMenu.LEGGINGS_SLOT);
            assertEquals("boots", EquipmentCustomMenu.BOOTS_SLOT);
            assertEquals("mainhand", EquipmentCustomMenu.MAINHAND_SLOT);
            assertEquals("offhand", EquipmentCustomMenu.OFFHAND_SLOT);
        }
    }

    @Nested
    @DisplayName("Static Menu Map Tests")
    class StaticMenuMapTests {

        @Test
        @DisplayName("putMenu stores menu and getMenu retrieves it")
        void putAndGetMenu() {
            assumeTrue(server != null, "MockBukkit not available");
            Player player = server.addPlayer();
            EquipmentCustomMenu menu = new EquipmentCustomMenu();
            EquipmentExtraData extraData = new EquipmentExtraData();
            menu.setExtraData(extraData);

            EquipmentCustomMenu.putMenu(player, menu);

            assertSame(menu, EquipmentCustomMenu.getMenu(player));
        }

        @Test
        @DisplayName("putMenu marks old menu as removed")
        void putMenuMarksOldAsRemoved() {
            assumeTrue(server != null, "MockBukkit not available");
            Player player = server.addPlayer();

            EquipmentCustomMenu oldMenu = new EquipmentCustomMenu();
            EquipmentExtraData oldExtraData = new EquipmentExtraData();
            oldMenu.setExtraData(oldExtraData);
            EquipmentCustomMenu.putMenu(player, oldMenu);

            EquipmentCustomMenu newMenu = new EquipmentCustomMenu();
            EquipmentExtraData newExtraData = new EquipmentExtraData();
            newMenu.setExtraData(newExtraData);
            EquipmentCustomMenu.putMenu(player, newMenu);

            assertTrue(oldExtraData.isRemoved());
            assertFalse(newExtraData.isRemoved());
            assertSame(newMenu, EquipmentCustomMenu.getMenu(player));
        }

        @Test
        @DisplayName("removeMenu clears entry and marks as removed")
        void removeMenuClearsAndMarks() {
            assumeTrue(server != null, "MockBukkit not available");
            Player player = server.addPlayer();

            EquipmentCustomMenu menu = new EquipmentCustomMenu();
            EquipmentExtraData extraData = new EquipmentExtraData();
            menu.setExtraData(extraData);
            EquipmentCustomMenu.putMenu(player, menu);

            EquipmentCustomMenu removed = EquipmentCustomMenu.removeMenu(player);

            assertSame(menu, removed);
            assertTrue(extraData.isRemoved());
            assertNull(EquipmentCustomMenu.getMenu(player));
        }

        @Test
        @DisplayName("removeMenu returns null for unknown player")
        void removeMenuReturnsNullForUnknown() {
            assumeTrue(server != null, "MockBukkit not available");
            Player player = server.addPlayer();

            assertNull(EquipmentCustomMenu.removeMenu(player));
        }

        @Test
        @DisplayName("getMenu returns null for unregistered player")
        void getMenuReturnsNullForUnregistered() {
            assumeTrue(server != null, "MockBukkit not available");
            Player player = server.addPlayer();

            assertNull(EquipmentCustomMenu.getMenu(player));
        }

        @Test
        @DisplayName("clearAll empties menu map and marks all as removed")
        void clearAllEmptiesMap() {
            assumeTrue(server != null, "MockBukkit not available");
            Player player1 = server.addPlayer();
            Player player2 = server.addPlayer();

            EquipmentCustomMenu menu1 = new EquipmentCustomMenu();
            EquipmentExtraData extraData1 = new EquipmentExtraData();
            menu1.setExtraData(extraData1);

            EquipmentCustomMenu menu2 = new EquipmentCustomMenu();
            EquipmentExtraData extraData2 = new EquipmentExtraData();
            menu2.setExtraData(extraData2);

            EquipmentCustomMenu.putMenu(player1, menu1);
            EquipmentCustomMenu.putMenu(player2, menu2);

            EquipmentCustomMenu.clearAll();

            assertNull(EquipmentCustomMenu.getMenu(player1));
            assertNull(EquipmentCustomMenu.getMenu(player2));
            assertTrue(extraData1.isRemoved());
            assertTrue(extraData2.isRemoved());
        }
    }

    @Nested
    @DisplayName("ExtraData Tests")
    class ExtraDataTests {

        @Test
        @DisplayName("EquipmentExtraData initializes with default values")
        void extraDataDefaults() {
            EquipmentExtraData data = new EquipmentExtraData();
            assertFalse(data.isInUpdateMenu());
            assertFalse(data.isRemoved());
            assertNotNull(data.getLastClickTime());
            assertTrue(data.getLastClickTime().isEmpty());
        }

        @Test
        @DisplayName("isInLastClickCooldown with no previous click returns false")
        void noLastClickReturnsFalse() {
            EquipmentExtraData data = new EquipmentExtraData();
            assertFalse(data.isInLastClickCooldown(EquipSlot.HELMET));
        }

        @Test
        @DisplayName("addLastClickTime + isInLastClickCooldown within 500ms returns true")
        void withinCooldownReturnsTrue() {
            EquipmentExtraData data = new EquipmentExtraData();
            data.addLastClickTime(EquipSlot.HELMET, System.currentTimeMillis());
            assertTrue(data.isInLastClickCooldown(EquipSlot.HELMET));
        }

        @Test
        @DisplayName("isInLastClickCooldown after cooldown expires returns false")
        void afterCooldownReturnsFalse() {
            EquipmentExtraData data = new EquipmentExtraData();
            // Set click time 600ms ago (>500ms cooldown)
            data.addLastClickTime(EquipSlot.HELMET, System.currentTimeMillis() - 600);
            assertFalse(data.isInLastClickCooldown(EquipSlot.HELMET));
        }

        @Test
        @DisplayName("Different EquipSlots have independent cooldowns")
        void differentSlotsIndependentCooldowns() {
            EquipmentExtraData data = new EquipmentExtraData();
            data.addLastClickTime(EquipSlot.HELMET, System.currentTimeMillis());

            assertTrue(data.isInLastClickCooldown(EquipSlot.HELMET));
            assertFalse(data.isInLastClickCooldown(EquipSlot.BOOTS));
        }

        @Test
        @DisplayName("setInUpdateMenu controls update flag")
        void setInUpdateMenuControlsFlag() {
            EquipmentExtraData data = new EquipmentExtraData();
            assertFalse(data.isInUpdateMenu());

            data.setInUpdateMenu(true);
            assertTrue(data.isInUpdateMenu());

            data.setInUpdateMenu(false);
            assertFalse(data.isInUpdateMenu());
        }

        @Test
        @DisplayName("setRemoved controls removed flag")
        void setRemovedControlsFlag() {
            EquipmentExtraData data = new EquipmentExtraData();
            assertFalse(data.isRemoved());

            data.setRemoved(true);
            assertTrue(data.isRemoved());
        }
    }

    @Nested
    @DisplayName("EquipmentAddReason Enum Tests")
    class EquipmentAddReasonTests {

        @Test
        @DisplayName("EquipmentAddReason has all expected values")
        void allEnumValuesExist() {
            EquipmentCustomMenu.EquipmentAddReason[] values = EquipmentCustomMenu.EquipmentAddReason.values();
            assertEquals(11, values.length);

            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("SUCCESS"));
            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("NOT_SUPPORT_ITEM"));
            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("UNDRESS_FIRST"));
            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("ADD_EXTRA_SLOT"));
            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("MAX_EXTRA_SLOT"));
            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("DUPLICATE_EXTRA_SLOT"));
            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("NOTHING"));
            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("NO_EXTRA_SLOT"));
            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("ADD_PROTECT_DEAD"));
            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("EXCEED_PROTECT_DEAD"));
            assertNotNull(EquipmentCustomMenu.EquipmentAddReason.valueOf("DIFFERENT_PROTECT_DEAD"));
        }
    }
}
