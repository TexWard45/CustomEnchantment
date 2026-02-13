package com.bafmc.customenchantment.player;

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

@DisplayName("CancelManager")
@ExtendWith(MockitoExtension.class)
class CancelManagerTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private PlayerCustomAttribute mockCustomAttribute;

    private CancelManager cancelManager;
    private CEPlayerExpansion expansion;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockCEPlayer.getCustomAttribute()).thenReturn(mockCustomAttribute);
        lenient().when(mockCustomAttribute.getValue(CustomAttributeType.MAGIC_RESISTANCE)).thenReturn(0.0);

        expansion = new CEPlayerExpansion(mockCEPlayer) {
            @Override
            public void onJoin() {
            }

            @Override
            public void onQuit() {
            }
        };
        cancelManager = new CancelManager(expansion);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayerExpansion")
        void shouldCreateInstanceWithCEPlayerExpansion() {
            assertNotNull(cancelManager);
        }
    }

    @Nested
    @DisplayName("setCancel Tests")
    class SetCancelTests {

        @Test
        @DisplayName("Should set cancel with positive duration")
        void shouldSetCancelWithPositiveDuration() {
            cancelManager.setCancel("test", true, 5000);
            assertTrue(cancelManager.isCancel());
        }

        @Test
        @DisplayName("Should remove cancel when cancel is false")
        void shouldRemoveCancelWhenCancelIsFalse() {
            cancelManager.setCancel("test", true, 5000);
            cancelManager.setCancel("test", false, 0);
            assertFalse(cancelManager.isCancel());
        }

        @Test
        @DisplayName("Should remove entry when duration is zero and cancel is false")
        void shouldRemoveEntryWhenDurationIsZero() {
            cancelManager.setCancel("test", true, 5000);
            cancelManager.setCancel("test", false, 0);
            assertFalse(cancelManager.isCancel());
        }

        @Test
        @DisplayName("Should handle multiple unique cancels")
        void shouldHandleMultipleUniqueCancels() {
            cancelManager.setCancel("unique1", true, 5000);
            cancelManager.setCancel("unique2", true, 5000);
            assertTrue(cancelManager.isCancel());

            cancelManager.setCancel("unique1", false, 0);
            assertTrue(cancelManager.isCancel()); // unique2 still active
        }

        @Test
        @DisplayName("Should not add cancel when magic resistance eliminates duration")
        void shouldNotAddWhenMagicResistanceEliminatesDuration() {
            when(mockCustomAttribute.getValue(CustomAttributeType.MAGIC_RESISTANCE)).thenReturn(100.0);
            cancelManager.setCancel("test", true, 1000);
            assertFalse(cancelManager.isCancel());
        }

        @Test
        @DisplayName("Should reduce duration based on magic resistance")
        void shouldReduceDurationBasedOnMagicResistance() {
            when(mockCustomAttribute.getValue(CustomAttributeType.MAGIC_RESISTANCE)).thenReturn(50.0);
            cancelManager.setCancel("test", true, 10000); // 10s -> 5s after resistance
            assertTrue(cancelManager.isCancel());
        }
    }

    @Nested
    @DisplayName("isCancel Tests")
    class IsCancelTests {

        @Test
        @DisplayName("Should return false when no cancels set")
        void shouldReturnFalseWhenNoCancelsSet() {
            assertFalse(cancelManager.isCancel());
        }

        @Test
        @DisplayName("Should return true when active cancel exists")
        void shouldReturnTrueWhenActiveCancelExists() {
            cancelManager.setCancel("test", true, 60000);
            assertTrue(cancelManager.isCancel());
        }

        @Test
        @DisplayName("Should return false after cancel expires")
        void shouldReturnFalseAfterCancelExpires() {
            // Set cancel with 1ms duration
            cancelManager.setCancel("test", true, 1);

            // Wait for expiration
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }

            assertFalse(cancelManager.isCancel());
        }

        @Test
        @DisplayName("Should clean up expired entries during check")
        void shouldCleanUpExpiredEntries() {
            cancelManager.setCancel("test", true, 1);

            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }

            // After isCancel cleans up, result should be false
            assertFalse(cancelManager.isCancel());
        }
    }

    @Nested
    @DisplayName("CancelData Tests")
    class CancelDataTests {

        @Test
        @DisplayName("Should store cancel flag")
        void shouldStoreCancelFlag() {
            CancelManager.CancelData data = new CancelManager.CancelData(true, 1000L);
            assertTrue(data.isCancel());
        }

        @Test
        @DisplayName("Should store end time")
        void shouldStoreEndTime() {
            CancelManager.CancelData data = new CancelManager.CancelData(true, 1000L);
            assertEquals(1000L, data.getEndTime());
        }

        @Test
        @DisplayName("Should support false cancel flag")
        void shouldSupportFalseCancelFlag() {
            CancelManager.CancelData data = new CancelManager.CancelData(false, 1000L);
            assertFalse(data.isCancel());
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should use ConcurrentHashMap for list")
        void shouldUseConcurrentHashMap() throws Exception {
            var field = CancelManager.class.getDeclaredField("list");
            field.setAccessible(true);
            assertTrue(field.get(cancelManager) instanceof java.util.concurrent.ConcurrentHashMap);
        }
    }
}
