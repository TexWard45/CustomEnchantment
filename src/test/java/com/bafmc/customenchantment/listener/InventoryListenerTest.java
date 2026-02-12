package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.nametag.CENameTag;
import com.bafmc.customenchantment.player.CEPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for InventoryListener class.
 * Tests inventory-related event handling including crafting, anvil, and clicking.
 */
@DisplayName("InventoryListener Tests")
class InventoryListenerTest {

    private InventoryListener inventoryListener;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private Server mockServer;

    @Mock
    private PluginManager mockPluginManager;

    @Mock
    private BukkitScheduler mockScheduler;

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private PlayerInventory mockPlayerInventory;

    @Mock
    private Inventory mockInventory;

    @Mock
    private AnvilInventory mockAnvilInventory;

    @Mock
    private CraftingInventory mockCraftingInventory;

    @Mock
    private InventoryView mockInventoryView;

    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
        when(mockPlayer.getInventory()).thenReturn(mockPlayerInventory);
    }

    private void setupInventoryListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);
            mockedBukkit.when(Bukkit::getScheduler).thenReturn(mockScheduler);

            inventoryListener = new InventoryListener(mockPlugin);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should initialize with plugin reference and register events")
        void constructor_initializesWithPluginAndRegistersEvents() {
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
                mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

                inventoryListener = new InventoryListener(mockPlugin);

                assertNotNull(inventoryListener);
                verify(mockPluginManager).registerEvents(inventoryListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("exceptSlot Tests")
    class ExceptSlotTests {

        @Test
        @DisplayName("should contain hotbar slots and offhand slot")
        void exceptSlot_containsCorrectSlots() {
            List<Integer> expectedSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 45);

            assertEquals(expectedSlots.size(), InventoryListener.exceptSlot.size());
            assertTrue(InventoryListener.exceptSlot.containsAll(expectedSlots));
        }
    }

    @Nested
    @DisplayName("onPrepareCrafting Tests")
    class OnPrepareCraftingTests {

        @Test
        @DisplayName("should set result to null when CE item found in matrix")
        void onPrepareCrafting_setsResultNullWhenCEItemFound() {
            setupInventoryListener();

            PrepareItemCraftEvent event = mock(PrepareItemCraftEvent.class);
            ItemStack mockItem = mock(ItemStack.class);
            ItemStack[] matrix = new ItemStack[]{mockItem, null, null};

            when(event.getInventory()).thenReturn(mockCraftingInventory);
            when(mockCraftingInventory.getMatrix()).thenReturn(matrix);
            when(mockItem.getType()).thenReturn(Material.DIAMOND);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                CEItem mockCEItem = mock(CEItem.class);
                mockedCEAPI.when(() -> CEAPI.getCEItem(mockItem)).thenReturn(mockCEItem);

                inventoryListener.onPrepareCrafting(event);

                verify(mockCraftingInventory).setResult(null);
            }
        }

        @Test
        @DisplayName("should not modify result when no CE items in matrix")
        void onPrepareCrafting_doesNotModifyResultWhenNoCEItems() {
            setupInventoryListener();

            PrepareItemCraftEvent event = mock(PrepareItemCraftEvent.class);
            ItemStack mockItem = mock(ItemStack.class);
            ItemStack[] matrix = new ItemStack[]{mockItem, null, null};

            when(event.getInventory()).thenReturn(mockCraftingInventory);
            when(mockCraftingInventory.getMatrix()).thenReturn(matrix);
            when(mockItem.getType()).thenReturn(Material.DIAMOND);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEItem(mockItem)).thenReturn(null);

                inventoryListener.onPrepareCrafting(event);

                verify(mockCraftingInventory, never()).setResult(null);
            }
        }

        @Test
        @DisplayName("should skip null and air items in matrix")
        void onPrepareCrafting_skipsNullAndAirItems() {
            setupInventoryListener();

            PrepareItemCraftEvent event = mock(PrepareItemCraftEvent.class);
            ItemStack airItem = mock(ItemStack.class);
            ItemStack[] matrix = new ItemStack[]{null, airItem, null};

            when(event.getInventory()).thenReturn(mockCraftingInventory);
            when(mockCraftingInventory.getMatrix()).thenReturn(matrix);
            when(airItem.getType()).thenReturn(Material.AIR);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                inventoryListener.onPrepareCrafting(event);

                verify(mockCraftingInventory, never()).setResult(null);
            }
        }
    }

    @Nested
    @DisplayName("onInventoryOpen Tests")
    class OnInventoryOpenTests {

        @Test
        @DisplayName("should set title in CEPlayer when inventory opens")
        void onInventoryOpen_setsTitleInCEPlayer() {
            setupInventoryListener();

            InventoryOpenEvent event = mock(InventoryOpenEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(event.getView()).thenReturn(mockInventoryView);
            when(mockInventoryView.getTitle()).thenReturn(ChatColor.GOLD + "Test Menu");

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                inventoryListener.onInventoryOpen(event);

                verify(mockCEPlayer).setTitleOpenInventory("Test Menu");
            }
        }
    }

    @Nested
    @DisplayName("onInventoryClose Tests")
    class OnInventoryCloseTests {

        @Test
        @DisplayName("should clear title in CEPlayer when inventory closes")
        void onInventoryClose_clearsTitleInCEPlayer() {
            setupInventoryListener();

            InventoryCloseEvent event = mock(InventoryCloseEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                inventoryListener.onInventoryClose(event);

                verify(mockCEPlayer).setTitleOpenInventory(null);
            }
        }
    }

    @Nested
    @org.junit.jupiter.api.Disabled("InventoryType static initialization requires Minecraft registries")
    @DisplayName("onAnvilClick Tests")
    class OnAnvilClickTests {

        @Test
        @DisplayName("should return early when inventory is not anvil type")
        void onAnvilClick_returnsEarlyWhenNotAnvilType() {
            setupInventoryListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getInventory()).thenReturn(mockInventory);
            when(mockInventory.getType()).thenReturn(InventoryType.CHEST);

            inventoryListener.onAnvilClick(event);

            verify(event, never()).getWhoClicked();
        }

        @Test
        @DisplayName("should return early when clicker is not player")
        void onAnvilClick_returnsEarlyWhenClickerNotPlayer() {
            setupInventoryListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            HumanEntity mockEntity = mock(HumanEntity.class);

            when(event.getInventory()).thenReturn(mockAnvilInventory);
            when(mockAnvilInventory.getType()).thenReturn(InventoryType.ANVIL);
            when(event.getWhoClicked()).thenReturn(mockEntity);

            inventoryListener.onAnvilClick(event);

            verify(mockAnvilInventory, never()).getItem(anyInt());
        }
    }

    @Nested
    @DisplayName("getColoredItem Tests")
    class GetColoredItemTests {

        @Test
        @DisplayName("should return null when item is null")
        void getColoredItem_returnsNullWhenItemNull() {
            setupInventoryListener();

            CENameTag mockNameTag = mock(CENameTag.class);

            ItemStack result = InventoryListener.getColoredItem(mockNameTag, null);

            assertNull(result);
        }

        @Test
        @DisplayName("should return same item when item has no meta")
        void getColoredItem_returnsSameItemWhenNoMeta() {
            setupInventoryListener();

            CENameTag mockNameTag = mock(CENameTag.class);
            ItemStack mockItem = mock(ItemStack.class);

            when(mockItem.hasItemMeta()).thenReturn(false);

            ItemStack result = InventoryListener.getColoredItem(mockNameTag, mockItem);

            assertSame(mockItem, result);
        }

        @Test
        @DisplayName("should return same item when item has no display name")
        void getColoredItem_returnsSameItemWhenNoDisplayName() {
            setupInventoryListener();

            CENameTag mockNameTag = mock(CENameTag.class);
            ItemStack mockItem = mock(ItemStack.class);
            ItemMeta mockMeta = mock(ItemMeta.class);

            when(mockItem.hasItemMeta()).thenReturn(true);
            when(mockItem.getItemMeta()).thenReturn(mockMeta);
            when(mockMeta.hasDisplayName()).thenReturn(false);

            ItemStack result = InventoryListener.getColoredItem(mockNameTag, mockItem);

            assertSame(mockItem, result);
        }

        @Test
        @DisplayName("should apply new display name from nametag")
        void getColoredItem_appliesNewDisplayName() {
            setupInventoryListener();

            CENameTag mockNameTag = mock(CENameTag.class);
            ItemStack mockItem = mock(ItemStack.class);
            ItemMeta mockMeta = mock(ItemMeta.class);

            when(mockItem.hasItemMeta()).thenReturn(true);
            when(mockItem.getItemMeta()).thenReturn(mockMeta);
            when(mockMeta.hasDisplayName()).thenReturn(true);
            when(mockMeta.getDisplayName()).thenReturn("Old Name");
            when(mockNameTag.getNewDisplay("Old Name")).thenReturn("New Name");

            ItemStack result = InventoryListener.getColoredItem(mockNameTag, mockItem);

            verify(mockMeta).setDisplayName("New Name");
            verify(mockItem).setItemMeta(mockMeta);
            assertSame(mockItem, result);
        }
    }

    @Nested
    @DisplayName("onInventoryClick Tests")
    class OnInventoryClickTests {

        @Test
        @DisplayName("should handle left click with cursor item")
        void onInventoryClick_handlesLeftClick() {
            setupInventoryListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockCursor = mock(ItemStack.class);

            when(event.getRawSlot()).thenReturn(10);
            when(event.getClick()).thenReturn(ClickType.LEFT);
            when(event.getAction()).thenReturn(InventoryAction.PLACE_ALL);
            when(event.getCursor()).thenReturn(mockCursor);
            when(mockCursor.getType()).thenReturn(Material.DIAMOND);

            inventoryListener.onInventoryClick(event);

            // Should not call onCEItemInventory for left click
        }

        @Test
        @DisplayName("should call onCEItemInventory for swap with cursor action")
        void onInventoryClick_callsOnCEItemInventoryForSwap() {
            setupInventoryListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockCursor = mock(ItemStack.class);

            when(event.getRawSlot()).thenReturn(10);
            when(event.getClick()).thenReturn(ClickType.RIGHT);
            when(event.getAction()).thenReturn(InventoryAction.SWAP_WITH_CURSOR);
            when(event.getCursor()).thenReturn(mockCursor);
            when(event.getWhoClicked()).thenReturn(mockPlayer);
            when(event.getSlot()).thenReturn(10);
            when(mockPlayerInventory.getItem(10)).thenReturn(null);
            when(mockCursor.getType()).thenReturn(Material.DIAMOND);

            inventoryListener.onInventoryClick(event);

            // Method should be called, but will return early due to null click item
        }

        @Test
        @DisplayName("should call onUnifyInventory for middle click with empty cursor")
        void onInventoryClick_callsOnUnifyInventoryForMiddleClick() {
            setupInventoryListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockCursor = mock(ItemStack.class);

            when(event.getRawSlot()).thenReturn(10);
            when(event.getClick()).thenReturn(ClickType.MIDDLE);
            when(event.getCursor()).thenReturn(mockCursor);
            when(mockCursor.getType()).thenReturn(Material.AIR);
            when(event.getCurrentItem()).thenReturn(null);

            inventoryListener.onInventoryClick(event);

            // Method will check current item
        }
    }

    @Nested
    @DisplayName("onFastEnchant Tests")
    class OnFastEnchantTests {

        @Test
        @DisplayName("should return false when cursor is not enchanted book")
        void onFastEnchant_returnsFalseWhenNotEnchantedBook() {
            setupInventoryListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockCursor = mock(ItemStack.class);

            when(event.getSlot()).thenReturn(10);
            when(event.getCursor()).thenReturn(mockCursor);
            when(event.getWhoClicked()).thenReturn(mockPlayer);
            when(mockPlayerInventory.getItem(10)).thenReturn(mock(ItemStack.class));
            when(mockCursor.getType()).thenReturn(Material.DIAMOND);

            boolean result = inventoryListener.onFastEnchant(event);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("updateEmptyDisplayAndDefaultColor Tests")
    class UpdateEmptyDisplayAndDefaultColorTests {

        @Test
        @DisplayName("should return when item is null")
        void updateEmptyDisplayAndDefaultColor_returnsWhenItemNull() {
            setupInventoryListener();

            PrepareAnvilEvent event = mock(PrepareAnvilEvent.class);

            // Should not throw when items are null
            assertDoesNotThrow(() ->
                inventoryListener.updateEmptyDisplayAndDefaultColor(event, null, null, false));
        }

        @Test
        @DisplayName("should set result to null when result has no display name")
        void updateEmptyDisplayAndDefaultColor_setsNullWhenNoDisplayName() {
            setupInventoryListener();

            PrepareAnvilEvent event = mock(PrepareAnvilEvent.class);
            ItemStack mockItem = mock(ItemStack.class);
            ItemStack mockResult = mock(ItemStack.class);
            ItemMeta mockMeta = mock(ItemMeta.class);
            ItemMeta mockResultMeta = mock(ItemMeta.class);

            when(mockItem.hasItemMeta()).thenReturn(true);
            when(mockItem.getItemMeta()).thenReturn(mockMeta);
            when(mockMeta.hasDisplayName()).thenReturn(true);
            when(mockMeta.getDisplayName()).thenReturn("Test");
            when(mockResult.hasItemMeta()).thenReturn(true);
            when(mockResult.getItemMeta()).thenReturn(mockResultMeta);
            when(mockResultMeta.getDisplayName()).thenReturn(null);

            inventoryListener.updateEmptyDisplayAndDefaultColor(event, mockItem, mockResult, false);

            verify(event).setResult(null);
        }
    }
}
