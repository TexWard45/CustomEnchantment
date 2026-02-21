package com.bafmc.customenchantment.menu.ceanvil;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.MenuData;
import com.bafmc.customenchantment.item.ApplyReason;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.CEWeapon;
import com.bafmc.customenchantment.menu.ceanvil.handler.DefaultHandler;
import com.bafmc.customenchantment.menu.ceanvil.handler.Slot2Handler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CEAnvilCustomMenu
 *
 * Tests handler routing, addItem/returnItem state machine,
 * confirm delegation, and menu close behavior.
 */
@DisplayName("CEAnvilCustomMenu Tests")
class CEAnvilCustomMenuTest {

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

    /** Helper to create a fully-initialized menu for tests that need MockBukkit. */
    private static MenuTestContext createMenuContext() {
        Player player = server.addPlayer();
        CEAnvilSettings settings = new CEAnvilSettings();
        settings.initialize(null);
        CEAnvilExtraData extraData = new CEAnvilExtraData(settings);
        CEAnvilCustomMenu menu = new CEAnvilCustomMenu();

        MenuData menuData = new MenuData();
        menuData.setId("ceanvil-test");
        menuData.setType("ce-anvil");

        menu.setExtraData(extraData);
        menu.setOwner(player);
        menu.setMenuData(menuData);

        Inventory inventory = server.createInventory(player, 54, "Test CE Anvil");
        menu.setInventory(inventory);

        return new MenuTestContext(menu, player, extraData, settings);
    }

    private record MenuTestContext(
            CEAnvilCustomMenu menu, Player player,
            CEAnvilExtraData extraData, CEAnvilSettings settings) {}

    // ==================== Pure Unit Tests (no MockBukkit required) ====================

    @Nested
    @DisplayName("Constants and Type Tests")
    class ConstantsTests {

        @Test
        @DisplayName("MENU_NAME constant is 'ce-anvil'")
        void testMenuNameConstant() {
            assertEquals("ce-anvil", CEAnvilCustomMenu.MENU_NAME);
        }

        @Test
        @DisplayName("getType() returns 'ce-anvil'")
        void testGetType() {
            assertEquals("ce-anvil", new CEAnvilCustomMenu().getType());
        }
    }

    @Nested
    @DisplayName("CEAnvilExtraData Tests")
    class ExtraDataTests {

        @Test
        @DisplayName("initializes with null fields")
        void extraDataInitializesWithNullFields() {
            CEAnvilSettings settings = new CEAnvilSettings();
            settings.initialize(null);
            CEAnvilExtraData data = new CEAnvilExtraData(settings);
            assertNull(data.getItemData1());
            assertNull(data.getItemData2());
            assertNull(data.getActiveHandler());
            assertNotNull(data.getSettings());
        }

        @Test
        @DisplayName("CEAnvilAddReason enum has all expected values")
        void addReasonEnumValues() {
            CEAnvilExtraData.CEAnvilAddReason[] values = CEAnvilExtraData.CEAnvilAddReason.values();
            assertEquals(4, values.length);
            assertNotNull(CEAnvilExtraData.CEAnvilAddReason.valueOf("SUCCESS"));
            assertNotNull(CEAnvilExtraData.CEAnvilAddReason.valueOf("ALREADY_HAS_SLOT1"));
            assertNotNull(CEAnvilExtraData.CEAnvilAddReason.valueOf("ALREADY_HAS_SLOT2"));
            assertNotNull(CEAnvilExtraData.CEAnvilAddReason.valueOf("NOT_SUITABLE"));
        }
    }

    @Nested
    @DisplayName("CEAnvilSettings Tests")
    class CEAnvilSettingsTests {

        @Test
        @DisplayName("initialize with null menuData uses default preview order")
        void initializeWithNullUsesDefaults() {
            CEAnvilSettings s = new CEAnvilSettings();
            s.initialize(null);
            int[] order = s.getPreviewIndexOrder();
            assertNotNull(order);
            assertArrayEquals(new int[]{3, 2, 4, 1, 5}, order);
        }
    }

    // ==================== MockBukkit-dependent Tests ====================

    @Nested
    @DisplayName("AnvilItemData Tests")
    class AnvilItemDataTests {

        @Test
        @DisplayName("clones ItemStack on creation")
        void clonesItemStack() {
            assumeTrue(server != null, "MockBukkit not available");
            ItemStack original = new ItemStack(Material.DIAMOND_SWORD, 3);
            CEItem ceItem = mock(CEItem.class);
            AnvilItemData data = new AnvilItemData(original, ceItem);
            assertEquals(Material.DIAMOND_SWORD, data.getItemStack().getType());
            assertEquals(3, data.getItemStack().getAmount());
            assertNotSame(original, data.getItemStack());
        }

        @Test
        @DisplayName("stores CEItem reference")
        void storesCEItem() {
            assumeTrue(server != null, "MockBukkit not available");
            CEItem ceItem = mock(CEItem.class);
            AnvilItemData data = new AnvilItemData(new ItemStack(Material.DIAMOND_SWORD), ceItem);
            assertSame(ceItem, data.getCeItem());
        }
    }

    @Nested
    @DisplayName("Add Item Tests")
    class AddItemTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("addItem with CEWeapon sets itemData1 and returns SUCCESS")
        void addWeaponSetsSlot1() {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            CEWeapon ceWeapon = mock(CEWeapon.class);
            var result = ctx.menu.addItem(item, ceWeapon);
            assertEquals(CEAnvilExtraData.CEAnvilAddReason.SUCCESS, result);
            assertNotNull(ctx.extraData.getItemData1());
            assertEquals(Material.DIAMOND_SWORD, ctx.extraData.getItemData1().getItemStack().getType());
        }

        @Test
        @DisplayName("addItem with CEWeapon creates DefaultHandler when no handler exists")
        void addWeaponCreatesDefaultHandler() {
            assertNull(ctx.extraData.getActiveHandler());
            ctx.menu.addItem(new ItemStack(Material.DIAMOND_SWORD), mock(CEWeapon.class));
            assertNotNull(ctx.extraData.getActiveHandler());
            assertInstanceOf(DefaultHandler.class, ctx.extraData.getActiveHandler());
        }

        @Test
        @DisplayName("addItem CEWeapon when slot1 full returns ALREADY_HAS_SLOT1")
        void addWeaponWhenSlot1Full() {
            ctx.menu.addItem(new ItemStack(Material.DIAMOND_SWORD), mock(CEWeapon.class));
            var result = ctx.menu.addItem(new ItemStack(Material.IRON_SWORD), mock(CEWeapon.class));
            assertEquals(CEAnvilExtraData.CEAnvilAddReason.ALREADY_HAS_SLOT1, result);
        }

        @Test
        @DisplayName("addItem unregistered type returns NOT_SUITABLE")
        void addUnregisteredTypeReturnsNotSuitable() {
            var result = ctx.menu.addItem(new ItemStack(Material.STONE), mock(CEItem.class));
            assertEquals(CEAnvilExtraData.CEAnvilAddReason.NOT_SUITABLE, result);
        }

        @Test
        @DisplayName("addItem slot2 with registered handler returns SUCCESS")
        void addSlot2WithRegisteredHandler() {
            CEAnvilCustomMenu.registerHandler("test-add-s2", () -> {
                Slot2Handler h = mock(Slot2Handler.class);
                when(h.isSuitable(any())).thenReturn(true);
                return h;
            });
            try {
                var result = ctx.menu.addItem(new ItemStack(Material.ENCHANTED_BOOK), mock(CEItem.class));
                assertEquals(CEAnvilExtraData.CEAnvilAddReason.SUCCESS, result);
                assertNotNull(ctx.extraData.getItemData2());
            } finally {
                CEAnvilCustomMenu.registerHandler("test-add-s2", () -> {
                    Slot2Handler h = mock(Slot2Handler.class);
                    when(h.isSuitable(any())).thenReturn(false);
                    return h;
                });
            }
        }

        @Test
        @DisplayName("addItem slot2 when slot2 full returns ALREADY_HAS_SLOT2")
        void addSlot2WhenFull() {
            CEAnvilCustomMenu.registerHandler("test-add-s2f", () -> {
                Slot2Handler h = mock(Slot2Handler.class);
                when(h.isSuitable(any())).thenReturn(true);
                return h;
            });
            try {
                ctx.menu.addItem(new ItemStack(Material.ENCHANTED_BOOK), mock(CEItem.class));
                var result = ctx.menu.addItem(new ItemStack(Material.ENCHANTED_BOOK), mock(CEItem.class));
                assertEquals(CEAnvilExtraData.CEAnvilAddReason.ALREADY_HAS_SLOT2, result);
            } finally {
                CEAnvilCustomMenu.registerHandler("test-add-s2f", () -> {
                    Slot2Handler h = mock(Slot2Handler.class);
                    when(h.isSuitable(any())).thenReturn(false);
                    return h;
                });
            }
        }

        @Test
        @DisplayName("addItem slot2 calls clearPreviews on old handler")
        void addSlot2ClearsPreviousHandler() {
            Slot2Handler oldHandler = mock(Slot2Handler.class);
            ctx.extraData.setActiveHandler(oldHandler);
            CEAnvilCustomMenu.registerHandler("test-add-clr", () -> {
                Slot2Handler h = mock(Slot2Handler.class);
                when(h.isSuitable(any())).thenReturn(true);
                return h;
            });
            try {
                ctx.menu.addItem(new ItemStack(Material.ENCHANTED_BOOK), mock(CEItem.class));
                verify(oldHandler).clearPreviews(ctx.menu);
            } finally {
                CEAnvilCustomMenu.registerHandler("test-add-clr", () -> {
                    Slot2Handler h = mock(Slot2Handler.class);
                    when(h.isSuitable(any())).thenReturn(false);
                    return h;
                });
            }
        }
    }

    @Nested
    @DisplayName("Return Item Tests")
    class ReturnItemTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("returnItem('slot1') clears itemData1")
        void returnSlot1ClearsItemData1() {
            ctx.menu.addItem(new ItemStack(Material.DIAMOND_SWORD), mock(CEWeapon.class));
            assertNotNull(ctx.extraData.getItemData1());
            ctx.menu.returnItem("slot1");
            assertNull(ctx.extraData.getItemData1());
        }

        @Test
        @DisplayName("returnItem('slot1') nulls handler when slot2 is empty")
        void returnSlot1NullsHandlerWhenSlot2Empty() {
            ctx.menu.addItem(new ItemStack(Material.DIAMOND_SWORD), mock(CEWeapon.class));
            assertNotNull(ctx.extraData.getActiveHandler());
            ctx.menu.returnItem("slot1");
            assertNull(ctx.extraData.getActiveHandler());
        }

        @Test
        @DisplayName("returnItem('slot1') preserves handler when slot2 exists")
        void returnSlot1PreservesHandlerWhenSlot2Exists() {
            ctx.menu.addItem(new ItemStack(Material.DIAMOND_SWORD), mock(CEWeapon.class));
            ctx.extraData.setActiveHandler(mock(Slot2Handler.class));
            ctx.extraData.setItemData2(new AnvilItemData(new ItemStack(Material.ENCHANTED_BOOK), mock(CEItem.class)));
            ctx.menu.returnItem("slot1");
            assertNull(ctx.extraData.getItemData1());
            assertNotNull(ctx.extraData.getActiveHandler());
        }

        @Test
        @DisplayName("returnItem('slot2') reverts to DefaultHandler when slot1 exists")
        void returnSlot2RevertsToDefaultHandler() {
            ctx.menu.addItem(new ItemStack(Material.DIAMOND_SWORD), mock(CEWeapon.class));
            ctx.extraData.setActiveHandler(mock(Slot2Handler.class));
            ctx.extraData.setItemData2(new AnvilItemData(new ItemStack(Material.ENCHANTED_BOOK), mock(CEItem.class)));
            ctx.menu.returnItem("slot2");
            assertNull(ctx.extraData.getItemData2());
            assertInstanceOf(DefaultHandler.class, ctx.extraData.getActiveHandler());
        }

        @Test
        @DisplayName("returnItem('slot2') nulls handler when slot1 is also empty")
        void returnSlot2NullsHandlerWhenSlot1Empty() {
            ctx.extraData.setActiveHandler(mock(Slot2Handler.class));
            ctx.extraData.setItemData2(new AnvilItemData(new ItemStack(Material.ENCHANTED_BOOK), mock(CEItem.class)));
            ctx.menu.returnItem("slot2");
            assertNull(ctx.extraData.getItemData2());
            assertNull(ctx.extraData.getActiveHandler());
        }

        @Test
        @DisplayName("returnItem with invalid name is a no-op")
        void returnItemInvalidNameIsNoop() {
            ctx.menu.addItem(new ItemStack(Material.DIAMOND_SWORD), mock(CEWeapon.class));
            AnvilItemData original = ctx.extraData.getItemData1();
            ctx.menu.returnItem("invalid");
            assertSame(original, ctx.extraData.getItemData1());
        }

        @Test
        @DisplayName("returnItem('slot2') calls clearPreviews on handler")
        void returnSlot2CallsClearPreviews() {
            Slot2Handler handler = mock(Slot2Handler.class);
            ctx.extraData.setActiveHandler(handler);
            ctx.extraData.setItemData2(new AnvilItemData(new ItemStack(Material.ENCHANTED_BOOK), mock(CEItem.class)));
            ctx.menu.returnItem("slot2");
            verify(handler).clearPreviews(ctx.menu);
        }
    }

    @Nested
    @DisplayName("Confirm Tests")
    class ConfirmTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("confirm with null slots is a no-op")
        void confirmWithNullSlotsIsNoop() {
            assertDoesNotThrow(() -> ctx.menu.confirm());
        }

        @Test
        @DisplayName("confirm with null slot2 is a no-op")
        void confirmWithNullSlot2IsNoop() {
            ctx.menu.addItem(new ItemStack(Material.DIAMOND_SWORD), mock(CEWeapon.class));
            assertDoesNotThrow(() -> ctx.menu.confirm());
        }

        @Test
        @DisplayName("confirm delegates to handler.apply()")
        void confirmDelegatesToHandler() {
            CEWeapon ceWeapon = mock(CEWeapon.class);
            CEItem ceItem2 = mock(CEItem.class);
            when(ceItem2.getType()).thenReturn("test");
            ctx.extraData.setItemData1(new AnvilItemData(new ItemStack(Material.DIAMOND_SWORD), ceWeapon));
            ctx.extraData.setItemData2(new AnvilItemData(new ItemStack(Material.ENCHANTED_BOOK), ceItem2));
            Slot2Handler handler = mock(Slot2Handler.class);
            when(handler.apply(ceWeapon, ceItem2)).thenReturn(ApplyReason.NOTHING);
            ctx.extraData.setActiveHandler(handler);
            ctx.menu.confirm();
            verify(handler).apply(ceWeapon, ceItem2);
        }

        @Test
        @DisplayName("confirm with NOTHING result does not decrement item2 amount")
        void confirmNothingNoDecrement() {
            CEWeapon ceWeapon = mock(CEWeapon.class);
            CEItem ceItem2 = mock(CEItem.class);
            when(ceItem2.getType()).thenReturn("test");
            ctx.extraData.setItemData1(new AnvilItemData(new ItemStack(Material.DIAMOND_SWORD), ceWeapon));
            ctx.extraData.setItemData2(new AnvilItemData(new ItemStack(Material.ENCHANTED_BOOK, 3), ceItem2));
            Slot2Handler handler = mock(Slot2Handler.class);
            when(handler.apply(ceWeapon, ceItem2)).thenReturn(ApplyReason.NOTHING);
            ctx.extraData.setActiveHandler(handler);
            ctx.menu.confirm();
            assertEquals(3, ctx.extraData.getItemData2().getItemStack().getAmount());
        }

        @Test
        @DisplayName("confirm with CANCEL result does not decrement item2 amount")
        void confirmCancelNoDecrement() {
            CEWeapon ceWeapon = mock(CEWeapon.class);
            CEItem ceItem2 = mock(CEItem.class);
            when(ceItem2.getType()).thenReturn("test");
            ctx.extraData.setItemData1(new AnvilItemData(new ItemStack(Material.DIAMOND_SWORD), ceWeapon));
            ctx.extraData.setItemData2(new AnvilItemData(new ItemStack(Material.ENCHANTED_BOOK, 3), ceItem2));
            Slot2Handler handler = mock(Slot2Handler.class);
            when(handler.apply(ceWeapon, ceItem2)).thenReturn(ApplyReason.CANCEL);
            ctx.extraData.setActiveHandler(handler);
            ctx.menu.confirm();
            assertEquals(3, ctx.extraData.getItemData2().getItemStack().getAmount());
        }

        @Test
        @DisplayName("confirm with DESTROY and amount=1 nulls itemData1")
        void confirmDestroyAmount1NullsItemData1() {
            CEWeapon ceWeapon = mock(CEWeapon.class);
            CEItem ceItem2 = mock(CEItem.class);
            when(ceItem2.getType()).thenReturn("test");
            ctx.extraData.setItemData1(new AnvilItemData(new ItemStack(Material.DIAMOND_SWORD, 1), ceWeapon));
            ctx.extraData.setItemData2(new AnvilItemData(new ItemStack(Material.ENCHANTED_BOOK, 2), ceItem2));
            Slot2Handler handler = mock(Slot2Handler.class);
            when(handler.apply(ceWeapon, ceItem2)).thenReturn(ApplyReason.DESTROY);
            ctx.extraData.setActiveHandler(handler);
            ctx.menu.confirm();
            assertNull(ctx.extraData.getItemData1());
        }
    }

    @Nested
    @DisplayName("Handle Close Tests")
    class HandleCloseTests {

        private MenuTestContext ctx;

        @BeforeEach
        void setUp() {
            assumeTrue(server != null, "MockBukkit not available");
            ctx = createMenuContext();
        }

        @Test
        @DisplayName("handleClose with no items does not throw")
        void handleCloseEmptyDoesNotThrow() {
            assertDoesNotThrow(() -> ctx.menu.handleClose());
        }

        @Test
        @DisplayName("handleClose with items set does not throw")
        void handleCloseWithItemsDoesNotThrow() {
            ctx.extraData.setItemData1(new AnvilItemData(new ItemStack(Material.DIAMOND_SWORD), mock(CEWeapon.class)));
            ctx.extraData.setItemData2(new AnvilItemData(new ItemStack(Material.ENCHANTED_BOOK), mock(CEItem.class)));
            assertDoesNotThrow(() -> ctx.menu.handleClose());
        }
    }
}
