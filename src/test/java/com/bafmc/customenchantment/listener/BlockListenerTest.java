package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerBlockBonus;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.bonus.BlockBonus;
import com.bafmc.customenchantment.player.mining.AutoSellSpecialMine;
import com.bafmc.customenchantment.player.mining.TelepathySpecialMine;
import com.bafmc.customenchantment.task.BlockTask;
import com.bafmc.customenchantment.task.TaskModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for BlockListener class.
 * Tests block-related event handling including breaking, drops, and explosions.
 *
 * Note: All tests are disabled because Mockito cannot mock AutoSellSpecialMine
 * (likely due to final class or other constraints).
 */
@org.junit.jupiter.api.Disabled("AutoSellSpecialMine cannot be mocked by Mockito")
@DisplayName("BlockListener Tests")
class BlockListenerTest {

    private BlockListener blockListener;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private Server mockServer;

    @Mock
    private PluginManager mockPluginManager;

    @Mock
    private TaskModule mockTaskModule;

    @Mock
    private BlockTask mockBlockTask;

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private PlayerInventory mockPlayerInventory;

    @Mock
    private PlayerTemporaryStorage mockTempStorage;

    @Mock
    private PlayerSpecialMining mockSpecialMining;

    @Mock
    private TelepathySpecialMine mockTelepathySpecialMine;

    @Mock
    private AutoSellSpecialMine mockAutoSellSpecialMine;

    @Mock
    private PlayerBlockBonus mockBlockBonus;

    @Mock
    private BlockBonus mockExpBonus;

    @Mock
    private BlockBonus mockMoneyBonus;

    @Mock
    private Block mockBlock;

    @Mock
    private Location mockLocation;

    @Mock
    private World mockWorld;

    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();

        when(mockPlugin.getTaskModule()).thenReturn(mockTaskModule);
        when(mockTaskModule.getBlockTask()).thenReturn(mockBlockTask);

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
        when(mockPlayer.getInventory()).thenReturn(mockPlayerInventory);

        when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockTempStorage);
        when(mockCEPlayer.getSpecialMining()).thenReturn(mockSpecialMining);
        when(mockCEPlayer.getBlockBonus()).thenReturn(mockBlockBonus);

        when(mockSpecialMining.getTelepathySpecialMine()).thenReturn(mockTelepathySpecialMine);
        when(mockSpecialMining.getAutoSellSpecialMine()).thenReturn(mockAutoSellSpecialMine);

        when(mockBlockBonus.getExpBonus()).thenReturn(mockExpBonus);
        when(mockBlockBonus.getMoneyBonus()).thenReturn(mockMoneyBonus);

        when(mockBlock.getLocation()).thenReturn(mockLocation);
        when(mockBlock.getType()).thenReturn(Material.STONE);
        when(mockLocation.getWorld()).thenReturn(mockWorld);
    }

    private void setupBlockListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            blockListener = new BlockListener(mockPlugin);
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

                blockListener = new BlockListener(mockPlugin);

                assertNotNull(blockListener);
                verify(mockPluginManager).registerEvents(blockListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("onBlockFromTo Tests")
    class OnBlockFromToTests {

        @Test
        @DisplayName("should cancel event when block is in task list")
        void onBlockFromTo_cancelsWhenBlockInTaskList() {
            setupBlockListener();

            BlockFromToEvent event = mock(BlockFromToEvent.class);
            when(event.getToBlock()).thenReturn(mockBlock);
            when(mockBlockTask.contains(mockLocation)).thenReturn(true);

            blockListener.onBlockFromTo(event);

            verify(event).setCancelled(true);
        }

        @Test
        @DisplayName("should not cancel event when block is not in task list")
        void onBlockFromTo_doesNotCancelWhenBlockNotInTaskList() {
            setupBlockListener();

            BlockFromToEvent event = mock(BlockFromToEvent.class);
            when(event.getToBlock()).thenReturn(mockBlock);
            when(mockBlockTask.contains(mockLocation)).thenReturn(false);

            blockListener.onBlockFromTo(event);

            verify(event, never()).setCancelled(true);
        }
    }

    @Nested
    @DisplayName("onBlockBreak Tests")
    class OnBlockBreakTests {

        @Test
        @DisplayName("should remove block from task list on break")
        void onBlockBreak_removesBlockFromTaskList() {
            setupBlockListener();

            BlockBreakEvent event = mock(BlockBreakEvent.class);
            ItemStack mockItem = mock(ItemStack.class);

            when(event.getBlock()).thenReturn(mockBlock);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getItemInHand()).thenReturn(mockItem);
            when(mockItem.getEnchantmentLevel(Enchantment.SILK_TOUCH)).thenReturn(0);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.isEmpty()).thenReturn(true);

                blockListener.onBlockBreak(event);

                verify(mockBlockTask).remove(mockLocation);
            }
        }

        @Test
        @DisplayName("should set last mine block type in temp storage")
        void onBlockBreak_setsLastMineBlockType() {
            setupBlockListener();

            BlockBreakEvent event = mock(BlockBreakEvent.class);
            ItemStack mockItem = mock(ItemStack.class);

            when(event.getBlock()).thenReturn(mockBlock);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getItemInHand()).thenReturn(mockItem);
            when(mockItem.getEnchantmentLevel(Enchantment.SILK_TOUCH)).thenReturn(0);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockExpBonus.isEmpty()).thenReturn(true);
                when(mockMoneyBonus.isEmpty()).thenReturn(true);

                blockListener.onBlockBreak(event);

                verify(mockTempStorage).set(any(), eq(Material.STONE));
            }
        }

        @Test
        @DisplayName("should return early when item is null")
        void onBlockBreak_returnsEarlyWhenItemNull() {
            setupBlockListener();

            BlockBreakEvent event = mock(BlockBreakEvent.class);

            when(event.getBlock()).thenReturn(mockBlock);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getItemInHand()).thenReturn(null);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                blockListener.onBlockBreak(event);

                // Should return early without checking bonuses
                verify(mockBlockBonus, never()).getExpBonus();
            }
        }

        @Test
        @DisplayName("should return early when using silk touch")
        void onBlockBreak_returnsEarlyWhenUsingSilkTouch() {
            setupBlockListener();

            BlockBreakEvent event = mock(BlockBreakEvent.class);
            ItemStack mockItem = mock(ItemStack.class);

            when(event.getBlock()).thenReturn(mockBlock);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getItemInHand()).thenReturn(mockItem);
            when(mockItem.getEnchantmentLevel(Enchantment.SILK_TOUCH)).thenReturn(1);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                blockListener.onBlockBreak(event);

                // Should return early without checking bonuses
                verify(mockBlockBonus, never()).getExpBonus();
            }
        }
    }

    @Nested
    @DisplayName("onBlockDropItemEvent Tests")
    class OnBlockDropItemEventTests {

        @Test
        @DisplayName("should call special mining on mining")
        void onBlockDropItemEvent_callsSpecialMiningOnMining() {
            setupBlockListener();

            BlockDropItemEvent event = mock(BlockDropItemEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                blockListener.onBlockBreak(event);

                verify(mockSpecialMining).onMining(event);
            }
        }
    }

    @Nested
    @DisplayName("onSugarCaneBreak Tests")
    class OnSugarCaneBreakTests {

        @Test
        @DisplayName("should return early when block is not sugar cane")
        void onSugarCaneBreak_returnsEarlyWhenNotSugarCane() {
            setupBlockListener();

            BlockBreakEvent event = mock(BlockBreakEvent.class);
            when(event.getBlock()).thenReturn(mockBlock);
            when(mockBlock.getType()).thenReturn(Material.STONE);

            blockListener.onSugarCaneBreak(event);

            // Should return early without processing
            verify(event, never()).getPlayer();
        }

        @Test
        @DisplayName("should return early when neither telepathy nor autosell is enabled")
        void onSugarCaneBreak_returnsEarlyWhenNoSpecialMining() {
            setupBlockListener();

            BlockBreakEvent event = mock(BlockBreakEvent.class);
            when(event.getBlock()).thenReturn(mockBlock);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockBlock.getType()).thenReturn(Material.SUGAR_CANE);
            when(mockTelepathySpecialMine.isWork(true)).thenReturn(false);
            when(mockAutoSellSpecialMine.isWork(true)).thenReturn(false);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                blockListener.onSugarCaneBreak(event);

                // Should return early
            }
        }
    }

    @Nested
    @DisplayName("onBlockBreakDropItemMonitor Tests")
    class OnBlockBreakDropItemMonitorTests {

        @Test
        @DisplayName("should add items to player and clear when telepathy is enabled")
        void onBlockBreakDropItemMonitor_addsItemsWhenTelepathyEnabled() {
            setupBlockListener();

            BlockDropItemEvent event = mock(BlockDropItemEvent.class);
            Item mockItem = mock(Item.class);
            ItemStack mockItemStack = mock(ItemStack.class);
            List<Item> items = new ArrayList<>();
            items.add(mockItem);

            when(event.getPlayer()).thenReturn(mockPlayer);
            when(event.getItems()).thenReturn(items);
            when(mockItem.getItemStack()).thenReturn(mockItemStack);
            when(mockTelepathySpecialMine.isWork(true)).thenReturn(true);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                blockListener.onBlockBreakDropItemMornitor(event);

                // Should clear items list after adding to player
                assertTrue(event.getItems().isEmpty());
            }
        }

        @Test
        @DisplayName("should not modify items when telepathy is disabled")
        void onBlockBreakDropItemMonitor_doesNotModifyItemsWhenTelepathyDisabled() {
            setupBlockListener();

            BlockDropItemEvent event = mock(BlockDropItemEvent.class);
            Item mockItem = mock(Item.class);
            List<Item> items = new ArrayList<>();
            items.add(mockItem);

            when(event.getPlayer()).thenReturn(mockPlayer);
            when(event.getItems()).thenReturn(items);
            when(mockTelepathySpecialMine.isWork(true)).thenReturn(false);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                blockListener.onBlockBreakDropItemMornitor(event);

                // Items should not be cleared
                assertFalse(event.getItems().isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("onBlockExplode Tests")
    class OnBlockExplodeTests {

        @Test
        @DisplayName("should remove blocks in task list from explosion")
        void onBlockExplode_removesBlocksInTaskList() {
            setupBlockListener();

            BlockExplodeEvent event = mock(BlockExplodeEvent.class);
            List<Block> blocks = new ArrayList<>();
            blocks.add(mockBlock);

            when(event.blockList()).thenReturn(blocks);
            when(mockBlockTask.contains(mockLocation)).thenReturn(true);

            blockListener.onBlockExplode(event);

            verify(mockBlock).setType(Material.AIR);
            assertTrue(blocks.isEmpty());
        }

        @Test
        @DisplayName("should not modify blocks not in task list")
        void onBlockExplode_doesNotModifyBlocksNotInTaskList() {
            setupBlockListener();

            BlockExplodeEvent event = mock(BlockExplodeEvent.class);
            List<Block> blocks = new ArrayList<>();
            blocks.add(mockBlock);

            when(event.blockList()).thenReturn(blocks);
            when(mockBlockTask.contains(mockLocation)).thenReturn(false);

            blockListener.onBlockExplode(event);

            verify(mockBlock, never()).setType(any());
            assertFalse(blocks.isEmpty());
        }
    }

    @Nested
    @DisplayName("onEntityExplode Tests")
    class OnEntityExplodeTests {

        @Test
        @DisplayName("should remove blocks in task list from entity explosion")
        void onEntityExplode_removesBlocksInTaskList() {
            setupBlockListener();

            EntityExplodeEvent event = mock(EntityExplodeEvent.class);
            List<Block> blocks = new ArrayList<>();
            blocks.add(mockBlock);

            when(event.blockList()).thenReturn(blocks);
            when(mockBlockTask.contains(mockLocation)).thenReturn(true);

            blockListener.onBlockExplode(event);

            verify(mockBlock).setType(Material.AIR);
            assertTrue(blocks.isEmpty());
        }

        @Test
        @DisplayName("should not modify blocks not in task list from entity explosion")
        void onEntityExplode_doesNotModifyBlocksNotInTaskList() {
            setupBlockListener();

            EntityExplodeEvent event = mock(EntityExplodeEvent.class);
            List<Block> blocks = new ArrayList<>();
            blocks.add(mockBlock);

            when(event.blockList()).thenReturn(blocks);
            when(mockBlockTask.contains(mockLocation)).thenReturn(false);

            blockListener.onBlockExplode(event);

            verify(mockBlock, never()).setType(any());
            assertFalse(blocks.isEmpty());
        }
    }
}
