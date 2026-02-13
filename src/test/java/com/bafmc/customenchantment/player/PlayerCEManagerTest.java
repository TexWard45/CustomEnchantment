package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.attribute.CustomAttributeType;
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

@DisplayName("PlayerCEManager")
@ExtendWith(MockitoExtension.class)
class PlayerCEManagerTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private PlayerCustomAttribute mockCustomAttribute;

    private PlayerCEManager playerCEManager;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockCEPlayer.getCustomAttribute()).thenReturn(mockCustomAttribute);
        lenient().when(mockCustomAttribute.getValue(CustomAttributeType.MAGIC_RESISTANCE)).thenReturn(0.0);
        playerCEManager = new PlayerCEManager(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerCEManager);
            assertSame(mockCEPlayer, playerCEManager.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerCEManager instanceof CEPlayerExpansion);
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should initialize cancel managers for all equip slots")
        void onJoinShouldInitializeCancelManagers() {
            playerCEManager.onJoin();

            // After onJoin, all slots should have cancel managers
            for (EquipSlot slot : EquipSlot.ALL_ARRAY) {
                assertFalse(playerCEManager.isCancelSlot(slot));
            }
        }

        @Test
        @DisplayName("onQuit should not throw")
        void onQuitShouldNotThrow() {
            assertDoesNotThrow(() -> playerCEManager.onQuit());
        }
    }

    @Nested
    @DisplayName("setCancelSlot Tests")
    class SetCancelSlotTests {

        @Test
        @DisplayName("Should set cancel for specific slot")
        void shouldSetCancelForSpecificSlot() {
            playerCEManager.onJoin();
            playerCEManager.setCancelSlot(EquipSlot.MAINHAND, "test", true, 5000);
            assertTrue(playerCEManager.isCancelSlot(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("Should not affect other slots")
        void shouldNotAffectOtherSlots() {
            playerCEManager.onJoin();
            playerCEManager.setCancelSlot(EquipSlot.MAINHAND, "test", true, 5000);
            assertFalse(playerCEManager.isCancelSlot(EquipSlot.OFFHAND));
        }
    }

    @Nested
    @DisplayName("isCancelSlot Tests")
    class IsCancelSlotTests {

        @Test
        @DisplayName("Should return false when slot not in map")
        void shouldReturnFalseWhenSlotNotInMap() {
            // Before onJoin, map is empty
            assertFalse(playerCEManager.isCancelSlot(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("Should return false when no cancel set for slot")
        void shouldReturnFalseWhenNoCancelSet() {
            playerCEManager.onJoin();
            assertFalse(playerCEManager.isCancelSlot(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("Should return true when cancel is active for slot")
        void shouldReturnTrueWhenCancelActive() {
            playerCEManager.onJoin();
            playerCEManager.setCancelSlot(EquipSlot.MAINHAND, "unique", true, 60000);
            assertTrue(playerCEManager.isCancelSlot(EquipSlot.MAINHAND));
        }

        @Test
        @DisplayName("Should return false after cancel expires")
        void shouldReturnFalseAfterCancelExpires() {
            playerCEManager.onJoin();
            playerCEManager.setCancelSlot(EquipSlot.MAINHAND, "unique", true, 1);

            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }

            assertFalse(playerCEManager.isCancelSlot(EquipSlot.MAINHAND));
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should use ConcurrentHashMap for internal map")
        void shouldUseConcurrentHashMap() throws Exception {
            var field = PlayerCEManager.class.getDeclaredField("map");
            field.setAccessible(true);
            assertTrue(field.get(playerCEManager) instanceof java.util.concurrent.ConcurrentHashMap);
        }
    }
}
