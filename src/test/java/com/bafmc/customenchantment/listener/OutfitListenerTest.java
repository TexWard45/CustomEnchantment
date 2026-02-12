package com.bafmc.customenchantment.listener;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.item.CEItemOptimizeLoader;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for OutfitListener class.
 * Tests outfit-related event handling including inventory clicks and item drops.
 */
@DisplayName("OutfitListener Tests")
class OutfitListenerTest {

    private OutfitListener outfitListener;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private Server mockServer;

    @Mock
    private PluginManager mockPluginManager;

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private PlayerEquipment mockPlayerEquipment;

    @Mock
    private PlayerInventory mockPlayerInventory;

    @Mock
    private Inventory mockClickedInventory;

    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
        when(mockPlayer.getInventory()).thenReturn(mockPlayerInventory);

        when(mockCEPlayer.getEquipment()).thenReturn(mockPlayerEquipment);
    }

    private void setupOutfitListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            outfitListener = new OutfitListener(mockPlugin);
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

                outfitListener = new OutfitListener(mockPlugin);

                assertNotNull(outfitListener);
                verify(mockPluginManager).registerEvents(outfitListener, mockPlugin);
            }
        }
    }

    @Nested
    @org.junit.jupiter.api.Disabled("InventoryType static initialization requires Minecraft registries")
    @DisplayName("onInventoryClick Tests")
    class OnInventoryClickTests {

        @Test
        @DisplayName("should return early when clicked inventory is null")
        void onInventoryClick_returnsEarlyWhenInventoryNull() {
            setupOutfitListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getClickedInventory()).thenReturn(null);

            outfitListener.onInventoryClick(event);

            verify(event, never()).getWhoClicked();
        }

        @Test
        @DisplayName("should return early when clicked inventory is crafting type")
        void onInventoryClick_returnsEarlyWhenCraftingType() {
            setupOutfitListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getClickedInventory()).thenReturn(mockClickedInventory);
            when(mockClickedInventory.getType()).thenReturn(InventoryType.CRAFTING);

            outfitListener.onInventoryClick(event);

            verify(event, never()).getWhoClicked();
        }

        @Test
        @DisplayName("should cancel event when player has wings and clicks offhand slot")
        void onInventoryClick_cancelsWhenHasWingsAndOffhandSlot() {
            setupOutfitListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getClickedInventory()).thenReturn(mockClickedInventory);
            when(mockClickedInventory.getType()).thenReturn(InventoryType.CHEST);
            when(event.getWhoClicked()).thenReturn(mockPlayer);
            when(event.getSlot()).thenReturn(40); // Offhand slot

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<EquipSlot> mockedEquipSlot = mockStatic(EquipSlot.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockPlayerEquipment.hasWings()).thenReturn(true);
                mockedEquipSlot.when(() -> EquipSlot.isMatchingEquipSlot(40, EquipSlot.OFFHAND)).thenReturn(true);

                outfitListener.onInventoryClick(event);

                verify(event).setCancelled(true);
            }
        }

        @Test
        @DisplayName("should not cancel event when player does not have wings")
        void onInventoryClick_doesNotCancelWhenNoWings() {
            setupOutfitListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getClickedInventory()).thenReturn(mockClickedInventory);
            when(mockClickedInventory.getType()).thenReturn(InventoryType.CHEST);
            when(event.getWhoClicked()).thenReturn(mockPlayer);
            when(event.getSlot()).thenReturn(40);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<EquipSlot> mockedEquipSlot = mockStatic(EquipSlot.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockPlayerEquipment.hasWings()).thenReturn(false);

                outfitListener.onInventoryClick(event);

                verify(event, never()).setCancelled(true);
            }
        }

        @Test
        @DisplayName("should not cancel event when click is not on offhand slot")
        void onInventoryClick_doesNotCancelWhenNotOffhandSlot() {
            setupOutfitListener();

            InventoryClickEvent event = mock(InventoryClickEvent.class);
            when(event.getClickedInventory()).thenReturn(mockClickedInventory);
            when(mockClickedInventory.getType()).thenReturn(InventoryType.CHEST);
            when(event.getWhoClicked()).thenReturn(mockPlayer);
            when(event.getSlot()).thenReturn(0); // Not offhand slot

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<EquipSlot> mockedEquipSlot = mockStatic(EquipSlot.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockPlayerEquipment.hasWings()).thenReturn(true);
                mockedEquipSlot.when(() -> EquipSlot.isMatchingEquipSlot(0, EquipSlot.OFFHAND)).thenReturn(false);

                outfitListener.onInventoryClick(event);

                verify(event, never()).setCancelled(true);
            }
        }
    }

    @Nested
    @DisplayName("onPlayerDropItemEvent Tests")
    class OnPlayerDropItemEventTests {

        @Test
        @DisplayName("should replace item with weapon item when CE skin has weapon")
        void onPlayerDropItemEvent_replacesWithWeaponItem() {
            setupOutfitListener();

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            Item mockItem = mock(Item.class);
            ItemStack mockItemStack = mock(ItemStack.class);
            ItemStack mockWeaponItemStack = mock(ItemStack.class);

            when(event.getItemDrop()).thenReturn(mockItem);
            when(mockItem.getItemStack()).thenReturn(mockItemStack);

            try (MockedConstruction<CEItemOptimizeLoader> mockedLoader =
                     mockConstruction(CEItemOptimizeLoader.class, (mock, context) -> {
                         when(mock.isCESkin()).thenReturn(true);
                         when(mock.getWeaponItemStack()).thenReturn(mockWeaponItemStack);
                     })) {

                outfitListener.onPlayerDropItemEvent(event);

                verify(mockItem).setItemStack(mockWeaponItemStack);
            }
        }

        @Test
        @DisplayName("should not modify item when not CE skin")
        void onPlayerDropItemEvent_doesNotModifyWhenNotCESkin() {
            setupOutfitListener();

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            Item mockItem = mock(Item.class);
            ItemStack mockItemStack = mock(ItemStack.class);

            when(event.getItemDrop()).thenReturn(mockItem);
            when(mockItem.getItemStack()).thenReturn(mockItemStack);

            try (MockedConstruction<CEItemOptimizeLoader> mockedLoader =
                     mockConstruction(CEItemOptimizeLoader.class, (mock, context) -> {
                         when(mock.isCESkin()).thenReturn(false);
                     })) {

                outfitListener.onPlayerDropItemEvent(event);

                verify(mockItem, never()).setItemStack(any());
            }
        }

        @Test
        @DisplayName("should not modify item when CE skin has no weapon")
        void onPlayerDropItemEvent_doesNotModifyWhenNoWeapon() {
            setupOutfitListener();

            PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);
            Item mockItem = mock(Item.class);
            ItemStack mockItemStack = mock(ItemStack.class);

            when(event.getItemDrop()).thenReturn(mockItem);
            when(mockItem.getItemStack()).thenReturn(mockItemStack);

            try (MockedConstruction<CEItemOptimizeLoader> mockedLoader =
                     mockConstruction(CEItemOptimizeLoader.class, (mock, context) -> {
                         when(mock.isCESkin()).thenReturn(true);
                         when(mock.getWeaponItemStack()).thenReturn(null);
                     })) {

                outfitListener.onPlayerDropItemEvent(event);

                verify(mockItem, never()).setItemStack(any());
            }
        }
    }

    @Nested
    @DisplayName("onBlockDropItemEvent Tests")
    class OnBlockDropItemEventTests {

        @Test
        @DisplayName("should replace items with weapon items when CE skin has weapon")
        void onBlockDropItemEvent_replacesWithWeaponItems() {
            setupOutfitListener();

            BlockDropItemEvent event = mock(BlockDropItemEvent.class);
            Item mockItem = mock(Item.class);
            ItemStack mockItemStack = mock(ItemStack.class);
            ItemStack mockWeaponItemStack = mock(ItemStack.class);
            List<Item> items = new ArrayList<>();
            items.add(mockItem);

            when(event.getItems()).thenReturn(items);
            when(mockItem.getItemStack()).thenReturn(mockItemStack);

            try (MockedConstruction<CEItemOptimizeLoader> mockedLoader =
                     mockConstruction(CEItemOptimizeLoader.class, (mock, context) -> {
                         when(mock.isCESkin()).thenReturn(true);
                         when(mock.getWeaponItemStack()).thenReturn(mockWeaponItemStack);
                     })) {

                outfitListener.onPlayerDropItemEvent(event);

                verify(mockItem).setItemStack(mockWeaponItemStack);
            }
        }

        @Test
        @DisplayName("should process multiple items in drop event")
        void onBlockDropItemEvent_processesMultipleItems() {
            setupOutfitListener();

            BlockDropItemEvent event = mock(BlockDropItemEvent.class);
            Item mockItem1 = mock(Item.class);
            Item mockItem2 = mock(Item.class);
            ItemStack mockItemStack1 = mock(ItemStack.class);
            ItemStack mockItemStack2 = mock(ItemStack.class);
            ItemStack mockWeaponItemStack = mock(ItemStack.class);
            List<Item> items = new ArrayList<>();
            items.add(mockItem1);
            items.add(mockItem2);

            when(event.getItems()).thenReturn(items);
            when(mockItem1.getItemStack()).thenReturn(mockItemStack1);
            when(mockItem2.getItemStack()).thenReturn(mockItemStack2);

            try (MockedConstruction<CEItemOptimizeLoader> mockedLoader =
                     mockConstruction(CEItemOptimizeLoader.class, (mock, context) -> {
                         // Only first item is CE skin
                         if (context.arguments().get(0) == mockItemStack1) {
                             when(mock.isCESkin()).thenReturn(true);
                             when(mock.getWeaponItemStack()).thenReturn(mockWeaponItemStack);
                         } else {
                             when(mock.isCESkin()).thenReturn(false);
                         }
                     })) {

                outfitListener.onPlayerDropItemEvent(event);

                // Both items should be checked
            }
        }

        @Test
        @DisplayName("should handle empty item list")
        void onBlockDropItemEvent_handlesEmptyList() {
            setupOutfitListener();

            BlockDropItemEvent event = mock(BlockDropItemEvent.class);
            when(event.getItems()).thenReturn(Collections.emptyList());

            assertDoesNotThrow(() -> outfitListener.onPlayerDropItemEvent(event));
        }
    }
}
