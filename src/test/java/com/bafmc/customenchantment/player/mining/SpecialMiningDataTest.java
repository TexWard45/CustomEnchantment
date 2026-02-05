package com.bafmc.customenchantment.player.mining;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SpecialMiningData")
class SpecialMiningDataTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private Block mockBlock;

    @Mock
    private ItemStack mockItemStack;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with 3-arg constructor")
        void shouldCreateWithThreeArgConstructor() {
            SpecialMiningData data = new SpecialMiningData(mockPlayer, mockBlock, mockItemStack);

            assertNotNull(data);
            assertEquals(mockPlayer, data.getPlayer());
            assertEquals(mockBlock, data.getBlock());
            assertEquals(mockItemStack, data.getItemStack());
            assertNotNull(data.getWorkMap());
            assertTrue(data.getWorkMap().isEmpty());
            assertFalse(data.isDropItem());
        }

        @Test
        @DisplayName("Should create instance with 5-arg constructor")
        void shouldCreateWithFiveArgConstructor() {
            List<Class<? extends AbstractSpecialMine>> workMap = new ArrayList<>();
            workMap.add(FurnaceSpecialMine.class);

            SpecialMiningData data = new SpecialMiningData(
                    mockPlayer, mockBlock, mockItemStack, workMap, true);

            assertNotNull(data);
            assertEquals(mockPlayer, data.getPlayer());
            assertEquals(mockBlock, data.getBlock());
            assertEquals(mockItemStack, data.getItemStack());
            assertEquals(workMap, data.getWorkMap());
            assertTrue(data.isDropItem());
        }

        @Test
        @DisplayName("Should handle null player")
        void shouldHandleNullPlayer() {
            SpecialMiningData data = new SpecialMiningData(null, mockBlock, mockItemStack);
            assertNull(data.getPlayer());
        }

        @Test
        @DisplayName("Should handle null block")
        void shouldHandleNullBlock() {
            SpecialMiningData data = new SpecialMiningData(mockPlayer, null, mockItemStack);
            assertNull(data.getBlock());
        }

        @Test
        @DisplayName("Should handle null itemStack")
        void shouldHandleNullItemStack() {
            SpecialMiningData data = new SpecialMiningData(mockPlayer, mockBlock, null);
            assertNull(data.getItemStack());
        }
    }

    @Nested
    @DisplayName("Getter/Setter Tests")
    class GetterSetterTests {

        private SpecialMiningData data;

        @BeforeEach
        void setUp() {
            data = new SpecialMiningData(mockPlayer, mockBlock, mockItemStack);
        }

        @Test
        @DisplayName("Should set and get player")
        void shouldSetAndGetPlayer() {
            Player newPlayer = mock(Player.class);
            data.setPlayer(newPlayer);
            assertEquals(newPlayer, data.getPlayer());
        }

        @Test
        @DisplayName("Should set and get block")
        void shouldSetAndGetBlock() {
            Block newBlock = mock(Block.class);
            data.setBlock(newBlock);
            assertEquals(newBlock, data.getBlock());
        }

        @Test
        @DisplayName("Should set and get itemStack")
        void shouldSetAndGetItemStack() {
            ItemStack newItemStack = mock(ItemStack.class);
            data.setItemStack(newItemStack);
            assertEquals(newItemStack, data.getItemStack());
        }

        @Test
        @DisplayName("Should set and get dropItem flag")
        void shouldSetAndGetDropItem() {
            assertFalse(data.isDropItem());
            data.setDropItem(true);
            assertTrue(data.isDropItem());
        }

        @Test
        @DisplayName("Should set and get workMap")
        void shouldSetAndGetWorkMap() {
            List<Class<? extends AbstractSpecialMine>> newWorkMap = new ArrayList<>();
            newWorkMap.add(VeinSpecialMine.class);
            newWorkMap.add(ExplosionSpecialMine.class);

            data.setWorkMap(newWorkMap);

            assertEquals(2, data.getWorkMap().size());
            assertTrue(data.getWorkMap().contains(VeinSpecialMine.class));
            assertTrue(data.getWorkMap().contains(ExplosionSpecialMine.class));
        }

        @Test
        @DisplayName("Should set and get drops")
        void shouldSetAndGetDrops() {
            List<ItemStack> drops = new ArrayList<>();
            ItemStack drop1 = mock(ItemStack.class);
            ItemStack drop2 = mock(ItemStack.class);
            drops.add(drop1);
            drops.add(drop2);

            data.setDrops(drops);

            assertEquals(2, data.getDrops().size());
            assertTrue(data.getDrops().contains(drop1));
            assertTrue(data.getDrops().contains(drop2));
        }

        @Test
        @DisplayName("Should set and get originalDrops")
        void shouldSetAndGetOriginalDrops() {
            List<ItemStack> originalDrops = new ArrayList<>();
            ItemStack drop1 = mock(ItemStack.class);
            originalDrops.add(drop1);

            data.setOriginalDrops(originalDrops);

            assertEquals(1, data.getOriginalDrops().size());
            assertTrue(data.getOriginalDrops().contains(drop1));
        }

        @Test
        @DisplayName("Should have empty drops list by default")
        void shouldHaveEmptyDropsListByDefault() {
            assertNotNull(data.getDrops());
            assertTrue(data.getDrops().isEmpty());
        }

        @Test
        @DisplayName("Should have empty originalDrops list by default")
        void shouldHaveEmptyOriginalDropsListByDefault() {
            assertNotNull(data.getOriginalDrops());
            assertTrue(data.getOriginalDrops().isEmpty());
        }
    }

    @Nested
    @DisplayName("WorkMap Tests")
    class WorkMapTests {

        @Test
        @DisplayName("Should allow adding to workMap")
        void shouldAllowAddingToWorkMap() {
            SpecialMiningData data = new SpecialMiningData(mockPlayer, mockBlock, mockItemStack);

            data.getWorkMap().add(FurnaceSpecialMine.class);
            data.getWorkMap().add(TelepathySpecialMine.class);

            assertEquals(2, data.getWorkMap().size());
        }

        @Test
        @DisplayName("Should preserve workMap order")
        void shouldPreserveWorkMapOrder() {
            List<Class<? extends AbstractSpecialMine>> workMap = new ArrayList<>();
            workMap.add(BlockDropBonusSpecialMine.class);
            workMap.add(FurnaceSpecialMine.class);
            workMap.add(AutoSellSpecialMine.class);

            SpecialMiningData data = new SpecialMiningData(
                    mockPlayer, mockBlock, mockItemStack, workMap, true);

            assertEquals(BlockDropBonusSpecialMine.class, data.getWorkMap().get(0));
            assertEquals(FurnaceSpecialMine.class, data.getWorkMap().get(1));
            assertEquals(AutoSellSpecialMine.class, data.getWorkMap().get(2));
        }
    }
}
