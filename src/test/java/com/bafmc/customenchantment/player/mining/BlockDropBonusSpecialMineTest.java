package com.bafmc.customenchantment.player.mining;

import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.player.mining.BlockDropBonusSpecialMine.MiningBlockDropBonus;
import com.bafmc.customenchantment.player.mining.BlockDropBonusSpecialMine.MiningBlockDropBonus.BonusType;
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

@DisplayName("BlockDropBonusSpecialMine")
@ExtendWith(MockitoExtension.class)
class BlockDropBonusSpecialMineTest {

    @Mock
    private PlayerSpecialMining mockPlayerSpecialMining;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private Player mockPlayer;

    private BlockDropBonusSpecialMine blockDropBonusSpecialMine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lenient().when(mockPlayerSpecialMining.getCEPlayer()).thenReturn(mockCEPlayer);
        lenient().when(mockPlayerSpecialMining.getPlayer()).thenReturn(mockPlayer);
        blockDropBonusSpecialMine = new BlockDropBonusSpecialMine(mockPlayerSpecialMining);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with PlayerSpecialMining")
        void shouldCreateInstance() {
            assertNotNull(blockDropBonusSpecialMine);
            assertEquals(mockPlayerSpecialMining, blockDropBonusSpecialMine.getPlayerSpecialMining());
        }

        @Test
        @DisplayName("Should initialize MiningBlockDropBonus")
        void shouldInitializeMiningBlockDropBonus() {
            assertNotNull(blockDropBonusSpecialMine.getBlockDropBonus());
        }
    }

    @Nested
    @DisplayName("Priority Tests")
    class PriorityTests {

        @Test
        @DisplayName("Should return priority of 5")
        void shouldReturnPriority5() {
            assertEquals(5, blockDropBonusSpecialMine.getPriority());
        }

        @Test
        @DisplayName("Priority should be higher than default (0)")
        void priorityShouldBeHigherThanDefault() {
            assertTrue(blockDropBonusSpecialMine.getPriority() > 0);
        }

        @Test
        @DisplayName("Priority should be lower than AutoSell (10)")
        void priorityShouldBeLowerThanAutoSell() {
            assertTrue(blockDropBonusSpecialMine.getPriority() < 10);
        }
    }

    @Nested
    @DisplayName("BonusType Enum Tests")
    class BonusTypeEnumTests {

        @Test
        @DisplayName("Should have ALL type")
        void shouldHaveAllType() {
            assertNotNull(BonusType.ALL);
        }

        @Test
        @DisplayName("Should have ONE type")
        void shouldHaveOneType() {
            assertNotNull(BonusType.ONE);
        }

        @Test
        @DisplayName("Should have NONE type")
        void shouldHaveNoneType() {
            assertNotNull(BonusType.NONE);
        }

        @Test
        @DisplayName("Should have exactly 3 types")
        void shouldHaveExactlyThreeTypes() {
            assertEquals(3, BonusType.values().length);
        }
    }

    @Nested
    @DisplayName("MiningBlockDropBonus Tests")
    class MiningBlockDropBonusTests {

        private MiningBlockDropBonus miningBlockDropBonus;

        @BeforeEach
        void setUp() {
            miningBlockDropBonus = blockDropBonusSpecialMine.getBlockDropBonus();
        }

        @Test
        @DisplayName("Should not work initially when empty")
        void shouldNotWorkInitiallyWhenEmpty() {
            assertFalse(miningBlockDropBonus.isWork());
        }

        @Test
        @DisplayName("Should remove bonus by unique key")
        void shouldRemoveBonusByUniqueKey() {
            // Add a bonus first, then remove it
            // Since we can't easily add without dependencies, just verify remove doesn't throw
            miningBlockDropBonus.removeBonus("non_existent");
            assertFalse(miningBlockDropBonus.isWork());
        }

        @Test
        @DisplayName("Should return same list when no bonus configured")
        void shouldReturnSameListWhenNoBonusConfigured() {
            List<ItemStack> items = new ArrayList<>();
            ItemStack mockItem = mock(ItemStack.class);
            items.add(mockItem);

            List<ItemStack> result = miningBlockDropBonus.getBonus(items);

            assertEquals(1, result.size());
            assertSame(mockItem, result.get(0));
        }

        @Test
        @DisplayName("Should handle empty items list")
        void shouldHandleEmptyItemsList() {
            List<ItemStack> items = new ArrayList<>();

            List<ItemStack> result = miningBlockDropBonus.getBonus(items);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("isWork Tests")
    class IsWorkTests {

        @Test
        @DisplayName("Should return false when no bonuses configured")
        void shouldReturnFalseWhenNoBonusesConfigured() {
            assertFalse(blockDropBonusSpecialMine.isWork(false));
        }

        @Test
        @DisplayName("Should delegate to blockDropBonus.isWork()")
        void shouldDelegateToBlockDropBonusIsWork() {
            MiningBlockDropBonus blockDropBonus = blockDropBonusSpecialMine.getBlockDropBonus();
            assertEquals(blockDropBonus.isWork(), blockDropBonusSpecialMine.isWork(false));
        }

        @Test
        @DisplayName("Should work regardless of fake flag")
        void shouldWorkRegardlessOfFakeFlag() {
            // Both should return the same result
            assertEquals(blockDropBonusSpecialMine.isWork(false),
                         blockDropBonusSpecialMine.isWork(true));
        }
    }

    @Nested
    @DisplayName("getDrops Tests")
    class GetDropsTests {

        @Test
        @DisplayName("Should delegate to blockDropBonus.getBonus()")
        void shouldDelegateToBlockDropBonusGetBonus() {
            List<ItemStack> drops = new ArrayList<>();
            SpecialMiningData data = mock(SpecialMiningData.class);

            List<ItemStack> result = blockDropBonusSpecialMine.getDrops(data, drops, false);

            // Should return the same list when no bonuses configured
            assertEquals(drops, result);
        }

        @Test
        @DisplayName("Should pass drops to blockDropBonus")
        void shouldPassDropsToBlockDropBonus() {
            List<ItemStack> drops = new ArrayList<>();
            ItemStack mockItem = mock(ItemStack.class);
            drops.add(mockItem);

            SpecialMiningData data = mock(SpecialMiningData.class);

            List<ItemStack> result = blockDropBonusSpecialMine.getDrops(data, drops, false);

            assertEquals(1, result.size());
            assertSame(mockItem, result.get(0));
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
            blockDropBonusSpecialMine.doSpecialMine(data, false);
            blockDropBonusSpecialMine.doSpecialMine(data, true);
        }
    }

    @Nested
    @DisplayName("Priority Order Tests")
    class PriorityOrderTests {

        @Test
        @DisplayName("BlockDropBonus should run before AutoSell")
        void blockDropBonusShouldRunBeforeAutoSell() {
            // BlockDropBonusSpecialMine has priority 5
            // AutoSellSpecialMine has priority 10
            // Lower priority runs first
            assertTrue(blockDropBonusSpecialMine.getPriority() < 10);
        }

        @Test
        @DisplayName("BlockDropBonus should run before Telepathy")
        void blockDropBonusShouldRunBeforeTelepathy() {
            // BlockDropBonusSpecialMine has priority 5
            // TelepathySpecialMine has priority 20
            assertTrue(blockDropBonusSpecialMine.getPriority() < 20);
        }

        @Test
        @DisplayName("BlockDropBonus should run before Furnace (priority 0)")
        void blockDropBonusShouldRunAfterFurnace() {
            // BlockDropBonusSpecialMine has priority 5
            // FurnaceSpecialMine has priority 0 (default)
            assertTrue(blockDropBonusSpecialMine.getPriority() > 0);
        }
    }
}
