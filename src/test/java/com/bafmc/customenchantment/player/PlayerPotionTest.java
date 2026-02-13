package com.bafmc.customenchantment.player;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for PlayerPotion.
 * Note: PotionEffectType cannot be mocked with Mockito in this environment.
 * Tests focus on class structure, thread safety, and lifecycle.
 */
@DisplayName("PlayerPotion")
@ExtendWith(MockitoExtension.class)
class PlayerPotionTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerPotion playerPotion;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        playerPotion = new PlayerPotion(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerPotion);
            assertSame(mockCEPlayer, playerPotion.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerPotion instanceof CEPlayerExpansion);
        }

        @Test
        @DisplayName("Should have constructor accepting CEPlayer")
        void shouldHaveConstructorAcceptingCEPlayer() throws NoSuchMethodException {
            assertNotNull(PlayerPotion.class.getConstructor(CEPlayer.class));
        }
    }

    @Nested
    @DisplayName("Method Signature Tests")
    class MethodSignatureTests {

        @Test
        @DisplayName("Should have addPotionType method")
        void shouldHaveAddPotionTypeMethod() throws NoSuchMethodException {
            assertNotNull(PlayerPotion.class.getMethod("addPotionType", String.class, PotionEffectType.class, int.class));
        }

        @Test
        @DisplayName("Should have removePotionType method")
        void shouldHaveRemovePotionTypeMethod() throws NoSuchMethodException {
            assertNotNull(PlayerPotion.class.getMethod("removePotionType", String.class));
        }

        @Test
        @DisplayName("Should have blockPotionType method")
        void shouldHaveBlockPotionTypeMethod() throws NoSuchMethodException {
            assertNotNull(PlayerPotion.class.getMethod("blockPotionType", String.class, PotionEffectType.class));
        }

        @Test
        @DisplayName("Should have unblockPotionType method")
        void shouldHaveUnblockPotionTypeMethod() throws NoSuchMethodException {
            assertNotNull(PlayerPotion.class.getMethod("unblockPotionType", String.class));
        }

        @Test
        @DisplayName("Should have updatePotion method")
        void shouldHaveUpdatePotionMethod() throws NoSuchMethodException {
            assertNotNull(PlayerPotion.class.getMethod("updatePotion"));
        }

        @Test
        @DisplayName("Should have clearAllPotion method")
        void shouldHaveClearAllPotionMethod() throws NoSuchMethodException {
            assertNotNull(PlayerPotion.class.getMethod("clearAllPotion"));
        }

        @Test
        @DisplayName("Should have getHighestAmplifierByPotionType method")
        void shouldHaveGetHighestAmplifierMethod() throws NoSuchMethodException {
            assertNotNull(PlayerPotion.class.getMethod("getHighestAmplifierByPotionType", PotionEffectType.class));
        }

        @Test
        @DisplayName("Should have updateHighestPriority method")
        void shouldHaveUpdateHighestPriorityMethod() throws NoSuchMethodException {
            assertNotNull(PlayerPotion.class.getMethod("updateHighestPriority", PotionEffectType.class));
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should not throw with empty maps")
        void onJoinShouldNotThrowWithEmptyMaps() {
            assertDoesNotThrow(() -> playerPotion.onJoin());
        }

        @Test
        @DisplayName("onQuit should not throw with empty maps")
        void onQuitShouldNotThrowWithEmptyMaps() {
            assertDoesNotThrow(() -> playerPotion.onQuit());
        }
    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("potionTypeActiveMap should be ConcurrentHashMap")
        void activeMapShouldBeConcurrentHashMap() throws Exception {
            Field field = PlayerPotion.class.getDeclaredField("potionTypeActiveMap");
            field.setAccessible(true);
            assertTrue(field.get(playerPotion) instanceof java.util.concurrent.ConcurrentHashMap);
        }

        @Test
        @DisplayName("potionTypeBlockMap should be ConcurrentHashMap")
        void blockMapShouldBeConcurrentHashMap() throws Exception {
            Field field = PlayerPotion.class.getDeclaredField("potionTypeBlockMap");
            field.setAccessible(true);
            assertTrue(field.get(playerPotion) instanceof java.util.concurrent.ConcurrentHashMap);
        }

        @Test
        @DisplayName("potionTypeAmplifierMap should be ConcurrentHashMap")
        void amplifierMapShouldBeConcurrentHashMap() throws Exception {
            Field field = PlayerPotion.class.getDeclaredField("potionTypeAmplifierMap");
            field.setAccessible(true);
            assertTrue(field.get(playerPotion) instanceof java.util.concurrent.ConcurrentHashMap);
        }
    }

    @Nested
    @DisplayName("unblockPotionType Tests")
    class UnblockPotionTypeTests {

        @Test
        @DisplayName("Should not throw when unblocking non-existing name")
        void shouldNotThrowWhenUnblockingNonExisting() {
            assertDoesNotThrow(() -> playerPotion.unblockPotionType("nonexistent"));
        }
    }

    @Nested
    @DisplayName("removePotionType Tests")
    class RemovePotionTypeTests {

        @Test
        @DisplayName("Should not throw when removing non-existing potion")
        void shouldNotThrowWhenRemovingNonExisting() {
            assertDoesNotThrow(() -> playerPotion.removePotionType("nonexistent"));
        }
    }
}
