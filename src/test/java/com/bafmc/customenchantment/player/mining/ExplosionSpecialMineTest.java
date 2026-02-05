package com.bafmc.customenchantment.player.mining;

import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.player.CEPlayer;
import com.bafmc.customenchantment.player.PlayerSpecialMining;
import com.bafmc.customenchantment.player.mining.ExplosionSpecialMine.Explosion;
import com.bafmc.customenchantment.player.mining.ExplosionSpecialMine.MiningExplosion;
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

@DisplayName("ExplosionSpecialMine")
@ExtendWith(MockitoExtension.class)
class ExplosionSpecialMineTest {

    @Mock
    private PlayerSpecialMining mockPlayerSpecialMining;

    @Mock
    private CEPlayer mockCEPlayer;

    @Mock
    private Player mockPlayer;

    private ExplosionSpecialMine explosionSpecialMine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lenient().when(mockPlayerSpecialMining.getCEPlayer()).thenReturn(mockCEPlayer);
        lenient().when(mockPlayerSpecialMining.getPlayer()).thenReturn(mockPlayer);
        explosionSpecialMine = new ExplosionSpecialMine(mockPlayerSpecialMining);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with PlayerSpecialMining")
        void shouldCreateInstance() {
            assertNotNull(explosionSpecialMine);
            assertEquals(mockPlayerSpecialMining, explosionSpecialMine.getPlayerSpecialMining());
        }

        @Test
        @DisplayName("Should initialize MiningExplosion")
        void shouldInitializeMiningExplosion() {
            assertNotNull(explosionSpecialMine.getExplosion());
        }
    }

    @Nested
    @DisplayName("Explosion Inner Class Tests")
    class ExplosionInnerClassTests {

        @Test
        @DisplayName("Should create Explosion with all parameters")
        void shouldCreateExplosion() {
            MaterialList whitelist = mock(MaterialList.class);
            Explosion explosion = new Explosion(3, 50.0, true, false, whitelist);

            assertEquals(3, explosion.getAoe());
            assertEquals(50.0, explosion.getChance());
            assertTrue(explosion.isDrop());
            assertFalse(explosion.isFlat());
            assertEquals(whitelist, explosion.getWhitelist());
        }

        @Test
        @DisplayName("Should clone Explosion correctly")
        void shouldCloneExplosion() {
            MaterialList whitelist = mock(MaterialList.class);
            Explosion original = new Explosion(5, 75.0, false, true, whitelist);

            Explosion cloned = original.clone();

            assertEquals(original.getAoe(), cloned.getAoe());
            assertEquals(original.getChance(), cloned.getChance());
            assertEquals(original.isDrop(), cloned.isDrop());
            assertEquals(original.isFlat(), cloned.isFlat());
        }

        @Test
        @DisplayName("Should check isWork based on chance")
        void shouldCheckIsWorkBasedOnChance() {
            // 100% chance should always work
            Explosion explosion100 = new Explosion(3, 100.0, true, false, null);
            assertTrue(explosion100.isWork());

            // 0% chance should never work
            Explosion explosion0 = new Explosion(3, 0.0, true, false, null);
            assertFalse(explosion0.isWork());
        }

        @Test
        @DisplayName("Should handle flat explosion flag")
        void shouldHandleFlatExplosionFlag() {
            Explosion flatExplosion = new Explosion(5, 100.0, true, true, null);
            assertTrue(flatExplosion.isFlat());

            Explosion nonFlatExplosion = new Explosion(5, 100.0, true, false, null);
            assertFalse(nonFlatExplosion.isFlat());
        }
    }

    @Nested
    @DisplayName("MiningExplosion Tests")
    class MiningExplosionTests {

        private MiningExplosion miningExplosion;

        @BeforeEach
        void setUp() {
            miningExplosion = explosionSpecialMine.getExplosion();
        }

        @Test
        @DisplayName("Should not work initially")
        void shouldNotWorkInitially() {
            assertFalse(miningExplosion.isWork());
        }

        @Test
        @DisplayName("Should have null highest explosion initially")
        void shouldHaveNullHighestExplosionInitially() {
            assertNull(miningExplosion.getHighestExplosion());
        }

        @Test
        @DisplayName("Should add explosion")
        void shouldAddExplosion() {
            Explosion explosion = new Explosion(3, 100.0, true, false, null);
            miningExplosion.addExplosion("test_enchant", explosion);

            assertNotNull(miningExplosion.getHighestExplosion());
            assertTrue(miningExplosion.isWork());
        }

        @Test
        @DisplayName("Should remove explosion")
        void shouldRemoveExplosion() {
            Explosion explosion = new Explosion(3, 100.0, true, false, null);
            miningExplosion.addExplosion("test_enchant", explosion);
            assertTrue(miningExplosion.isWork());

            miningExplosion.removeExplosion("test_enchant");
            assertFalse(miningExplosion.isWork());
        }

        @Test
        @DisplayName("Should ignore remove for non-existent key")
        void shouldIgnoreRemoveForNonExistentKey() {
            miningExplosion.removeExplosion("non_existent");
            assertFalse(miningExplosion.isWork());
        }

        @Test
        @DisplayName("Should remove when negative chance is added")
        void shouldRemoveWhenNegativeChanceAdded() {
            Explosion explosion = new Explosion(3, 100.0, true, false, null);
            miningExplosion.addExplosion("test_enchant", explosion);
            assertTrue(miningExplosion.isWork());

            Explosion negativeExplosion = new Explosion(3, -1.0, true, false, null);
            miningExplosion.addExplosion("test_enchant", negativeExplosion);
            assertFalse(miningExplosion.isWork());
        }

        @Test
        @DisplayName("Should track highest AOE explosion")
        void shouldTrackHighestAOEExplosion() {
            Explosion smallExplosion = new Explosion(3, 100.0, true, false, null);
            Explosion largeExplosion = new Explosion(7, 100.0, false, true, null);

            miningExplosion.addExplosion("small", smallExplosion);
            miningExplosion.addExplosion("large", largeExplosion);

            Explosion highest = miningExplosion.getHighestExplosion();
            assertEquals(7, highest.getAoe());
            assertFalse(highest.isDrop());
            assertTrue(highest.isFlat());
        }

        @Test
        @DisplayName("Should track highest chance explosion")
        void shouldTrackHighestChanceExplosion() {
            Explosion lowChance = new Explosion(5, 25.0, true, false, null);
            Explosion highChance = new Explosion(5, 75.0, false, true, null);

            miningExplosion.addExplosion("low", lowChance);
            miningExplosion.addExplosion("high", highChance);

            Explosion highest = miningExplosion.getHighestExplosion();
            assertEquals(75.0, highest.getChance());
        }

        @Test
        @DisplayName("Should update highest when top explosion removed")
        void shouldUpdateHighestWhenTopRemoved() {
            Explosion smallExplosion = new Explosion(3, 100.0, true, false, null);
            Explosion largeExplosion = new Explosion(7, 100.0, false, true, null);

            miningExplosion.addExplosion("small", smallExplosion);
            miningExplosion.addExplosion("large", largeExplosion);

            miningExplosion.removeExplosion("large");

            Explosion highest = miningExplosion.getHighestExplosion();
            assertEquals(3, highest.getAoe());
        }

        @Test
        @DisplayName("Should preserve whitelist in highest explosion")
        void shouldPreserveWhitelistInHighestExplosion() {
            MaterialList whitelist = mock(MaterialList.class);
            Explosion explosion = new Explosion(5, 100.0, true, false, whitelist);
            miningExplosion.addExplosion("test", explosion);

            Explosion highest = miningExplosion.getHighestExplosion();
            assertEquals(whitelist, highest.getWhitelist());
        }
    }

    @Nested
    @DisplayName("isWork Tests")
    class IsWorkTests {

        @Test
        @DisplayName("Should return false when explosion not working")
        void shouldReturnFalseWhenNotWorking() {
            assertFalse(explosionSpecialMine.isWork(false));
        }

        @Test
        @DisplayName("Should return false when fake is true")
        void shouldReturnFalseWhenFakeIsTrue() {
            Explosion explosion = new Explosion(3, 100.0, true, false, null);
            explosionSpecialMine.getExplosion().addExplosion("test", explosion);

            assertFalse(explosionSpecialMine.isWork(true));
        }

        @Test
        @DisplayName("Should return true when working and not fake")
        void shouldReturnTrueWhenWorkingAndNotFake() {
            Explosion explosion = new Explosion(3, 100.0, true, false, null);
            explosionSpecialMine.getExplosion().addExplosion("test", explosion);

            assertTrue(explosionSpecialMine.isWork(false));
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

            List<ItemStack> result = explosionSpecialMine.getDrops(data, drops, false);

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
            assertEquals(0, explosionSpecialMine.getPriority());
        }
    }
}
