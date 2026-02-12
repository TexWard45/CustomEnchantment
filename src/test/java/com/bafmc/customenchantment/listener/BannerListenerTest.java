package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for BannerListener class.
 * Tests banner and dragon head helmet interactions.
 *
 * Note: Many tests in this class are disabled due to Bukkit's InventoryType
 * static initialization which tries to load Minecraft registries that are
 * not available in the test environment.
 */
@DisplayName("BannerListener Tests")
class BannerListenerTest {

    private BannerListener bannerListener;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private Server mockServer;

    @Mock
    private PluginManager mockPluginManager;

    @Mock
    private Player mockPlayer;

    @Mock
    private PlayerInventory mockPlayerInventory;

    @Mock
    private Inventory mockClickedInventory;

    @Mock
    private InventoryView mockInventoryView;

    @Mock
    private Inventory mockTopInventory;

    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
        when(mockPlayer.getInventory()).thenReturn(mockPlayerInventory);
        when(mockPlayer.getOpenInventory()).thenReturn(mockInventoryView);
        when(mockInventoryView.getTopInventory()).thenReturn(mockTopInventory);
    }

    private void setupBannerListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            bannerListener = new BannerListener(mockPlugin);
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

                bannerListener = new BannerListener(mockPlugin);

                assertNotNull(bannerListener);
                verify(mockPluginManager).registerEvents(bannerListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("onInventoryClick Tests - Null Inventory")
    class NullInventoryTests {

        @Test
        @DisplayName("should return early when clicked inventory is null")
        void onInventoryClick_returnsEarlyWhenClickedInventoryNull() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getClickedInventory()).thenReturn(null);

            bannerListener.onInventoryClick(event);

            verify(event, never()).getSlotType();
        }
    }

    @Nested
    @org.junit.jupiter.api.Disabled("InventoryType static initialization requires Minecraft registries")
    @DisplayName("onInventoryClick Tests - Non-Player Inventory")
    class NonPlayerInventoryTests {

        @Test
        @DisplayName("should return early when clicked inventory is not player inventory")
        void onInventoryClick_returnsEarlyWhenNotPlayerInventory() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getClickedInventory()).thenReturn(mockClickedInventory);
            when(mockClickedInventory.getType()).thenReturn(InventoryType.CHEST);

            bannerListener.onInventoryClick(event);

            verify(event, never()).getSlotType();
        }
    }

    @Nested
    @org.junit.jupiter.api.Disabled("InventoryType static initialization requires Minecraft registries")
    @DisplayName("onInventoryClick Tests - Helmet Slot (Slot 5)")
    class HelmetSlotTests {

        @Test
        @DisplayName("should return early when cursor is null")
        void onInventoryClick_returnsEarlyWhenCursorNull() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getClickedInventory()).thenReturn(mockPlayerInventory);
            when(mockPlayerInventory.getType()).thenReturn(InventoryType.PLAYER);
            when(event.getRawSlot()).thenReturn(5);
            when(event.getSlotType()).thenReturn(SlotType.ARMOR);
            when(event.getCursor()).thenReturn(null);

            bannerListener.onInventoryClick(event);

            verify(mockPlayerInventory, never()).getHelmet();
        }

        @Test
        @DisplayName("should return early when cursor is not banner or dragon head")
        void onInventoryClick_returnsEarlyWhenCursorNotBannerOrDragonHead() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockCursor = mock(ItemStack.class);

            when(event.getClickedInventory()).thenReturn(mockPlayerInventory);
            when(mockPlayerInventory.getType()).thenReturn(InventoryType.PLAYER);
            when(event.getRawSlot()).thenReturn(5);
            when(event.getSlotType()).thenReturn(SlotType.ARMOR);
            when(event.getCursor()).thenReturn(mockCursor);
            when(mockCursor.getType()).thenReturn(Material.DIAMOND);

            bannerListener.onInventoryClick(event);

            verify(mockPlayerInventory, never()).getHelmet();
        }

        @Test
        @DisplayName("should swap helmet with banner when cursor is banner and helmet exists")
        void onInventoryClick_swapsHelmetWithBanner() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockCursor = mock(ItemStack.class);
            ItemStack mockHelmet = mock(ItemStack.class);

            when(event.getClickedInventory()).thenReturn(mockPlayerInventory);
            when(mockPlayerInventory.getType()).thenReturn(InventoryType.PLAYER);
            when(event.getRawSlot()).thenReturn(5);
            when(event.getSlotType()).thenReturn(SlotType.ARMOR);
            when(event.getCursor()).thenReturn(mockCursor);
            when(event.getClick()).thenReturn(ClickType.LEFT);
            when(mockCursor.getType()).thenReturn(Material.WHITE_BANNER);
            when(mockCursor.getAmount()).thenReturn(1);
            when(mockPlayerInventory.getHelmet()).thenReturn(mockHelmet);

            bannerListener.onInventoryClick(event);

            verify(mockPlayerInventory).setHelmet(mockCursor);
            verify(event).setCursor(mockHelmet);
            verify(event).setCancelled(true);
        }

        @Test
        @DisplayName("should handle dragon head as helmet")
        void onInventoryClick_handlesDragonHead() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockCursor = mock(ItemStack.class);
            ItemStack mockHelmet = mock(ItemStack.class);

            when(event.getClickedInventory()).thenReturn(mockPlayerInventory);
            when(mockPlayerInventory.getType()).thenReturn(InventoryType.PLAYER);
            when(event.getRawSlot()).thenReturn(5);
            when(event.getSlotType()).thenReturn(SlotType.ARMOR);
            when(event.getCursor()).thenReturn(mockCursor);
            when(event.getClick()).thenReturn(ClickType.LEFT);
            when(mockCursor.getType()).thenReturn(Material.DRAGON_HEAD);
            when(mockCursor.getAmount()).thenReturn(1);
            when(mockPlayerInventory.getHelmet()).thenReturn(mockHelmet);

            bannerListener.onInventoryClick(event);

            verify(mockPlayerInventory).setHelmet(mockCursor);
            verify(event).setCursor(mockHelmet);
            verify(event).setCancelled(true);
        }
    }

    @Nested
    @org.junit.jupiter.api.Disabled("InventoryType static initialization requires Minecraft registries")
    @DisplayName("onInventoryClick Tests - Shift Click Armor Slot 1")
    class ShiftClickArmorTests {

        @Test
        @DisplayName("should return early when current item is null")
        void onInventoryClick_returnsEarlyWhenCurrentItemNull() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);

            when(event.getClickedInventory()).thenReturn(mockPlayerInventory);
            when(mockPlayerInventory.getType()).thenReturn(InventoryType.PLAYER);
            when(event.getRawSlot()).thenReturn(1);
            when(event.getSlotType()).thenReturn(SlotType.ARMOR);
            when(event.getClick()).thenReturn(ClickType.SHIFT_LEFT);
            when(event.getCurrentItem()).thenReturn(null);

            bannerListener.onInventoryClick(event);

            verify(mockPlayerInventory, never()).setHelmet(any());
        }

        @Test
        @DisplayName("should return early when current item is not banner or dragon head")
        void onInventoryClick_returnsEarlyWhenNotBannerOrDragonHead() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockItem = mock(ItemStack.class);

            when(event.getClickedInventory()).thenReturn(mockPlayerInventory);
            when(mockPlayerInventory.getType()).thenReturn(InventoryType.PLAYER);
            when(event.getRawSlot()).thenReturn(1);
            when(event.getSlotType()).thenReturn(SlotType.ARMOR);
            when(event.getClick()).thenReturn(ClickType.SHIFT_LEFT);
            when(event.getCurrentItem()).thenReturn(mockItem);
            when(mockItem.getType()).thenReturn(Material.DIAMOND);

            bannerListener.onInventoryClick(event);

            verify(mockPlayerInventory, never()).setHelmet(any());
        }

        @Test
        @DisplayName("should return early when open inventory is not crafting")
        void onInventoryClick_returnsEarlyWhenOpenInventoryNotCrafting() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockItem = mock(ItemStack.class);

            when(event.getClickedInventory()).thenReturn(mockPlayerInventory);
            when(mockPlayerInventory.getType()).thenReturn(InventoryType.PLAYER);
            when(event.getRawSlot()).thenReturn(1);
            when(event.getSlotType()).thenReturn(SlotType.ARMOR);
            when(event.getClick()).thenReturn(ClickType.SHIFT_LEFT);
            when(event.getCurrentItem()).thenReturn(mockItem);
            when(mockItem.getType()).thenReturn(Material.WHITE_BANNER);
            when(event.getWhoClicked()).thenReturn(mockPlayer);
            when(mockTopInventory.getType()).thenReturn(InventoryType.CHEST);

            bannerListener.onInventoryClick(event);

            verify(mockPlayerInventory, never()).setHelmet(any());
        }

        @Test
        @DisplayName("should return early when player already has helmet")
        void onInventoryClick_returnsEarlyWhenAlreadyHasHelmet() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockItem = mock(ItemStack.class);
            ItemStack mockHelmet = mock(ItemStack.class);

            when(event.getClickedInventory()).thenReturn(mockPlayerInventory);
            when(mockPlayerInventory.getType()).thenReturn(InventoryType.PLAYER);
            when(event.getRawSlot()).thenReturn(1);
            when(event.getSlotType()).thenReturn(SlotType.ARMOR);
            when(event.getClick()).thenReturn(ClickType.SHIFT_LEFT);
            when(event.getCurrentItem()).thenReturn(mockItem);
            when(mockItem.getType()).thenReturn(Material.WHITE_BANNER);
            when(event.getWhoClicked()).thenReturn(mockPlayer);
            when(mockTopInventory.getType()).thenReturn(InventoryType.CRAFTING);
            when(mockPlayerInventory.getHelmet()).thenReturn(mockHelmet);

            bannerListener.onInventoryClick(event);

            verify(mockPlayerInventory, never()).setHelmet(any(ItemStack.class));
        }

        @Test
        @DisplayName("should set banner as helmet on shift click when no helmet")
        void onInventoryClick_setsBannerAsHelmetOnShiftClick() {
            setupBannerListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            ItemStack mockItem = mock(ItemStack.class);

            when(event.getClickedInventory()).thenReturn(mockPlayerInventory);
            when(mockPlayerInventory.getType()).thenReturn(InventoryType.PLAYER);
            when(event.getRawSlot()).thenReturn(1);
            when(event.getSlotType()).thenReturn(SlotType.ARMOR);
            when(event.getClick()).thenReturn(ClickType.SHIFT_LEFT);
            when(event.getCurrentItem()).thenReturn(mockItem);
            when(mockItem.getType()).thenReturn(Material.WHITE_BANNER);
            when(mockItem.getAmount()).thenReturn(2);
            when(event.getWhoClicked()).thenReturn(mockPlayer);
            when(mockTopInventory.getType()).thenReturn(InventoryType.CRAFTING);
            when(mockPlayerInventory.getHelmet()).thenReturn(null);

            bannerListener.onInventoryClick(event);

            verify(mockPlayerInventory).setHelmet(any(ItemStack.class));
            verify(event).setCancelled(true);
        }
    }
}
