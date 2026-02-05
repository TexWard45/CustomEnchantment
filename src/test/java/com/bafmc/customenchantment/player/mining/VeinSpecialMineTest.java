package com.bafmc.customenchantment.player.mining;

import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.player.mining.VeinSpecialMine.Vein;
import com.bafmc.customenchantment.player.mining.VeinSpecialMine.VeinMining;
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

@DisplayName("VeinSpecialMine")
@ExtendWith(MockitoExtension.class)
class VeinSpecialMineTest {

    @Mock
    private PlayerSpecialMining mockPlayerSpecialMining;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private Player mockPlayer;

    private VeinSpecialMine veinSpecialMine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lenient().when(mockPlayerSpecialMining.getCEPlayer()).thenReturn(mockCEPlayer);
        lenient().when(mockPlayerSpecialMining.getPlayer()).thenReturn(mockPlayer);
        veinSpecialMine = new VeinSpecialMine(mockPlayerSpecialMining);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with PlayerSpecialMining")
        void shouldCreateInstance() {
            assertNotNull(veinSpecialMine);
            assertEquals(mockPlayerSpecialMining, veinSpecialMine.getPlayerSpecialMining());
        }

        @Test
        @DisplayName("Should initialize VeinMining")
        void shouldInitializeVeinMining() {
            assertNotNull(veinSpecialMine.getVein());
        }
    }

    @Nested
    @DisplayName("Vein Inner Class Tests")
    class VeinInnerClassTests {

        @Test
        @DisplayName("Should create Vein with all parameters")
        void shouldCreateVein() {
            MaterialList whitelist = mock(MaterialList.class);
            Vein vein = new Vein(10, 75.0, whitelist);

            assertEquals(10, vein.getLength());
            assertEquals(75.0, vein.getChance());
            assertEquals(whitelist, vein.getWhitelist());
        }

        @Test
        @DisplayName("Should create Vein with null whitelist")
        void shouldCreateVeinWithNullWhitelist() {
            Vein vein = new Vein(5, 50.0, null);

            assertEquals(5, vein.getLength());
            assertEquals(50.0, vein.getChance());
            assertNull(vein.getWhitelist());
        }

        @Test
        @DisplayName("Should clone Vein correctly")
        void shouldCloneVein() {
            MaterialList whitelist = mock(MaterialList.class);
            Vein original = new Vein(15, 80.0, whitelist);

            Vein cloned = original.clone();

            assertEquals(original.getLength(), cloned.getLength());
            assertEquals(original.getChance(), cloned.getChance());
        }

        @Test
        @DisplayName("Should check isWork based on chance")
        void shouldCheckIsWorkBasedOnChance() {
            // 100% chance should always work
            Vein vein100 = new Vein(10, 100.0, null);
            assertTrue(vein100.isWork());

            // 0% chance should never work
            Vein vein0 = new Vein(10, 0.0, null);
            assertFalse(vein0.isWork());
        }
    }

    @Nested
    @DisplayName("VeinMining Tests")
    class VeinMiningTests {

        private VeinMining veinMining;

        @BeforeEach
        void setUp() {
            veinMining = veinSpecialMine.getVein();
        }

        @Test
        @DisplayName("Should not work initially")
        void shouldNotWorkInitially() {
            assertFalse(veinMining.isWork());
        }

        @Test
        @DisplayName("Should have null highest vein initially")
        void shouldHaveNullHighestVeinInitially() {
            assertNull(veinMining.getHighestVein());
        }

        @Test
        @DisplayName("Should add vein")
        void shouldAddVein() {
            Vein vein = new Vein(10, 100.0, null);
            veinMining.addVein("test_enchant", vein);

            assertNotNull(veinMining.getHighestVein());
            assertTrue(veinMining.isWork());
        }

        @Test
        @DisplayName("Should remove vein")
        void shouldRemoveVein() {
            Vein vein = new Vein(10, 100.0, null);
            veinMining.addVein("test_enchant", vein);
            assertTrue(veinMining.isWork());

            veinMining.removeVein("test_enchant");
            assertFalse(veinMining.isWork());
        }

        @Test
        @DisplayName("Should ignore remove for non-existent key")
        void shouldIgnoreRemoveForNonExistentKey() {
            veinMining.removeVein("non_existent");
            assertFalse(veinMining.isWork());
        }

        @Test
        @DisplayName("Should remove when negative chance is added")
        void shouldRemoveWhenNegativeChanceAdded() {
            Vein vein = new Vein(10, 100.0, null);
            veinMining.addVein("test_enchant", vein);
            assertTrue(veinMining.isWork());

            Vein negativeVein = new Vein(10, -1.0, null);
            veinMining.addVein("test_enchant", negativeVein);
            assertFalse(veinMining.isWork());
        }

        @Test
        @DisplayName("Should track highest length vein")
        void shouldTrackHighestLengthVein() {
            Vein shortVein = new Vein(5, 100.0, null);
            Vein longVein = new Vein(20, 100.0, null);

            veinMining.addVein("short", shortVein);
            veinMining.addVein("long", longVein);

            Vein highest = veinMining.getHighestVein();
            assertEquals(20, highest.getLength());
        }

        @Test
        @DisplayName("Should track highest chance vein")
        void shouldTrackHighestChanceVein() {
            Vein lowChance = new Vein(10, 25.0, null);
            Vein highChance = new Vein(10, 75.0, null);

            veinMining.addVein("low", lowChance);
            veinMining.addVein("high", highChance);

            Vein highest = veinMining.getHighestVein();
            assertEquals(75.0, highest.getChance());
        }

        @Test
        @DisplayName("Should update highest when top vein removed")
        void shouldUpdateHighestWhenTopRemoved() {
            Vein shortVein = new Vein(5, 100.0, null);
            Vein longVein = new Vein(20, 100.0, null);

            veinMining.addVein("short", shortVein);
            veinMining.addVein("long", longVein);

            veinMining.removeVein("long");

            Vein highest = veinMining.getHighestVein();
            assertEquals(5, highest.getLength());
        }

        @Test
        @DisplayName("Should preserve whitelist in highest vein")
        void shouldPreserveWhitelistInHighestVein() {
            MaterialList whitelist = mock(MaterialList.class);
            Vein vein = new Vein(10, 100.0, whitelist);
            veinMining.addVein("test", vein);

            Vein highest = veinMining.getHighestVein();
            assertEquals(whitelist, highest.getWhitelist());
        }

        @Test
        @DisplayName("Should set highest to null when all veins removed")
        void shouldSetHighestToNullWhenAllRemoved() {
            Vein vein1 = new Vein(5, 100.0, null);
            Vein vein2 = new Vein(10, 100.0, null);

            veinMining.addVein("vein1", vein1);
            veinMining.addVein("vein2", vein2);

            veinMining.removeVein("vein1");
            veinMining.removeVein("vein2");

            assertNull(veinMining.getHighestVein());
            assertFalse(veinMining.isWork());
        }
    }

    @Nested
    @DisplayName("isWork Tests")
    class IsWorkTests {

        @Test
        @DisplayName("Should return false when vein not working")
        void shouldReturnFalseWhenNotWorking() {
            assertFalse(veinSpecialMine.isWork(false));
        }

        @Test
        @DisplayName("Should return false when fake is true")
        void shouldReturnFalseWhenFakeIsTrue() {
            Vein vein = new Vein(10, 100.0, null);
            veinSpecialMine.getVein().addVein("test", vein);

            assertFalse(veinSpecialMine.isWork(true));
        }

        @Test
        @DisplayName("Should return true when working and not fake")
        void shouldReturnTrueWhenWorkingAndNotFake() {
            Vein vein = new Vein(10, 100.0, null);
            veinSpecialMine.getVein().addVein("test", vein);

            assertTrue(veinSpecialMine.isWork(false));
        }
    }

    @Nested
    @DisplayName("getDrops Tests")
    class GetDropsTests {

        @Test
        @DisplayName("Should return drops unchanged")
        void shouldReturnDropsUnchanged() {
            List<ItemStack> drops = new ArrayList<>();
            ItemStack mockItem = mock(ItemStack.class);
            drops.add(mockItem);

            SpecialMiningData data = mock(SpecialMiningData.class);

            List<ItemStack> result = veinSpecialMine.getDrops(data, drops, false);

            assertEquals(drops, result);
            assertEquals(1, result.size());
            assertSame(mockItem, result.get(0));
        }
    }

    @Nested
    @DisplayName("Priority Tests")
    class PriorityTests {

        @Test
        @DisplayName("Should return default priority of 0")
        void shouldReturnDefaultPriority() {
            assertEquals(0, veinSpecialMine.getPriority());
        }
    }
}
