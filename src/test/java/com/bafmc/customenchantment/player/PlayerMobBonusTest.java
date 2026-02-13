package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.player.bonus.EntityTypeBonus;
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

@DisplayName("PlayerMobBonus")
@ExtendWith(MockitoExtension.class)
class PlayerMobBonusTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerMobBonus playerMobBonus;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        playerMobBonus = new PlayerMobBonus(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerMobBonus);
            assertSame(mockCEPlayer, playerMobBonus.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerMobBonus instanceof CEPlayerExpansion);
        }

        @Test
        @DisplayName("Should initialize expBonus")
        void shouldInitializeExpBonus() {
            assertNotNull(playerMobBonus.getExpBonus());
        }

        @Test
        @DisplayName("Should initialize moneyBonus")
        void shouldInitializeMoneyBonus() {
            assertNotNull(playerMobBonus.getMoneyBonus());
        }

        @Test
        @DisplayName("Should initialize msExpBonus")
        void shouldInitializeMsExpBonus() {
            assertNotNull(playerMobBonus.getMobSlayerExpBonus());
        }

        @Test
        @DisplayName("All three bonuses should be different instances")
        void allBonusesShouldBeDifferentInstances() {
            assertNotSame(playerMobBonus.getExpBonus(), playerMobBonus.getMoneyBonus());
            assertNotSame(playerMobBonus.getExpBonus(), playerMobBonus.getMobSlayerExpBonus());
            assertNotSame(playerMobBonus.getMoneyBonus(), playerMobBonus.getMobSlayerExpBonus());
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should not throw")
        void onJoinShouldNotThrow() {
            assertDoesNotThrow(() -> playerMobBonus.onJoin());
        }

        @Test
        @DisplayName("onQuit should not throw")
        void onQuitShouldNotThrow() {
            assertDoesNotThrow(() -> playerMobBonus.onQuit());
        }
    }

    @Nested
    @DisplayName("Bonus Instance Tests")
    class BonusInstanceTests {

        @Test
        @DisplayName("getExpBonus should return EntityTypeBonus instance")
        void getExpBonusShouldReturnEntityTypeBonusInstance() {
            assertTrue(playerMobBonus.getExpBonus() instanceof EntityTypeBonus);
        }

        @Test
        @DisplayName("getMoneyBonus should return EntityTypeBonus instance")
        void getMoneyBonusShouldReturnEntityTypeBonusInstance() {
            assertTrue(playerMobBonus.getMoneyBonus() instanceof EntityTypeBonus);
        }

        @Test
        @DisplayName("getMobSlayerExpBonus should return EntityTypeBonus instance")
        void getMobSlayerExpBonusShouldReturnEntityTypeBonusInstance() {
            assertTrue(playerMobBonus.getMobSlayerExpBonus() instanceof EntityTypeBonus);
        }

        @Test
        @DisplayName("All bonuses should start empty")
        void allBonusesShouldStartEmpty() {
            assertTrue(playerMobBonus.getExpBonus().isEmpty());
            assertTrue(playerMobBonus.getMoneyBonus().isEmpty());
            assertTrue(playerMobBonus.getMobSlayerExpBonus().isEmpty());
        }

        @Test
        @DisplayName("Getters should return same instance on repeated calls")
        void gettersShouldReturnSameInstance() {
            assertSame(playerMobBonus.getExpBonus(), playerMobBonus.getExpBonus());
            assertSame(playerMobBonus.getMoneyBonus(), playerMobBonus.getMoneyBonus());
            assertSame(playerMobBonus.getMobSlayerExpBonus(), playerMobBonus.getMobSlayerExpBonus());
        }
    }
}
