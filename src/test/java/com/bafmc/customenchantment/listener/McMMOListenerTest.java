package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.player.mining.TelepathySpecialMine;
import com.gmail.nossr50.events.items.McMMOItemSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
 * Tests for McMMOListener class.
 * Tests mcMMO plugin integration for telepathy mechanics.
 */
@DisplayName("McMMOListener Tests")
class McMMOListenerTest {

    private McMMOListener mcMMOListener;

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
    private PlayerSpecialMining mockSpecialMining;

    @Mock
    private TelepathySpecialMine mockTelepathySpecialMine;

    @Mock
    private ItemStack mockItemStack;

    @Mock
    private org.bukkit.inventory.PlayerInventory mockPlayerInventory;

    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
        when(mockPlayer.getInventory()).thenReturn(mockPlayerInventory);

        when(mockCEPlayer.getSpecialMining()).thenReturn(mockSpecialMining);
        when(mockSpecialMining.getTelepathySpecialMine()).thenReturn(mockTelepathySpecialMine);
    }

    private void setupMcMMOListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            mcMMOListener = new McMMOListener(mockPlugin);
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

                mcMMOListener = new McMMOListener(mockPlugin);

                assertNotNull(mcMMOListener);
                verify(mockPluginManager).registerEvents(mcMMOListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("onMcMMO Tests")
    class OnMcMMOTests {

        @Test
        @DisplayName("should return early when player is null")
        void onMcMMO_returnsEarlyWhenPlayerNull() {
            setupMcMMOListener();

            McMMOItemSpawnEvent event = mock(McMMOItemSpawnEvent.class);
            when(event.getPlayer()).thenReturn(null);

            mcMMOListener.onMcMMO(event);

            verify(event, never()).setCancelled(anyBoolean());
        }

        @Test
        @DisplayName("should cancel event and add item when telepathy is enabled")
        void onMcMMO_cancelsAndAddsItemWhenTelepathyEnabled() {
            setupMcMMOListener();

            McMMOItemSpawnEvent event = mock(McMMOItemSpawnEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(event.getItemStack()).thenReturn(mockItemStack);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockTelepathySpecialMine.isWork(true)).thenReturn(true);

                mcMMOListener.onMcMMO(event);

                verify(event).setCancelled(true);
            }
        }

        @Test
        @DisplayName("should not cancel event when telepathy is disabled")
        void onMcMMO_doesNotCancelWhenTelepathyDisabled() {
            setupMcMMOListener();

            McMMOItemSpawnEvent event = mock(McMMOItemSpawnEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockTelepathySpecialMine.isWork(true)).thenReturn(false);

                mcMMOListener.onMcMMO(event);

                verify(event, never()).setCancelled(anyBoolean());
            }
        }

        @Test
        @DisplayName("should get item stack from event when telepathy is enabled")
        void onMcMMO_getsItemStackWhenTelepathyEnabled() {
            setupMcMMOListener();

            McMMOItemSpawnEvent event = mock(McMMOItemSpawnEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(event.getItemStack()).thenReturn(mockItemStack);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockTelepathySpecialMine.isWork(true)).thenReturn(true);

                mcMMOListener.onMcMMO(event);

                verify(event).getItemStack();
            }
        }

        @Test
        @DisplayName("should not get item stack when telepathy is disabled")
        void onMcMMO_doesNotGetItemStackWhenTelepathyDisabled() {
            setupMcMMOListener();

            McMMOItemSpawnEvent event = mock(McMMOItemSpawnEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                when(mockTelepathySpecialMine.isWork(true)).thenReturn(false);

                mcMMOListener.onMcMMO(event);

                verify(event, never()).getItemStack();
            }
        }
    }
}
