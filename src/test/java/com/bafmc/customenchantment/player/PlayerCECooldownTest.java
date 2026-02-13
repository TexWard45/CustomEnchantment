package com.bafmc.customenchantment.player;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.enchant.Cooldown;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerCECooldown")
@ExtendWith(MockitoExtension.class)
class PlayerCECooldownTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerCECooldown playerCECooldown;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        playerCECooldown = new PlayerCECooldown(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerCECooldown);
            assertSame(mockCEPlayer, playerCECooldown.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerCECooldown instanceof CEPlayerExpansion);
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should not throw")
        void onJoinShouldNotThrow() {
            assertDoesNotThrow(() -> playerCECooldown.onJoin());
        }

        @Test
        @DisplayName("onQuit should not throw")
        void onQuitShouldNotThrow() {
            assertDoesNotThrow(() -> playerCECooldown.onQuit());
        }
    }

    @Nested
    @DisplayName("put Tests - Single EquipSlot")
    class PutSingleSlotTests {

        @Test
        @DisplayName("Should put cooldown for single equip slot")
        void shouldPutCooldownForSingleSlot() {
            Cooldown cooldown = new Cooldown(5000);
            playerCECooldown.put(EquipSlot.MAINHAND, "TestCE", "TestFunc", cooldown);

            assertFalse(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, "TestCE", "TestFunc"));
        }

        @Test
        @DisplayName("Should handle null ceName")
        void shouldHandleNullCeName() {
            Cooldown cooldown = new Cooldown(5000);
            assertDoesNotThrow(() ->
                    playerCECooldown.put(EquipSlot.MAINHAND, (String) null, "TestFunc", cooldown));
        }

        @Test
        @DisplayName("Should handle null functionName")
        void shouldHandleNullFunctionName() {
            Cooldown cooldown = new Cooldown(5000);
            assertDoesNotThrow(() ->
                    playerCECooldown.put(EquipSlot.MAINHAND, "TestCE", null, cooldown));
        }

        @Test
        @DisplayName("Should handle null cooldown")
        void shouldHandleNullCooldown() {
            assertDoesNotThrow(() ->
                    playerCECooldown.put(EquipSlot.MAINHAND, "TestCE", "TestFunc", null));
        }
    }

    @Nested
    @DisplayName("put Tests - EquipSlot List")
    class PutSlotListTests {

        @Test
        @DisplayName("Should put cooldown for list of equip slots")
        void shouldPutCooldownForSlotList() {
            List<EquipSlot> slots = Arrays.asList(EquipSlot.MAINHAND, EquipSlot.OFFHAND);
            Cooldown cooldown = new Cooldown(5000);
            playerCECooldown.put(slots, "TestCE", "TestFunc", cooldown);

            assertFalse(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, "TestCE", "TestFunc"));
            assertFalse(playerCECooldown.isCooldownTimeout(EquipSlot.OFFHAND, "TestCE", "TestFunc"));
        }
    }

    @Nested
    @DisplayName("put Tests - EquipSlot Array")
    class PutSlotArrayTests {

        @Test
        @DisplayName("Should put cooldown for array of equip slots")
        void shouldPutCooldownForSlotArray() {
            EquipSlot[] slots = {EquipSlot.MAINHAND, EquipSlot.OFFHAND};
            Cooldown cooldown = new Cooldown(5000);
            playerCECooldown.put(slots, "TestCE", "TestFunc", cooldown);

            assertFalse(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, "TestCE", "TestFunc"));
            assertFalse(playerCECooldown.isCooldownTimeout(EquipSlot.OFFHAND, "TestCE", "TestFunc"));
        }
    }

    @Nested
    @DisplayName("isCooldownTimeout Tests")
    class IsCooldownTimeoutTests {

        @Test
        @DisplayName("Should return true when no cooldown is set")
        void shouldReturnTrueWhenNoCooldownSet() {
            assertTrue(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, "TestCE", "TestFunc"));
        }

        @Test
        @DisplayName("Should return false when cooldown is active")
        void shouldReturnFalseWhenCooldownIsActive() {
            Cooldown cooldown = new Cooldown(60000);
            playerCECooldown.put(EquipSlot.MAINHAND, "TestCE", "TestFunc", cooldown);

            assertFalse(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, "TestCE", "TestFunc"));
        }

        @Test
        @DisplayName("Should return true after cooldown expires")
        void shouldReturnTrueAfterCooldownExpires() {
            Cooldown cooldown = new Cooldown(1); // 1ms cooldown
            playerCECooldown.put(EquipSlot.MAINHAND, "TestCE", "TestFunc", cooldown);

            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }

            assertTrue(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, "TestCE", "TestFunc"));
        }

        @Test
        @DisplayName("Should handle null ceName in isCooldownTimeout")
        void shouldHandleNullCeNameInTimeout() {
            assertTrue(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, (String) null, "TestFunc"));
        }

        @Test
        @DisplayName("Should handle null functionName in isCooldownTimeout")
        void shouldHandleNullFunctionNameInTimeout() {
            assertTrue(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, "TestCE", null));
        }
    }

    @Nested
    @DisplayName("Global Cooldown Tests")
    class GlobalCooldownTests {

        @Test
        @DisplayName("ALL slot cooldown should block all specific slots")
        void allSlotCooldownShouldBlockAllSpecificSlots() {
            Cooldown cooldown = new Cooldown(60000);
            playerCECooldown.put(EquipSlot.ALL, "TestCE", "TestFunc", cooldown);

            assertFalse(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, "TestCE", "TestFunc"));
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should use ConcurrentHashMap for main map")
        void shouldUseConcurrentHashMapForMainMap() throws Exception {
            var field = PlayerCECooldown.class.getDeclaredField("map");
            field.setAccessible(true);
            assertTrue(field.get(playerCECooldown) instanceof java.util.concurrent.ConcurrentHashMap);
        }
    }

    @Nested
    @DisplayName("Zero Cooldown Tests")
    class ZeroCooldownTests {

        @Test
        @DisplayName("Should timeout very quickly with zero cooldown")
        void shouldTimeoutVeryQuicklyWithZeroCooldown() {
            Cooldown cooldown = new Cooldown(0);
            playerCECooldown.put(EquipSlot.MAINHAND, "TestCE", "TestFunc", cooldown);

            // Zero cooldown means end == start. isInCooldown checks currentTimeMillis() > end,
            // so it may still be "in cooldown" if checked in the same millisecond.
            // Wait 2ms to ensure the cooldown has expired.
            try {
                Thread.sleep(2);
            } catch (InterruptedException ignored) {
            }

            assertTrue(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, "TestCE", "TestFunc"));
        }

        @Test
        @DisplayName("Should handle null cooldown by creating zero cooldown")
        void shouldHandleNullCooldownByCreatingZeroCooldown() {
            playerCECooldown.put(EquipSlot.MAINHAND, "TestCE", "TestFunc", null);

            // Null cooldown creates new Cooldown(0) internally
            try {
                Thread.sleep(2);
            } catch (InterruptedException ignored) {
            }

            assertTrue(playerCECooldown.isCooldownTimeout(EquipSlot.MAINHAND, "TestCE", "TestFunc"));
        }
    }
}
