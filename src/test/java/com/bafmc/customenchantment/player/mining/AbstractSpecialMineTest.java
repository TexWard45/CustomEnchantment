package com.bafmc.customenchantment.player.mining;

import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
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

@DisplayName("AbstractSpecialMine")
@ExtendWith(MockitoExtension.class)
class AbstractSpecialMineTest {

    @Mock
    private PlayerSpecialMining mockPlayerSpecialMining;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private Player mockPlayer;

    // Concrete implementation for testing abstract class
    private static class TestSpecialMine extends AbstractSpecialMine {
        private Boolean workResult = true;
        private List<ItemStack> dropResult;

        public TestSpecialMine(PlayerSpecialMining playerSpecialMining) {
            super(playerSpecialMining);
        }

        public void setWorkResult(Boolean result) {
            this.workResult = result;
        }

        public void setDropResult(List<ItemStack> result) {
            this.dropResult = result;
        }

        @Override
        public Boolean isWork(boolean fake) {
            return workResult;
        }

        @Override
        public List<ItemStack> getDrops(SpecialMiningData specialMiningData, List<ItemStack> drops, boolean fake) {
            return dropResult != null ? dropResult : drops;
        }

        @Override
        public void doSpecialMine(SpecialMiningData data, boolean fake) {
            // Test implementation - do nothing
        }
    }

    private TestSpecialMine testSpecialMine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lenient().when(mockPlayerSpecialMining.getCEPlayer()).thenReturn(mockCEPlayer);
        lenient().when(mockPlayerSpecialMining.getPlayer()).thenReturn(mockPlayer);
        testSpecialMine = new TestSpecialMine(mockPlayerSpecialMining);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with PlayerSpecialMining")
        void shouldCreateInstance() {
            assertNotNull(testSpecialMine);
        }

        @Test
        @DisplayName("Should store PlayerSpecialMining reference")
        void shouldStorePlayerSpecialMiningReference() {
            assertEquals(mockPlayerSpecialMining, testSpecialMine.getPlayerSpecialMining());
        }

        @Test
        @DisplayName("Should handle null PlayerSpecialMining")
        void shouldHandleNullPlayerSpecialMining() {
            TestSpecialMine nullMine = new TestSpecialMine(null);
            assertNull(nullMine.getPlayerSpecialMining());
        }
    }

    @Nested
    @DisplayName("getPriority Tests")
    class GetPriorityTests {

        @Test
        @DisplayName("Should return default priority of 0")
        void shouldReturnDefaultPriorityOfZero() {
            assertEquals(0, testSpecialMine.getPriority());
        }
    }

    @Nested
    @DisplayName("getPlayerSpecialMining Tests")
    class GetPlayerSpecialMiningTests {

        @Test
        @DisplayName("Should return PlayerSpecialMining instance")
        void shouldReturnPlayerSpecialMiningInstance() {
            assertSame(mockPlayerSpecialMining, testSpecialMine.getPlayerSpecialMining());
        }
    }

    @Nested
    @DisplayName("Abstract Method Contract Tests")
    class AbstractMethodContractTests {

        @Test
        @DisplayName("isWork should be overridable")
        void isWorkShouldBeOverridable() {
            testSpecialMine.setWorkResult(true);
            assertTrue(testSpecialMine.isWork(false));

            testSpecialMine.setWorkResult(false);
            assertFalse(testSpecialMine.isWork(false));

            testSpecialMine.setWorkResult(null);
            assertNull(testSpecialMine.isWork(false));
        }

        @Test
        @DisplayName("getDrops should be overridable")
        void getDropsShouldBeOverridable() {
            List<ItemStack> customDrops = new ArrayList<>();
            ItemStack mockItem = mock(ItemStack.class);
            customDrops.add(mockItem);

            testSpecialMine.setDropResult(customDrops);

            List<ItemStack> originalDrops = new ArrayList<>();
            SpecialMiningData data = mock(SpecialMiningData.class);

            List<ItemStack> result = testSpecialMine.getDrops(data, originalDrops, false);

            assertEquals(customDrops, result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("doSpecialMine should be callable")
        void doSpecialMineShouldBeCallable() {
            SpecialMiningData data = mock(SpecialMiningData.class);
            // Should not throw exception
            testSpecialMine.doSpecialMine(data, false);
            testSpecialMine.doSpecialMine(data, true);
        }
    }

    @Nested
    @DisplayName("Fake Flag Handling Tests")
    class FakeFlagHandlingTests {

        @Test
        @DisplayName("isWork should receive fake flag")
        void isWorkShouldReceiveFakeFlag() {
            // Just verifying the method signature accepts the fake flag
            testSpecialMine.isWork(true);
            testSpecialMine.isWork(false);
        }

        @Test
        @DisplayName("getDrops should receive fake flag")
        void getDropsShouldReceiveFakeFlag() {
            List<ItemStack> drops = new ArrayList<>();
            SpecialMiningData data = mock(SpecialMiningData.class);

            // Just verifying the method signature accepts the fake flag
            testSpecialMine.getDrops(data, drops, true);
            testSpecialMine.getDrops(data, drops, false);
        }

        @Test
        @DisplayName("doSpecialMine should receive fake flag")
        void doSpecialMineShouldReceiveFakeFlag() {
            SpecialMiningData data = mock(SpecialMiningData.class);

            // Just verifying the method signature accepts the fake flag
            testSpecialMine.doSpecialMine(data, true);
            testSpecialMine.doSpecialMine(data, false);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should allow chaining from PlayerSpecialMining to CEPlayer")
        void shouldAllowChainingFromPlayerSpecialMiningToCEPlayer() {
            CEPlayer cePlayer = testSpecialMine.getPlayerSpecialMining().getCEPlayer();
            assertSame(mockCEPlayer, cePlayer);
        }

        @Test
        @DisplayName("Should allow chaining from PlayerSpecialMining to Player")
        void shouldAllowChainingFromPlayerSpecialMiningToPlayer() {
            Player player = testSpecialMine.getPlayerSpecialMining().getPlayer();
            assertSame(mockPlayer, player);
        }
    }

    @Nested
    @DisplayName("Concrete Implementation Priority Tests")
    class ConcreteImplementationPriorityTests {

        @Test
        @DisplayName("Verify priority order: Furnace(0) < BlockDropBonus(5) < AutoSell(10) < Telepathy(20)")
        void verifyPriorityOrder() {
            FurnaceSpecialMine furnace = new FurnaceSpecialMine(mockPlayerSpecialMining);
            BlockDropBonusSpecialMine blockDropBonus = new BlockDropBonusSpecialMine(mockPlayerSpecialMining);
            AutoSellSpecialMine autoSell = new AutoSellSpecialMine(mockPlayerSpecialMining);
            TelepathySpecialMine telepathy = new TelepathySpecialMine(mockPlayerSpecialMining);

            assertTrue(furnace.getPriority() < blockDropBonus.getPriority());
            assertTrue(blockDropBonus.getPriority() < autoSell.getPriority());
            assertTrue(autoSell.getPriority() < telepathy.getPriority());
        }

        @Test
        @DisplayName("Explosion and Vein have default priority")
        void explosionAndVeinHaveDefaultPriority() {
            ExplosionSpecialMine explosion = new ExplosionSpecialMine(mockPlayerSpecialMining);
            VeinSpecialMine vein = new VeinSpecialMine(mockPlayerSpecialMining);

            assertEquals(0, explosion.getPriority());
            assertEquals(0, vein.getPriority());
        }
    }
}
