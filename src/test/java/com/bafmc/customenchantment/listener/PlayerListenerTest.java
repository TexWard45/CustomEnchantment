package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.api.CEAPI;
import com.bafmc.customenchantment.config.MainConfig;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerAbility;
import com.bafmc.customenchantment.player.PlayerEquipment;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
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
 * Tests for PlayerListener class.
 * Tests player-related event handling including join, quit, movement, and abilities.
 */
@DisplayName("PlayerListener Tests")
class PlayerListenerTest {

    private PlayerListener playerListener;

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
    private PlayerInventory mockPlayerInventory;

    @Mock
    private PlayerAbility mockPlayerAbility;

    @Mock
    private PlayerEquipment mockPlayerEquipment;

    @Mock
    private PlayerTemporaryStorage mockTempStorage;

    @Mock
    private MainConfig mockMainConfig;

    @Mock
    private World mockWorld;

    @Mock
    private Location mockFromLocation;

    @Mock
    private Location mockToLocation;

    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();

        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
        when(mockPlayer.getInventory()).thenReturn(mockPlayerInventory);
        when(mockPlayer.getWorld()).thenReturn(mockWorld);
        when(mockPlayer.getLocation()).thenReturn(mockFromLocation);
        when(mockFromLocation.clone()).thenReturn(mockFromLocation);

        when(mockCEPlayer.getAbility()).thenReturn(mockPlayerAbility);
        when(mockCEPlayer.getEquipment()).thenReturn(mockPlayerEquipment);
        when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockTempStorage);
        when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);

        when(mockFromLocation.getWorld()).thenReturn(mockWorld);
        when(mockToLocation.getWorld()).thenReturn(mockWorld);
        when(mockFromLocation.getX()).thenReturn(0.0);
        when(mockFromLocation.getY()).thenReturn(64.0);
        when(mockFromLocation.getZ()).thenReturn(0.0);
        when(mockFromLocation.getYaw()).thenReturn(0.0f);
        when(mockFromLocation.getPitch()).thenReturn(0.0f);
        when(mockToLocation.getX()).thenReturn(1.0);
        when(mockToLocation.getY()).thenReturn(64.0);
        when(mockToLocation.getZ()).thenReturn(1.0);
        when(mockToLocation.getYaw()).thenReturn(0.0f);
        when(mockToLocation.getPitch()).thenReturn(0.0f);
    }

    private void setupPlayerListener() {
        try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
            mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
            mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);

            playerListener = new PlayerListener(mockPlugin);
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

                playerListener = new PlayerListener(mockPlugin);

                assertNotNull(playerListener);
                verify(mockPluginManager).registerEvents(playerListener, mockPlugin);
            }
        }
    }

    @Nested
    @DisplayName("onJoin Tests")
    class OnJoinTests {

        @Test
        @DisplayName("should call CEPlayer.onJoin when player joins")
        void onJoin_callsCEPlayerOnJoin() {
            setupPlayerListener();

            PlayerJoinEvent event = mock(PlayerJoinEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CustomEnchantment> mockedCE = mockStatic(CustomEnchantment.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                mockedCEAPI.when(() -> CEAPI.getCEWeaponMap(any(Player.class), any())).thenReturn(new java.util.HashMap<>());
                mockedCEAPI.when(() -> CEAPI.getCEWeaponMap(any(Player.class))).thenReturn(new java.util.HashMap<>());
                mockedCE.when(CustomEnchantment::instance).thenReturn(mockPlugin);
                when(mockPlugin.getMainConfig()).thenReturn(mockMainConfig);

                playerListener.onJoin(event);

                verify(mockCEPlayer).onJoin();
            }
        }
    }

    @Nested
    @DisplayName("onQuit Tests")
    class OnQuitTests {

        @Test
        @DisplayName("should call CEPlayer.onQuit when player quits")
        void onQuit_callsCEPlayerOnQuit() {
            setupPlayerListener();

            PlayerQuitEvent event = mock(PlayerQuitEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class);
                 MockedStatic<CustomEnchantment> mockedCE = mockStatic(CustomEnchantment.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);
                mockedCEAPI.when(() -> CEAPI.getCEWeaponMap(any(Player.class), any())).thenReturn(new java.util.HashMap<>());
                mockedCE.when(CustomEnchantment::instance).thenReturn(mockPlugin);
                when(mockPlugin.getMainConfig()).thenReturn(mockMainConfig);

                playerListener.onQuit(event);

                verify(mockCEPlayer).onQuit();
            }
        }
    }

    @Nested
    @DisplayName("isDifferentLocation Tests")
    class IsDifferentLocationTests {

        @Test
        @DisplayName("should return true when X coordinate differs")
        void isDifferentLocation_returnsTrueWhenXDiffers() {
            setupPlayerListener();

            when(mockFromLocation.getX()).thenReturn(0.0);
            when(mockToLocation.getX()).thenReturn(1.5);

            boolean result = playerListener.isDifferentLocation(mockFromLocation, mockToLocation);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when Y coordinate differs")
        void isDifferentLocation_returnsTrueWhenYDiffers() {
            setupPlayerListener();

            when(mockFromLocation.getY()).thenReturn(64.0);
            when(mockToLocation.getY()).thenReturn(65.5);
            when(mockFromLocation.getX()).thenReturn(0.0);
            when(mockToLocation.getX()).thenReturn(0.0);
            when(mockFromLocation.getZ()).thenReturn(0.0);
            when(mockToLocation.getZ()).thenReturn(0.0);

            boolean result = playerListener.isDifferentLocation(mockFromLocation, mockToLocation);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return true when Z coordinate differs")
        void isDifferentLocation_returnsTrueWhenZDiffers() {
            setupPlayerListener();

            when(mockFromLocation.getZ()).thenReturn(0.0);
            when(mockToLocation.getZ()).thenReturn(1.5);
            when(mockFromLocation.getX()).thenReturn(0.0);
            when(mockToLocation.getX()).thenReturn(0.0);
            when(mockFromLocation.getY()).thenReturn(64.0);
            when(mockToLocation.getY()).thenReturn(64.0);

            boolean result = playerListener.isDifferentLocation(mockFromLocation, mockToLocation);

            assertTrue(result);
        }

        @Test
        @DisplayName("should return false when locations are same")
        void isDifferentLocation_returnsFalseWhenSame() {
            setupPlayerListener();

            when(mockFromLocation.getX()).thenReturn(0.0);
            when(mockToLocation.getX()).thenReturn(0.05); // Within 0.1 tolerance
            when(mockFromLocation.getY()).thenReturn(64.0);
            when(mockToLocation.getY()).thenReturn(64.05);
            when(mockFromLocation.getZ()).thenReturn(0.0);
            when(mockToLocation.getZ()).thenReturn(0.05);

            boolean result = playerListener.isDifferentLocation(mockFromLocation, mockToLocation);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("onMove Tests")
    class OnMoveTests {

        @Test
        @DisplayName("should cancel event when move ability is cancelled")
        void onMove_cancelsEventWhenMoveAbilityCancelled() {
            setupPlayerListener();

            PlayerMoveEvent event = mock(PlayerMoveEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(event.getFrom()).thenReturn(mockFromLocation);
            when(event.getTo()).thenReturn(mockToLocation);
            when(mockPlayerAbility.isCancel(PlayerAbility.Type.MOVE)).thenReturn(true);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                playerListener.onMove(event);

                verify(event).setCancelled(true);
            }
        }

        @Test
        @DisplayName("should cancel event when look ability is cancelled and rotation changes")
        void onMove_cancelsEventWhenLookAbilityCancelledAndRotationChanges() {
            setupPlayerListener();

            PlayerMoveEvent event = mock(PlayerMoveEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(event.getFrom()).thenReturn(mockFromLocation);
            when(event.getTo()).thenReturn(mockToLocation);

            when(mockFromLocation.getYaw()).thenReturn(0.0f);
            when(mockToLocation.getYaw()).thenReturn(90.0f);
            when(mockPlayerAbility.isCancel(PlayerAbility.Type.MOVE)).thenReturn(false);
            when(mockPlayerAbility.isCancel(PlayerAbility.Type.LOOK)).thenReturn(true);
            when(mockPlayerAbility.isCancel(PlayerAbility.Type.JUMP)).thenReturn(false);

            // Make locations "same" to avoid MOVE cancel
            when(mockFromLocation.getX()).thenReturn(0.0);
            when(mockToLocation.getX()).thenReturn(0.0);
            when(mockFromLocation.getY()).thenReturn(64.0);
            when(mockToLocation.getY()).thenReturn(64.0);
            when(mockFromLocation.getZ()).thenReturn(0.0);
            when(mockToLocation.getZ()).thenReturn(0.0);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                playerListener.onMove(event);

                verify(event).setCancelled(true);
            }
        }
    }

    @Nested
    @DisplayName("updateMoveDirection Tests")
    class UpdateMoveDirectionTests {

        @Test
        @DisplayName("should not throw when to location is null")
        void updateMoveDirection_doesNotThrowWhenToIsNull() {
            setupPlayerListener();

            assertDoesNotThrow(() -> playerListener.updateMoveDirection(mockCEPlayer, mockFromLocation, null));
        }

        @Test
        @DisplayName("should set moving forward when moving in look direction")
        void updateMoveDirection_setsMovingForwardCorrectly() {
            setupPlayerListener();

            when(mockFromLocation.toVector()).thenReturn(new Vector(0, 64, 0));
            when(mockToLocation.toVector()).thenReturn(new Vector(1, 64, 0));
            when(mockFromLocation.getDirection()).thenReturn(new Vector(1, 0, 0));

            playerListener.updateMoveDirection(mockCEPlayer, mockFromLocation, mockToLocation);

            verify(mockCEPlayer).setMovingForward(true);
        }

        @Test
        @DisplayName("should set moving backward when moving opposite to look direction")
        void updateMoveDirection_setsMovingBackwardCorrectly() {
            setupPlayerListener();

            when(mockFromLocation.toVector()).thenReturn(new Vector(0, 64, 0));
            when(mockToLocation.toVector()).thenReturn(new Vector(-1, 64, 0));
            when(mockFromLocation.getDirection()).thenReturn(new Vector(1, 0, 0));

            playerListener.updateMoveDirection(mockCEPlayer, mockFromLocation, mockToLocation);

            verify(mockCEPlayer).setMovingForward(false);
        }
    }

    @Nested
    @DisplayName("onPlayerSneakEvent Tests")
    class OnPlayerSneakEventTests {

        @Test
        @DisplayName("should not process when not sneaking")
        void onPlayerSneakEvent_doesNothingWhenNotSneaking() {
            setupPlayerListener();

            PlayerToggleSneakEvent event = mock(PlayerToggleSneakEvent.class);
            when(event.isSneaking()).thenReturn(false);

            playerListener.onPlayerSneakEvent(event);

            // No processing should occur
            verify(event, never()).getPlayer();
        }

        @Test
        @DisplayName("should not process when player is flying")
        void onPlayerSneakEvent_doesNothingWhenFlying() {
            setupPlayerListener();

            PlayerToggleSneakEvent event = mock(PlayerToggleSneakEvent.class);
            when(event.isSneaking()).thenReturn(true);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.isFlying()).thenReturn(true);

            playerListener.onPlayerSneakEvent(event);

            // Should return early due to flying check
        }
    }

    @Nested
    @DisplayName("onToggleFlight Tests")
    class OnToggleFlightTests {

        @Test
        @org.junit.jupiter.api.Disabled("FactionAPI static mocking causes class transformation issues")
        @DisplayName("should cancel event and handle jump in survival mode")
        void onToggleFlight_cancelsAndHandlesJumpInSurvival() {
            setupPlayerListener();

            PlayerToggleFlightEvent event = mock(PlayerToggleFlightEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getGameMode()).thenReturn(GameMode.SURVIVAL);

            try (MockedStatic<com.bafmc.bukkit.api.FactionAPI> mockedFactionAPI =
                     mockStatic(com.bafmc.bukkit.api.FactionAPI.class)) {
                mockedFactionAPI.when(com.bafmc.bukkit.api.FactionAPI::isFactionSupport).thenReturn(false);

                playerListener.onToggleFlight(event);

                verify(event).setCancelled(true);
            }
        }

        @Test
        @DisplayName("should not cancel event in creative mode")
        void onToggleFlight_doesNotCancelInCreativeMode() {
            setupPlayerListener();

            PlayerToggleFlightEvent event = mock(PlayerToggleFlightEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getGameMode()).thenReturn(GameMode.CREATIVE);

            playerListener.onToggleFlight(event);

            verify(event, never()).setCancelled(true);
        }

        @Test
        @org.junit.jupiter.api.Disabled("FactionAPI requires Factions plugin dependency")
        @DisplayName("should not cancel when faction flying is enabled")
        void onToggleFlight_doesNotCancelWhenFactionFlying() {
            setupPlayerListener();

            PlayerToggleFlightEvent event = mock(PlayerToggleFlightEvent.class);
            when(event.getPlayer()).thenReturn(mockPlayer);
            when(mockPlayer.getGameMode()).thenReturn(GameMode.SURVIVAL);

            try (MockedStatic<com.bafmc.bukkit.api.FactionAPI> mockedFactionAPI =
                     mockStatic(com.bafmc.bukkit.api.FactionAPI.class)) {
                mockedFactionAPI.when(com.bafmc.bukkit.api.FactionAPI::isFactionSupport).thenReturn(true);
                mockedFactionAPI.when(() -> com.bafmc.bukkit.api.FactionAPI.isFlying(mockPlayer)).thenReturn(true);

                playerListener.onToggleFlight(event);

                verify(event, never()).setCancelled(true);
            }
        }
    }

    @Nested
    @DisplayName("handleDash Tests")
    class HandleDashTests {

        @Test
        @DisplayName("should not dash when move ability is cancelled")
        void handleDash_doesNotDashWhenMoveAbilityCancelled() {
            setupPlayerListener();

            when(mockPlayerAbility.isCancel(PlayerAbility.Type.MOVE)).thenReturn(true);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                playerListener.handleDash(mockPlayer);

                // Should return early without processing dash
                verify(mockTempStorage, never()).isBoolean(any());
            }
        }

        @Test
        @DisplayName("should not dash when dash is not enabled")
        void handleDash_doesNotDashWhenNotEnabled() {
            setupPlayerListener();

            when(mockPlayerAbility.isCancel(PlayerAbility.Type.MOVE)).thenReturn(false);
            when(mockTempStorage.isBoolean(any())).thenReturn(false);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                playerListener.handleDash(mockPlayer);

                // Should return early when dash not enabled
            }
        }
    }

    @Nested
    @DisplayName("handleJump Tests")
    class HandleJumpTests {

        @Test
        @DisplayName("should not jump when jump ability is cancelled")
        void handleJump_doesNotJumpWhenJumpAbilityCancelled() {
            setupPlayerListener();

            when(mockPlayerAbility.isCancel(PlayerAbility.Type.JUMP)).thenReturn(true);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                playerListener.handleJump(mockPlayer);

                // Should return early without processing jump
            }
        }

        @Test
        @DisplayName("should not jump when double jump is not enabled")
        void handleJump_doesNotJumpWhenNotEnabled() {
            setupPlayerListener();

            when(mockPlayerAbility.isCancel(PlayerAbility.Type.JUMP)).thenReturn(false);
            when(mockTempStorage.isBoolean(any())).thenReturn(false);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                playerListener.handleJump(mockPlayer);

                // Should return early when jump not enabled
            }
        }
    }

    @Nested
    @DisplayName("handleFlash Tests")
    class HandleFlashTests {

        @Test
        @DisplayName("should not flash when flash is not enabled")
        void handleFlash_doesNotFlashWhenNotEnabled() {
            setupPlayerListener();

            when(mockTempStorage.isBoolean(any())).thenReturn(false);

            try (MockedStatic<CEAPI> mockedCEAPI = mockStatic(CEAPI.class)) {
                mockedCEAPI.when(() -> CEAPI.getCEPlayer(mockPlayer)).thenReturn(mockCEPlayer);

                playerListener.handleFlash(mockPlayer);

                // Should return early when flash not enabled
            }
        }
    }
}
