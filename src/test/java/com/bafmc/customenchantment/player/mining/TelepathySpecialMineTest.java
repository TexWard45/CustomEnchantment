package com.bafmc.customenchantment.player.mining;

import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.player.PlayerTemporaryStorage;
import com.bafmc.customenchantment.player.TemporaryKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TelepathySpecialMine")
@ExtendWith(MockitoExtension.class)
class TelepathySpecialMineTest {

    @Mock
    private PlayerSpecialMining mockPlayerSpecialMining;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private Player mockPlayer;

    @Mock
    private PlayerTemporaryStorage mockTemporaryStorage;

    private TelepathySpecialMine telepathySpecialMine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lenient().when(mockPlayerSpecialMining.getCEPlayer()).thenReturn(mockCEPlayer);
        lenient().when(mockPlayerSpecialMining.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockTemporaryStorage);
        telepathySpecialMine = new TelepathySpecialMine(mockPlayerSpecialMining);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with PlayerSpecialMining")
        void shouldCreateInstance() {
            assertNotNull(telepathySpecialMine);
            assertEquals(mockPlayerSpecialMining, telepathySpecialMine.getPlayerSpecialMining());
        }
    }

    @Nested
    @DisplayName("Priority Tests")
    class PriorityTests {

        @Test
        @DisplayName("Should return priority of 20")
        void shouldReturnPriority20() {
            assertEquals(20, telepathySpecialMine.getPriority());
        }

        @Test
        @DisplayName("Priority should be higher than default")
        void priorityShouldBeHigherThanDefault() {
            AbstractSpecialMine defaultMine = mock(AbstractSpecialMine.class);
            when(defaultMine.getPriority()).thenReturn(0);

            assertTrue(telepathySpecialMine.getPriority() > defaultMine.getPriority());
        }
    }

    @Nested
    @DisplayName("isWork Tests")
    class IsWorkTests {

        @Test
        @DisplayName("Should return true when telepathy is enabled")
        void shouldReturnTrueWhenTelepathyEnabled() {
            when(mockTemporaryStorage.isBoolean(TemporaryKey.MINING_TELEPATHY_ENABLE)).thenReturn(true);

            assertTrue(telepathySpecialMine.isWork(false));
        }

        @Test
        @DisplayName("Should return false when telepathy is disabled")
        void shouldReturnFalseWhenTelepathyDisabled() {
            when(mockTemporaryStorage.isBoolean(TemporaryKey.MINING_TELEPATHY_ENABLE)).thenReturn(false);

            assertFalse(telepathySpecialMine.isWork(false));
        }

        @Test
        @DisplayName("Should check MINING_TELEPATHY_ENABLE key")
        void shouldCheckMiningTelepathyEnableKey() {
            telepathySpecialMine.isWork(false);

            verify(mockTemporaryStorage).isBoolean(TemporaryKey.MINING_TELEPATHY_ENABLE);
        }

        @Test
        @DisplayName("Should work regardless of fake flag")
        void shouldWorkRegardlessOfFakeFlag() {
            when(mockTemporaryStorage.isBoolean(TemporaryKey.MINING_TELEPATHY_ENABLE)).thenReturn(true);

            assertTrue(telepathySpecialMine.isWork(true));
            assertTrue(telepathySpecialMine.isWork(false));
        }
    }

    @Nested
    @DisplayName("doSpecialMine Tests")
    class DoSpecialMineTests {

        @Test
        @DisplayName("Should do nothing on doSpecialMine")
        void shouldDoNothingOnDoSpecialMine() {
            SpecialMiningData data = mock(SpecialMiningData.class);
            // Should not throw any exception
            telepathySpecialMine.doSpecialMine(data, false);
            telepathySpecialMine.doSpecialMine(data, true);
        }
    }

    @Nested
    @DisplayName("Interaction with TemporaryStorage Tests")
    class TemporaryStorageInteractionTests {

        @Test
        @DisplayName("Should query CEPlayer for temporary storage")
        void shouldQueryCEPlayerForTemporaryStorage() {
            when(mockTemporaryStorage.isBoolean(anyString())).thenReturn(false);

            telepathySpecialMine.isWork(false);

            verify(mockCEPlayer).getTemporaryStorage();
        }

        @Test
        @DisplayName("Should use correct temporary key constant")
        void shouldUseCorrectTemporaryKeyConstant() {
            assertEquals("mining_telepathy_enable", TemporaryKey.MINING_TELEPATHY_ENABLE);
        }
    }
}
