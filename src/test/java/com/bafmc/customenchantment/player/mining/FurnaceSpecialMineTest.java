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
@ExtendWith(MockitoExtension.class)
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
        lenient().when(mockPlayerSpecialMining.getCEPlayer()).thenReturn(mockCEPlayer);
        lenient().when(mockPlayerSpecialMining.getPlayer()).thenReturn(mockPlayer);
        furnaceSpecialMine = new FurnaceSpecialMine(mockPlayerSpecialMining);
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

            @Test
            @DisplayName("Should convert RAW_COPPER to COPPER_INGOT")
            void shouldConvertRawCopper() {
                List<ItemStack> items = new ArrayList<>();
                ItemStack rawCopper = new ItemStack(Material.RAW_COPPER, 1);
                items.add(rawCopper);

                List<ItemStack> result = miningFurnace.applyFurnace(items);

                assertEquals(1, result.size());
                assertEquals(Material.COPPER_INGOT, result.get(0).getType());
            }

            @Test
            @DisplayName("Should convert RAW_IRON to IRON_INGOT")
            void shouldConvertRawIron() {
                List<ItemStack> items = new ArrayList<>();
                ItemStack rawIron = new ItemStack(Material.RAW_IRON, 1);
                items.add(rawIron);

                List<ItemStack> result = miningFurnace.applyFurnace(items);

                assertEquals(1, result.size());
                assertEquals(Material.IRON_INGOT, result.get(0).getType());
            }

            @Test
            @DisplayName("Should convert RAW_GOLD to GOLD_INGOT")
            void shouldConvertRawGold() {
                List<ItemStack> items = new ArrayList<>();
                ItemStack rawGold = new ItemStack(Material.RAW_GOLD, 1);
                items.add(rawGold);

                List<ItemStack> result = miningFurnace.applyFurnace(items);

                assertEquals(1, result.size());
                assertEquals(Material.GOLD_INGOT, result.get(0).getType());
            }

            @Test
            @DisplayName("Should not modify non-smeltable items")
            void shouldNotModifyNonSmeltableItems() {
                List<ItemStack> items = new ArrayList<>();
                ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
                items.add(diamond);

                List<ItemStack> result = miningFurnace.applyFurnace(items);

                assertEquals(1, result.size());
                assertEquals(Material.DIAMOND, result.get(0).getType());
            }

            @Test
            @DisplayName("Should preserve item amounts after conversion")
            void shouldPreserveItemAmounts() {
                List<ItemStack> items = new ArrayList<>();
                ItemStack rawIron = new ItemStack(Material.RAW_IRON, 5);
                items.add(rawIron);

                List<ItemStack> result = miningFurnace.applyFurnace(items);

                assertEquals(1, result.size());
                assertEquals(Material.IRON_INGOT, result.get(0).getType());
                assertEquals(5, result.get(0).getAmount());
            }

            @Test
            @DisplayName("Should handle multiple items")
            void shouldHandleMultipleItems() {
                List<ItemStack> items = new ArrayList<>();
                items.add(new ItemStack(Material.RAW_COPPER, 2));
                items.add(new ItemStack(Material.RAW_IRON, 3));
                items.add(new ItemStack(Material.DIAMOND, 1));

                List<ItemStack> result = miningFurnace.applyFurnace(items);

                assertEquals(3, result.size());
                assertEquals(Material.COPPER_INGOT, result.get(0).getType());
                assertEquals(2, result.get(0).getAmount());
                assertEquals(Material.IRON_INGOT, result.get(1).getType());
                assertEquals(3, result.get(1).getAmount());
                assertEquals(Material.DIAMOND, result.get(2).getType());
                assertEquals(1, result.get(2).getAmount());
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
            List<ItemStack> drops = new ArrayList<>();
            drops.add(new ItemStack(Material.RAW_IRON, 1));

            SpecialMiningData data = mock(SpecialMiningData.class);

            List<ItemStack> result = furnaceSpecialMine.getDrops(data, drops, false);

            assertEquals(1, result.size());
            assertEquals(Material.IRON_INGOT, result.get(0).getType());
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
