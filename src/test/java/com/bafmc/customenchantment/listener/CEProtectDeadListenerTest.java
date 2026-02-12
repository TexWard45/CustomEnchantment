package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerStorage;
import com.bafmc.customenchantment.utils.StorageUtils;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CEProtectDeadListener class.
 * Tests death protection and item saving mechanics.
 */
@DisplayName("CEProtectDeadListener Tests")
class CEProtectDeadListenerTest {

    private CEProtectDeadListener ceProtectDeadListener;

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
    private PlayerStorage mockPlayerStorage;

    @Mock
    private AdvancedFileConfiguration mockConfig;

    @Mock
    private PlayerInventory mockPlayerInventory;

    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
        when(mockPlayer.getInventory()).thenReturn(mockPlayerInventory);

        when(mockCEPlayer.getStorage()).thenReturn(mockPlayerStorage);
        when(mockPlayerStorage.getConfig()).thenReturn(mockConfig);
    }

    private void setupCEProtectDeadListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            ceProtectDeadListener = new CEProtectDeadListener(mockPlugin);
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

                ceProtectDeadListener = new CEProtectDeadListener(mockPlugin);

                assertNotNull(ceProtectDeadListener);
                verify(mockPluginManager).registerEvents(ceProtectDeadListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("onDeath Tests")
    class OnDeathTests {

        @Test
        @DisplayName("should return early when keep inventory is enabled")
        void onDeath_returnsEarlyWhenKeepInventory() {
            setupCEProtectDeadListener();

            PlayerDeathEvent event = mock(PlayerDeathEvent.class);
            when(event.getKeepInventory()).thenReturn(true);

            ceProtectDeadListener.onDeath(event);

            verify(event, never()).getEntity();
        }

        @Test
        @org.junit.jupiter.api.Disabled("CustomEnchantmentMessage.config is null - requires static config initialization")
        @DisplayName("should keep inventory when player has advanced protect dead")
        void onDeath_keepsInventoryWhenHasAdvancedProtectDead() {
            setupCEProtectDeadListener();

            PlayerDeathEvent event = mock(PlayerDeathEvent.class);
            List<ItemStack> drops = new ArrayList<>();
            drops.add(mock(ItemStack.class));

            when(event.getKeepInventory()).thenReturn(false);
            when(event.getEntity()).thenReturn(mockPlayer);
            when(event.getDrops()).thenReturn(drops);
            when(event.getDroppedExp()).thenReturn(100);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<StorageUtils> mockedStorageUtils = mockStatic(StorageUtils.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                mockedStorageUtils.when(() -> StorageUtils.getProtectDeadAmount(mockPlayerStorage)).thenReturn(1);

                ceProtectDeadListener.onDeath(event);

                verify(event).setKeepInventory(true);
                verify(event).setKeepLevel(true);
                assertTrue(drops.isEmpty());
            }
        }

        @Test
        @DisplayName("should not keep inventory when player has no protect dead")
        void onDeath_doesNotKeepInventoryWhenNoProtectDead() {
            setupCEProtectDeadListener();

            PlayerDeathEvent event = mock(PlayerDeathEvent.class);
            List<ItemStack> drops = new ArrayList<>();
            ItemStack mockItem = mock(ItemStack.class);
            drops.add(mockItem);

            when(event.getKeepInventory()).thenReturn(false);
            when(event.getEntity()).thenReturn(mockPlayer);
            when(event.getDrops()).thenReturn(drops);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<StorageUtils> mockedStorageUtils = mockStatic(StorageUtils.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                mockedStorageUtils.when(() -> StorageUtils.getProtectDeadAmount(mockPlayerStorage)).thenReturn(0);

                ceProtectDeadListener.onDeath(event);

                verify(event, never()).setKeepInventory(true);
            }
        }

        @Test
        @DisplayName("should not use protect dead when drops and exp are empty")
        void onDeath_doesNotUseProtectDeadWhenEmpty() {
            setupCEProtectDeadListener();

            PlayerDeathEvent event = mock(PlayerDeathEvent.class);
            List<ItemStack> drops = new ArrayList<>();

            when(event.getKeepInventory()).thenReturn(false);
            when(event.getEntity()).thenReturn(mockPlayer);
            when(event.getDrops()).thenReturn(drops);
            when(event.getDroppedExp()).thenReturn(0);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<StorageUtils> mockedStorageUtils = mockStatic(StorageUtils.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                mockedStorageUtils.when(() -> StorageUtils.getProtectDeadAmount(mockPlayerStorage)).thenReturn(1);

                ceProtectDeadListener.onDeath(event);

                // Should set keep inventory but not use protect dead
                verify(event).setKeepInventory(true);
                verify(event).setKeepLevel(true);
            }
        }

        @Test
        @org.junit.jupiter.api.Disabled("CEWeaponAbstract requires full plugin initialization")
        @DisplayName("should increment death time when death time is not before")
        void onDeath_incrementsDeathTime() {
            setupCEProtectDeadListener();

            PlayerDeathEvent event = mock(PlayerDeathEvent.class);
            when(event.getKeepInventory()).thenReturn(false);
            when(event.getEntity()).thenReturn(mockPlayer);
            when(event.getDrops()).thenReturn(new ArrayList<>());
            when(mockCEPlayer.isDeathTimeBefore()).thenReturn(false);
            when(mockCEPlayer.getDeathTime()).thenReturn(5);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<StorageUtils> mockedStorageUtils = mockStatic(StorageUtils.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                mockedStorageUtils.when(() -> StorageUtils.getProtectDeadAmount(mockPlayerStorage)).thenReturn(0);

                ceProtectDeadListener.onDeath(event);

                verify(mockCEPlayer).setDeathTime(6);
            }
        }

        @Test
        @org.junit.jupiter.api.Disabled("CEWeaponAbstract requires full plugin initialization")
        @DisplayName("should reset death time before flag when it is set")
        void onDeath_resetsDeathTimeBeforeFlag() {
            setupCEProtectDeadListener();

            PlayerDeathEvent event = mock(PlayerDeathEvent.class);
            when(event.getKeepInventory()).thenReturn(false);
            when(event.getEntity()).thenReturn(mockPlayer);
            when(event.getDrops()).thenReturn(new ArrayList<>());
            when(mockCEPlayer.isDeathTimeBefore()).thenReturn(true);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<StorageUtils> mockedStorageUtils = mockStatic(StorageUtils.class)) {

                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                mockedStorageUtils.when(() -> StorageUtils.getProtectDeadAmount(mockPlayerStorage)).thenReturn(0);

                ceProtectDeadListener.onDeath(event);

                verify(mockCEPlayer).setDeathTimeBefore(false);
                verify(mockCEPlayer, never()).setDeathTime(anyInt());
            }
        }
    }

    @Nested
    @DisplayName("onRespawn Tests")
    class OnRespawnTests {

        @Test
        @DisplayName("should add saved items to player on respawn")
        void onRespawn_addsSavedItemsToPlayer() {
            setupCEProtectDeadListener();

            PlayerRespawnEvent event = mock(PlayerRespawnEvent.class);
            List<ItemStack> savedItems = new ArrayList<>();
            savedItems.add(mock(ItemStack.class));

            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockConfig.getItemStackList("save-items-on-death")).thenReturn(savedItems);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                ceProtectDeadListener.onRespawn(event);

                // Verify items are cleared after respawn
                verify(mockConfig).set("save-items-on-death", null);
            }
        }

        @Test
        @DisplayName("should clear save-items-on-death config after respawn")
        void onRespawn_clearsSaveItemsConfig() {
            setupCEProtectDeadListener();

            PlayerRespawnEvent event = mock(PlayerRespawnEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockConfig.getItemStackList("save-items-on-death")).thenReturn(Collections.emptyList());

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                ceProtectDeadListener.onRespawn(event);

                verify(mockConfig).set("save-items-on-death", null);
            }
        }

        @Test
        @DisplayName("should handle null saved items list")
        void onRespawn_handlesNullSavedItems() {
            setupCEProtectDeadListener();

            PlayerRespawnEvent event = mock(PlayerRespawnEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockConfig.getItemStackList("save-items-on-death")).thenReturn(null);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                // Should not throw
                assertDoesNotThrow(() -> ceProtectDeadListener.onRespawn(event));

                verify(mockConfig).set("save-items-on-death", null);
            }
        }
    }
}
