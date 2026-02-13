package com.bafmc.customenchantment.player;

import com.bafmc.customenchantment.player.mining.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PlayerSpecialMining")
@ExtendWith(MockitoExtension.class)
class PlayerSpecialMiningTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private CEPlayer mockCEPlayer;

    private PlayerSpecialMining playerSpecialMining;

    @BeforeEach
    void setUp() {
        lenient().when(mockCEPlayer.getPlayer()).thenReturn(mockPlayer);
        // Clear the register to avoid side effects from constructor calling setup()
        PlayerSpecialMiningRegister.list.clear();
        playerSpecialMining = new PlayerSpecialMining(mockCEPlayer);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with CEPlayer")
        void shouldCreateInstanceWithCEPlayer() {
            assertNotNull(playerSpecialMining);
            assertSame(mockCEPlayer, playerSpecialMining.getCEPlayer());
        }

        @Test
        @DisplayName("Should extend CEPlayerExpansion")
        void shouldExtendCEPlayerExpansion() {
            assertTrue(playerSpecialMining instanceof CEPlayerExpansion);
        }

        @Test
        @DisplayName("Should initialize with LinkedHashMap for map field")
        void shouldInitializeWithLinkedHashMap() throws Exception {
            Field field = PlayerSpecialMining.class.getDeclaredField("map");
            field.setAccessible(true);
            assertTrue(field.get(playerSpecialMining) instanceof LinkedHashMap);
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("onJoin should not throw")
        void onJoinShouldNotThrow() {
            assertDoesNotThrow(() -> playerSpecialMining.onJoin());
        }

        @Test
        @DisplayName("onQuit should not throw")
        void onQuitShouldNotThrow() {
            assertDoesNotThrow(() -> playerSpecialMining.onQuit());
        }
    }

    @Nested
    @DisplayName("FakeMiningData Tests")
    class FakeMiningDataTests {

        @Test
        @DisplayName("Should have no fake mining data initially")
        void shouldHaveNoFakeMiningDataInitially() {
            assertFalse(playerSpecialMining.hasFakeMiningData());
            assertNull(playerSpecialMining.getMiningData());
        }

        @Test
        @DisplayName("Should set and get fake mining data")
        void shouldSetAndGetFakeMiningData() {
            SpecialMiningData mockData = mock(SpecialMiningData.class);
            playerSpecialMining.setFakeMiningData(mockData);

            assertTrue(playerSpecialMining.hasFakeMiningData());
            assertSame(mockData, playerSpecialMining.getMiningData());
        }

        @Test
        @DisplayName("Should clear fake mining data with null")
        void shouldClearFakeMiningDataWithNull() {
            SpecialMiningData mockData = mock(SpecialMiningData.class);
            playerSpecialMining.setFakeMiningData(mockData);
            playerSpecialMining.setFakeMiningData(null);

            assertFalse(playerSpecialMining.hasFakeMiningData());
        }
    }

    @Nested
    @DisplayName("addExpantion Tests")
    class AddExpantionTests {

        @Test
        @DisplayName("Should add special mine expansion to internal map")
        @SuppressWarnings("unchecked")
        void shouldAddSpecialMineExpansionToMap() throws Exception {
            // Create a concrete mock that has a real class
            AbstractSpecialMine mockMine = mock(AbstractSpecialMine.class);

            playerSpecialMining.addExpantion(mockMine);

            // Verify via reflection that the map contains the entry
            Field field = PlayerSpecialMining.class.getDeclaredField("map");
            field.setAccessible(true);
            Map<Class<? extends AbstractSpecialMine>, AbstractSpecialMine> map =
                    (Map<Class<? extends AbstractSpecialMine>, AbstractSpecialMine>) field.get(playerSpecialMining);

            assertEquals(1, map.size());
            assertTrue(map.containsValue(mockMine));
        }
    }

    @Nested
    @DisplayName("getSpecialMine Tests")
    class GetSpecialMineTests {

        @Test
        @DisplayName("Should return null for non-registered mining type")
        void shouldReturnNullForNonRegistered() {
            assertNull(playerSpecialMining.getSpecialMine(BlockDropBonusSpecialMine.class));
        }

        @Test
        @DisplayName("Convenience getters should return null when not registered")
        void convenienceGettersShouldReturnNull() {
            assertNull(playerSpecialMining.getBlockDropBonusSpecialMine());
            assertNull(playerSpecialMining.getExplosionSpecialMine());
            assertNull(playerSpecialMining.getFurnaceSpecialMine());
            assertNull(playerSpecialMining.getTelepathySpecialMine());
            assertNull(playerSpecialMining.getAutoSellSpecialMine());
            assertNull(playerSpecialMining.getVeinSpecialMine());
        }
    }

    @Nested
    @DisplayName("optimizeItemStacks Tests")
    class OptimizeItemStacksTests {

        @Test
        @DisplayName("Should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            List<ItemStack> result = PlayerSpecialMining.optimizeItemStacks(new ArrayList<>());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should have optimizeItemStacks as static method")
        void shouldHaveOptimizeItemStacksAsStaticMethod() throws NoSuchMethodException {
            assertNotNull(PlayerSpecialMining.class.getMethod("optimizeItemStacks", List.class));
            assertTrue(java.lang.reflect.Modifier.isStatic(
                    PlayerSpecialMining.class.getMethod("optimizeItemStacks", List.class).getModifiers()));
        }

        @Test
        @DisplayName("Should accept List of ItemStack parameter")
        void shouldAcceptListOfItemStack() throws NoSuchMethodException {
            var method = PlayerSpecialMining.class.getMethod("optimizeItemStacks", List.class);
            assertEquals(List.class, method.getReturnType());
        }
    }

    @Nested
    @DisplayName("toItemStackList Tests")
    class ToItemStackListTests {

        @Test
        @DisplayName("Should convert Item list to ItemStack list")
        void shouldConvertItemListToItemStackList() {
            org.bukkit.entity.Item mockItem1 = mock(org.bukkit.entity.Item.class);
            org.bukkit.entity.Item mockItem2 = mock(org.bukkit.entity.Item.class);
            ItemStack mockStack1 = mock(ItemStack.class);
            ItemStack mockStack2 = mock(ItemStack.class);
            when(mockItem1.getItemStack()).thenReturn(mockStack1);
            when(mockItem2.getItemStack()).thenReturn(mockStack2);

            List<org.bukkit.entity.Item> items = List.of(mockItem1, mockItem2);
            List<ItemStack> result = playerSpecialMining.toItemStackList(items);

            assertEquals(2, result.size());
            assertSame(mockStack1, result.get(0));
            assertSame(mockStack2, result.get(1));
        }

        @Test
        @DisplayName("Should return empty list for empty input")
        void shouldReturnEmptyListForEmptyInput() {
            List<ItemStack> result = playerSpecialMining.toItemStackList(new ArrayList<>());
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Method Signature Tests")
    class MethodSignatureTests {

        @Test
        @DisplayName("Should have getWorkSpecialMining method")
        void shouldHaveGetWorkSpecialMiningMethod() throws NoSuchMethodException {
            assertNotNull(PlayerSpecialMining.class.getMethod("getWorkSpecialMining", boolean.class));
        }

        @Test
        @DisplayName("Should have callFakeBreakBlock method")
        void shouldHaveCallFakeBreakBlockMethod() throws NoSuchMethodException {
            assertNotNull(PlayerSpecialMining.class.getMethod("callFakeBreakBlock",
                    org.bukkit.block.Block.class, SpecialMiningData.class));
        }

        @Test
        @DisplayName("Should have getSpecialMine method")
        void shouldHaveGetSpecialMineMethod() throws NoSuchMethodException {
            assertNotNull(PlayerSpecialMining.class.getMethod("getSpecialMine", Class.class));
        }
    }
}
