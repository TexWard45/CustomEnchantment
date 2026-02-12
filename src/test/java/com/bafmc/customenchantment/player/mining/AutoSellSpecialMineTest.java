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

@DisplayName("AutoSellSpecialMine")
@ExtendWith(MockitoExtension.class)
class AutoSellSpecialMineTest {

    @Mock
    private PlayerSpecialMining mockPlayerSpecialMining;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private Player mockPlayer;

    @Mock
    private PlayerTemporaryStorage mockTemporaryStorage;

    private AutoSellSpecialMine autoSellSpecialMine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lenient().when(mockPlayerSpecialMining.getCEPlayer()).thenReturn(mockCEPlayer);
        lenient().when(mockPlayerSpecialMining.getPlayer()).thenReturn(mockPlayer);
        lenient().when(mockCEPlayer.getTemporaryStorage()).thenReturn(mockTemporaryStorage);
        autoSellSpecialMine = new AutoSellSpecialMine(mockPlayerSpecialMining);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with PlayerSpecialMining")
        void shouldCreateInstance() {
            assertNotNull(autoSellSpecialMine);
            assertEquals(mockPlayerSpecialMining, autoSellSpecialMine.getPlayerSpecialMining());
        }
    }

    @Nested
    @DisplayName("Priority Tests")
    class PriorityTests {

        @Test
        @DisplayName("Should return priority of 10")
        void shouldReturnPriority10() {
            assertEquals(10, autoSellSpecialMine.getPriority());
        }

        @Test
        @DisplayName("Priority should be higher than default")
        void priorityShouldBeHigherThanDefault() {
            AbstractSpecialMine defaultMine = mock(AbstractSpecialMine.class);
            when(defaultMine.getPriority()).thenReturn(0);

            assertTrue(autoSellSpecialMine.getPriority() > defaultMine.getPriority());
        }

        @Test
        @DisplayName("Priority should be lower than telepathy")
        void priorityShouldBeLowerThanTelepathy() {
            // TelepathySpecialMine has priority 20
            assertTrue(autoSellSpecialMine.getPriority() < 20);
        }
    }

    @Nested
    @DisplayName("isWork Tests")
    class IsWorkTests {

        @Test
        @DisplayName("Should return true when auto sell is enabled")
        void shouldReturnTrueWhenAutoSellEnabled() {
            when(mockTemporaryStorage.isBoolean(TemporaryKey.AUTO_SELL_ENABLE)).thenReturn(true);

            assertTrue(autoSellSpecialMine.isWork(false));
        }

        @Test
        @DisplayName("Should return false when auto sell is disabled")
        void shouldReturnFalseWhenAutoSellDisabled() {
            when(mockTemporaryStorage.isBoolean(TemporaryKey.AUTO_SELL_ENABLE)).thenReturn(false);

            assertFalse(autoSellSpecialMine.isWork(false));
        }

        @Test
        @DisplayName("Should check AUTO_SELL_ENABLE key")
        void shouldCheckAutoSellEnableKey() {
            autoSellSpecialMine.isWork(false);

            verify(mockTemporaryStorage).isBoolean(TemporaryKey.AUTO_SELL_ENABLE);
        }

        @Test
        @DisplayName("Should work regardless of fake flag")
        void shouldWorkRegardlessOfFakeFlag() {
            when(mockTemporaryStorage.isBoolean(TemporaryKey.AUTO_SELL_ENABLE)).thenReturn(true);

            assertTrue(autoSellSpecialMine.isWork(true));
            assertTrue(autoSellSpecialMine.isWork(false));
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
            autoSellSpecialMine.doSpecialMine(data, false);
            autoSellSpecialMine.doSpecialMine(data, true);
        }
    }

    @Nested
    @DisplayName("Interaction with TemporaryStorage Tests")
    class TemporaryStorageInteractionTests {

        @Test
        @DisplayName("Should query CEPlayer for temporary storage")
        void shouldQueryCEPlayerForTemporaryStorage() {
            when(mockTemporaryStorage.isBoolean(anyString())).thenReturn(false);

            autoSellSpecialMine.isWork(false);

            verify(mockCEPlayer).getTemporaryStorage();
        }

        @Test
        @DisplayName("Should use correct temporary key constant")
        void shouldUseCorrectTemporaryKeyConstant() {
            assertEquals("auto_sell_enable", TemporaryKey.AUTO_SELL_ENABLE);
        }
    }

    @Nested
    @DisplayName("Priority Order Tests")
    class PriorityOrderTests {

        @Test
        @DisplayName("AutoSell should run after BlockDropBonus (priority 5)")
        void autoSellShouldRunAfterBlockDropBonus() {
            // BlockDropBonusSpecialMine has priority 5
            // AutoSellSpecialMine has priority 10
            // Higher priority runs later
            assertTrue(autoSellSpecialMine.getPriority() > 5);
        }

        @Test
        @DisplayName("AutoSell should run before Telepathy (priority 20)")
        void autoSellShouldRunBeforeTelepathy() {
            // TelepathySpecialMine has priority 20
            // AutoSellSpecialMine has priority 10
            assertTrue(autoSellSpecialMine.getPriority() < 20);
        }
    }
}
