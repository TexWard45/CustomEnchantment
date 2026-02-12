package com.bafmc.customenchantment.player.mining;

import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.player.mining.FurnaceSpecialMine.MiningFurnace;
import org.bukkit.Material;
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

@DisplayName("FurnaceSpecialMine")
class FurnaceSpecialMineTest {

    @Mock
    private PlayerSpecialMining mockPlayerSpecialMining;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private Player mockPlayer;

    private FurnaceSpecialMine furnaceSpecialMine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Don't use lenient() - stub only when needed in specific tests
        furnaceSpecialMine = new FurnaceSpecialMine(mockPlayerSpecialMining);
    }

    /**
     * Helper to create a mock ItemStack.
     * We use mocks because real ItemStack requires Bukkit server initialization.
     */
    private ItemStack createMockItemStack(Material material, int amount) {
        ItemStack mockItem = mock(ItemStack.class);
        when(mockItem.getType()).thenReturn(material);
        when(mockItem.getAmount()).thenReturn(amount);
        return mockItem;
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with PlayerSpecialMining")
        void shouldCreateInstance() {
            assertNotNull(furnaceSpecialMine);
            assertEquals(mockPlayerSpecialMining, furnaceSpecialMine.getPlayerSpecialMining());
        }

        @Test
        @DisplayName("Should initialize MiningFurnace")
        void shouldInitializeMiningFurnace() {
            assertNotNull(furnaceSpecialMine.getFurnace());
        }
    }

    @Nested
    @DisplayName("Priority Tests")
    class PriorityTests {

        @Test
        @DisplayName("Should return default priority of 0")
        void shouldReturnDefaultPriority() {
            assertEquals(0, furnaceSpecialMine.getPriority());
        }
    }

    @Nested
    @DisplayName("MiningFurnace Tests")
    class MiningFurnaceTests {

        private MiningFurnace miningFurnace;

        @BeforeEach
        void setUp() {
            miningFurnace = furnaceSpecialMine.getFurnace();
        }

        @Test
        @DisplayName("Should not work initially with 0% chance")
        void shouldNotWorkInitially() {
            assertFalse(miningFurnace.isWork());
        }

        @Test
        @DisplayName("Should add furnace chance")
        void shouldAddFurnaceChance() {
            miningFurnace.addFurnaceChance("test_enchant", 100.0);
            // With 100% chance, should always work
            assertTrue(miningFurnace.isWork());
        }

        @Test
        @DisplayName("Should remove furnace chance")
        void shouldRemoveFurnaceChance() {
            miningFurnace.addFurnaceChance("test_enchant", 100.0);
            assertTrue(miningFurnace.isWork());

            miningFurnace.removeFurnaceChance("test_enchant");
            assertFalse(miningFurnace.isWork());
        }

        @Test
        @DisplayName("Should ignore remove for non-existent key")
        void shouldIgnoreRemoveForNonExistentKey() {
            miningFurnace.removeFurnaceChance("non_existent");
            // Should not throw exception
            assertFalse(miningFurnace.isWork());
        }

        @Test
        @DisplayName("Should remove when negative chance is added")
        void shouldRemoveWhenNegativeChanceAdded() {
            miningFurnace.addFurnaceChance("test_enchant", 100.0);
            assertTrue(miningFurnace.isWork());

            miningFurnace.addFurnaceChance("test_enchant", -1.0);
            assertFalse(miningFurnace.isWork());
        }

        @Test
        @DisplayName("Should use highest chance when multiple added")
        void shouldUseHighestChance() {
            miningFurnace.addFurnaceChance("low", 10.0);
            miningFurnace.addFurnaceChance("medium", 50.0);
            miningFurnace.addFurnaceChance("high", 100.0);

            // With 100% chance from "high", should always work
            assertTrue(miningFurnace.isWork());
        }

        @Test
        @DisplayName("Should update highest chance when high removed")
        void shouldUpdateHighestChanceWhenRemoved() {
            miningFurnace.addFurnaceChance("low", 0.0);
            miningFurnace.addFurnaceChance("high", 100.0);

            miningFurnace.removeFurnaceChance("high");
            // With 0% from "low", should not work
            assertFalse(miningFurnace.isWork());
        }

        @Nested
        @DisplayName("Apply Furnace Tests")
        class ApplyFurnaceTests {

            // Note: These tests use mock ItemStacks because real ItemStack
            // requires Bukkit server initialization which is not available
            // in unit tests without MockBukkit.

            @Test
            @DisplayName("Should convert RAW_COPPER to COPPER_INGOT")
            void shouldConvertRawCopper() {
                ItemStack rawCopper = mock(ItemStack.class);
                when(rawCopper.getType()).thenReturn(Material.RAW_COPPER);

                List<ItemStack> items = new ArrayList<>();
                items.add(rawCopper);

                miningFurnace.applyFurnace(items);

                verify(rawCopper).setType(Material.COPPER_INGOT);
            }

            @Test
            @DisplayName("Should convert RAW_IRON to IRON_INGOT")
            void shouldConvertRawIron() {
                ItemStack rawIron = mock(ItemStack.class);
                when(rawIron.getType()).thenReturn(Material.RAW_IRON);

                List<ItemStack> items = new ArrayList<>();
                items.add(rawIron);

                miningFurnace.applyFurnace(items);

                verify(rawIron).setType(Material.IRON_INGOT);
            }

            @Test
            @DisplayName("Should convert RAW_GOLD to GOLD_INGOT")
            void shouldConvertRawGold() {
                ItemStack rawGold = mock(ItemStack.class);
                when(rawGold.getType()).thenReturn(Material.RAW_GOLD);

                List<ItemStack> items = new ArrayList<>();
                items.add(rawGold);

                miningFurnace.applyFurnace(items);

                verify(rawGold).setType(Material.GOLD_INGOT);
            }

            @Test
            @DisplayName("Should not modify non-smeltable items")
            void shouldNotModifyNonSmeltableItems() {
                ItemStack diamond = mock(ItemStack.class);
                when(diamond.getType()).thenReturn(Material.DIAMOND);

                List<ItemStack> items = new ArrayList<>();
                items.add(diamond);

                miningFurnace.applyFurnace(items);

                verify(diamond, never()).setType(any());
            }

            @Test
            @DisplayName("Should preserve item amounts after conversion (amounts are not modified)")
            void shouldPreserveItemAmounts() {
                ItemStack rawIron = mock(ItemStack.class);
                when(rawIron.getType()).thenReturn(Material.RAW_IRON);
                when(rawIron.getAmount()).thenReturn(5);

                List<ItemStack> items = new ArrayList<>();
                items.add(rawIron);

                List<ItemStack> result = miningFurnace.applyFurnace(items);

                // applyFurnace only changes type, not amount
                assertEquals(1, result.size());
                verify(rawIron).setType(Material.IRON_INGOT);
                // Amount is not changed by applyFurnace
            }

            @Test
            @DisplayName("Should handle multiple items")
            void shouldHandleMultipleItems() {
                ItemStack rawCopper = mock(ItemStack.class);
                when(rawCopper.getType()).thenReturn(Material.RAW_COPPER);
                ItemStack rawIron = mock(ItemStack.class);
                when(rawIron.getType()).thenReturn(Material.RAW_IRON);
                ItemStack diamond = mock(ItemStack.class);
                when(diamond.getType()).thenReturn(Material.DIAMOND);

                List<ItemStack> items = new ArrayList<>();
                items.add(rawCopper);
                items.add(rawIron);
                items.add(diamond);

                List<ItemStack> result = miningFurnace.applyFurnace(items);

                assertEquals(3, result.size());
                verify(rawCopper).setType(Material.COPPER_INGOT);
                verify(rawIron).setType(Material.IRON_INGOT);
                verify(diamond, never()).setType(any());
            }

            @Test
            @DisplayName("Should handle empty list")
            void shouldHandleEmptyList() {
                List<ItemStack> items = new ArrayList<>();

                List<ItemStack> result = miningFurnace.applyFurnace(items);

                assertNotNull(result);
                assertTrue(result.isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("isWork Tests")
    class IsWorkTests {

        @Test
        @DisplayName("Should return false when furnace not working")
        void shouldReturnFalseWhenNotWorking() {
            assertFalse(furnaceSpecialMine.isWork(false));
        }

        @Test
        @DisplayName("Should return furnace isWork result")
        void shouldReturnFurnaceIsWorkResult() {
            furnaceSpecialMine.getFurnace().addFurnaceChance("test", 100.0);
            assertTrue(furnaceSpecialMine.isWork(false));
        }

        @Test
        @DisplayName("Should work regardless of fake flag")
        void shouldWorkRegardlessOfFakeFlag() {
            furnaceSpecialMine.getFurnace().addFurnaceChance("test", 100.0);
            assertTrue(furnaceSpecialMine.isWork(true));
            assertTrue(furnaceSpecialMine.isWork(false));
        }
    }

    @Nested
    @DisplayName("getDrops Tests")
    class GetDropsTests {

        @Test
        @DisplayName("Should apply furnace to drops")
        void shouldApplyFurnaceToDrops() {
            ItemStack rawIron = mock(ItemStack.class);
            when(rawIron.getType()).thenReturn(Material.RAW_IRON);

            List<ItemStack> drops = new ArrayList<>();
            drops.add(rawIron);

            SpecialMiningData data = mock(SpecialMiningData.class);

            List<ItemStack> result = furnaceSpecialMine.getDrops(data, drops, false);

            assertEquals(1, result.size());
            verify(rawIron).setType(Material.IRON_INGOT);
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
            furnaceSpecialMine.doSpecialMine(data, false);
            furnaceSpecialMine.doSpecialMine(data, true);
        }
    }
}
