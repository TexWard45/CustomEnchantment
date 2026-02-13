package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.player.bonus.BlockBonus;
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

@DisplayName("PlayerBlockBonus")
@ExtendWith(MockitoExtension.class)
class PlayerBlockBonusTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerBlockBonus playerBlockBonus;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        playerBlockBonus = new PlayerBlockBonus(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerBlockBonus);
            assertSame(mockCEPlayer, playerBlockBonus.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerBlockBonus instanceof CEPlayerExpansion);
        }

        @Test
        @DisplayName("Should initialize expBonus")
        void shouldInitializeExpBonus() {
            assertNotNull(playerBlockBonus.getExpBonus());
        }

        @Test
        @DisplayName("Should initialize moneyBonus")
        void shouldInitializeMoneyBonus() {
            assertNotNull(playerBlockBonus.getMoneyBonus());
        }

        @Test
        @DisplayName("expBonus and moneyBonus should be different instances")
        void expBonusAndMoneyBonusShouldBeDifferentInstances() {
            assertNotSame(playerBlockBonus.getExpBonus(), playerBlockBonus.getMoneyBonus());
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should not throw")
        void onJoinShouldNotThrow() {
            assertDoesNotThrow(() -> playerBlockBonus.onJoin());
        }

        @Test
        @DisplayName("onQuit should not throw")
        void onQuitShouldNotThrow() {
            assertDoesNotThrow(() -> playerBlockBonus.onQuit());
        }
    }

    @Nested
    @DisplayName("Bonus Instance Tests")
    class BonusInstanceTests {

        @Test
        @DisplayName("getExpBonus should return BlockBonus instance")
        void getExpBonusShouldReturnBlockBonusInstance() {
            assertTrue(playerBlockBonus.getExpBonus() instanceof BlockBonus);
        }

        @Test
        @DisplayName("getMoneyBonus should return BlockBonus instance")
        void getMoneyBonusShouldReturnBlockBonusInstance() {
            assertTrue(playerBlockBonus.getMoneyBonus() instanceof BlockBonus);
        }

        @Test
        @DisplayName("Bonuses should start empty")
        void bonusesShouldStartEmpty() {
            assertTrue(playerBlockBonus.getExpBonus().isEmpty());
            assertTrue(playerBlockBonus.getMoneyBonus().isEmpty());
        }

        @Test
        @DisplayName("getExpBonus should return same instance on multiple calls")
        void getExpBonusShouldReturnSameInstance() {
            assertSame(playerBlockBonus.getExpBonus(), playerBlockBonus.getExpBonus());
        }

        @Test
        @DisplayName("getMoneyBonus should return same instance on multiple calls")
        void getMoneyBonusShouldReturnSameInstance() {
            assertSame(playerBlockBonus.getMoneyBonus(), playerBlockBonus.getMoneyBonus());
        }
    }
}
