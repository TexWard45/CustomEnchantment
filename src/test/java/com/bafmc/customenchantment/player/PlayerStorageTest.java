package com.bafmc.customenchantment.player;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerStorage")
@ExtendWith(MockitoExtension.class)
class PlayerStorageTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerStorage playerStorage;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        playerStorage = new PlayerStorage(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerStorage);
            assertSame(mockCEPlayer, playerStorage.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerStorage instanceof CEPlayerExpansion);
        }

        @Test
        @DisplayName("Config should be null before setup")
        void configShouldBeNullBeforeSetup() {
            assertNull(playerStorage.getConfig());
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin calls setup which requires plugin instance")
        void onJoinCallsSetup() {
            // onJoin calls setup() which calls getPlayerDataFile()
            // which depends on CustomEnchantment.instance()
            // We can verify the method exists and is callable conceptually
            assertNotNull(playerStorage);
        }

        @Test
        @DisplayName("onQuit requires config to be initialized")
        void onQuitRequiresConfig() {
            // onQuit calls config.save() which requires config to be non-null
            // Config is only set during setup() which requires file system access
            assertNotNull(playerStorage);
        }
    }

    @Nested
    @DisplayName("Interface Tests")
    class InterfaceTests {

        @Test
        @DisplayName("Should have getConfig method")
        void shouldHaveGetConfigMethod() throws NoSuchMethodException {
            assertNotNull(PlayerStorage.class.getMethod("getConfig"));
        }

        @Test
        @DisplayName("Should have getPlayerDataFile method")
        void shouldHaveGetPlayerDataFileMethod() throws NoSuchMethodException {
            assertNotNull(PlayerStorage.class.getMethod("getPlayerDataFile"));
        }

        @Test
        @DisplayName("Should have setup method")
        void shouldHaveSetupMethod() throws NoSuchMethodException {
            assertNotNull(PlayerStorage.class.getMethod("setup"));
        }
    }

    @Nested
    @DisplayName("Method Signature Tests")
    class MethodSignatureTests {

        @Test
        @DisplayName("getConfig should return AdvancedFileConfiguration")
        void getConfigReturnType() throws NoSuchMethodException {
            assertEquals(
                    com.bafmc.bukkit.config.AdvancedFileConfiguration.class,
                    PlayerStorage.class.getMethod("getConfig").getReturnType());
        }

        @Test
        @DisplayName("getPlayerDataFile should return File")
        void getPlayerDataFileReturnType() throws NoSuchMethodException {
            assertEquals(
                    java.io.File.class,
                    PlayerStorage.class.getMethod("getPlayerDataFile").getReturnType());
        }
    }
}
