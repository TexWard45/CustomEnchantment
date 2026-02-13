package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.attribute.CustomAttributeType;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerAbility")
@ExtendWith(MockitoExtension.class)
class PlayerAbilityTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private PlayerCustomAttribute mockCustomAttribute;

    private PlayerAbility playerAbility;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockCEPlayer.getCustomAttribute()).thenReturn(mockCustomAttribute);
        lenient().when(mockCustomAttribute.getValue(CustomAttributeType.MAGIC_RESISTANCE)).thenReturn(0.0);
        playerAbility = new PlayerAbility(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerAbility);
            assertSame(mockCEPlayer, playerAbility.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerAbility instanceof CEPlayerExpansion);
        }
    }

    @Nested
    @DisplayName("Type Enum Tests")
    class TypeEnumTests {

        @Test
        @DisplayName("Should have ATTACK type")
        void shouldHaveAttackType() {
            assertNotNull(PlayerAbility.Type.ATTACK);
        }

        @Test
        @DisplayName("Should have MOVE type")
        void shouldHaveMoveType() {
            assertNotNull(PlayerAbility.Type.MOVE);
        }

        @Test
        @DisplayName("Should have JUMP type")
        void shouldHaveJumpType() {
            assertNotNull(PlayerAbility.Type.JUMP);
        }

        @Test
        @DisplayName("Should have LOOK type")
        void shouldHaveLookType() {
            assertNotNull(PlayerAbility.Type.LOOK);
        }

        @Test
        @DisplayName("Should have exactly 4 types")
        void shouldHaveExactly4Types() {
            assertEquals(4, PlayerAbility.Type.values().length);
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should initialize cancel managers for all types")
        void onJoinShouldInitializeCancelManagers() {
            playerAbility.onJoin();

            for (PlayerAbility.Type type : PlayerAbility.Type.values()) {
                assertFalse(playerAbility.isCancel(type));
            }
        }

        @Test
        @DisplayName("onQuit should not throw")
        void onQuitShouldNotThrow() {
            assertDoesNotThrow(() -> playerAbility.onQuit());
        }
    }

    @Nested
    @DisplayName("setCancel Tests")
    class SetCancelTests {

        @Test
        @DisplayName("Should set cancel for specific type")
        void shouldSetCancelForSpecificType() {
            playerAbility.onJoin();
            playerAbility.setCancel(PlayerAbility.Type.ATTACK, "test", true, 5000);
            assertTrue(playerAbility.isCancel(PlayerAbility.Type.ATTACK));
        }

        @Test
        @DisplayName("Should not affect other types when setting cancel")
        void shouldNotAffectOtherTypes() {
            playerAbility.onJoin();
            playerAbility.setCancel(PlayerAbility.Type.ATTACK, "test", true, 5000);

            assertFalse(playerAbility.isCancel(PlayerAbility.Type.MOVE));
            assertFalse(playerAbility.isCancel(PlayerAbility.Type.JUMP));
            assertFalse(playerAbility.isCancel(PlayerAbility.Type.LOOK));
        }

        @ParameterizedTest
        @EnumSource(PlayerAbility.Type.class)
        @DisplayName("Should set cancel for each type")
        void shouldSetCancelForEachType(PlayerAbility.Type type) {
            playerAbility.onJoin();
            playerAbility.setCancel(type, "unique", true, 5000);
            assertTrue(playerAbility.isCancel(type));
        }
    }

    @Nested
    @DisplayName("isCancel Tests")
    class IsCancelTests {

        @Test
        @DisplayName("Should return false when type not initialized")
        void shouldReturnFalseWhenTypeNotInitialized() {
            // Before onJoin, map is empty
            assertFalse(playerAbility.isCancel(PlayerAbility.Type.ATTACK));
        }

        @Test
        @DisplayName("Should return false when no cancel is set")
        void shouldReturnFalseWhenNoCancelSet() {
            playerAbility.onJoin();
            assertFalse(playerAbility.isCancel(PlayerAbility.Type.ATTACK));
        }

        @Test
        @DisplayName("Should return true when cancel is active")
        void shouldReturnTrueWhenCancelIsActive() {
            playerAbility.onJoin();
            playerAbility.setCancel(PlayerAbility.Type.MOVE, "test", true, 60000);
            assertTrue(playerAbility.isCancel(PlayerAbility.Type.MOVE));
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should use ConcurrentHashMap for type map")
        void shouldUseConcurrentHashMap() throws Exception {
            var field = PlayerAbility.class.getDeclaredField("map");
            field.setAccessible(true);
            assertTrue(field.get(playerAbility) instanceof java.util.concurrent.ConcurrentHashMap);
        }
    }

    @Nested
    @DisplayName("Null Safety Tests")
    class NullSafetyTests {

        @Test
        @DisplayName("setCancel should handle null cancel manager gracefully")
        void setCancelShouldHandleNullCancelManager() {
            // Before onJoin, map is empty so cancel managers are null
            assertDoesNotThrow(() ->
                    playerAbility.setCancel(PlayerAbility.Type.ATTACK, "test", true, 1000));
        }

        @Test
        @DisplayName("isCancel should handle null cancel manager gracefully")
        void isCancelShouldHandleNullCancelManager() {
            // Before onJoin, map is empty
            assertFalse(playerAbility.isCancel(PlayerAbility.Type.ATTACK));
        }
    }
}
