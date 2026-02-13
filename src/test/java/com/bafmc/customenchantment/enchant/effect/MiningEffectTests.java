package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for mining-related effects.
 */
@DisplayName("Mining Effects Tests")
class MiningEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectEnableTelepathy Tests")
    class EffectEnableTelepathyTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectEnableTelepathy effect = new EffectEnableTelepathy();
            assertEffectIdentifier(effect, "ENABLE_TELEPATHY");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectEnableTelepathy effect = new EffectEnableTelepathy();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectEnableTelepathy effect = new EffectEnableTelepathy();
            String[] args = {};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectDisableTelepathy Tests")
    class EffectDisableTelepathyTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectDisableTelepathy effect = new EffectDisableTelepathy();
            assertEffectIdentifier(effect, "DISABLE_TELEPATHY");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectDisableTelepathy effect = new EffectDisableTelepathy();
            assertTrue(effect.isAsync());
        }
    }

    @Nested
    @DisplayName("EffectEnableAutoSell Tests")
    class EffectEnableAutoSellTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectEnableAutoSell effect = new EffectEnableAutoSell();
            assertEffectIdentifier(effect, "ENABLE_AUTO_SELL");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectEnableAutoSell effect = new EffectEnableAutoSell();
            assertTrue(effect.isAsync());
        }
    }

    @Nested
    @DisplayName("EffectDisableAutoSell Tests")
    class EffectDisableAutoSellTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectDisableAutoSell effect = new EffectDisableAutoSell();
            assertEffectIdentifier(effect, "DISABLE_AUTO_SELL");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectDisableAutoSell effect = new EffectDisableAutoSell();
            assertTrue(effect.isAsync());
        }
    }

    @Nested
    @DisplayName("EffectAddFurnaceMining Tests")
    class EffectAddFurnaceMiningTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddFurnaceMining effect = new EffectAddFurnaceMining();
            assertEffectIdentifier(effect, "ADD_FURNACE_MINING");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectAddFurnaceMining effect = new EffectAddFurnaceMining();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddFurnaceMining effect = new EffectAddFurnaceMining();
            // args: name, chance
            String[] args = {"furnace_mining", "100"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectRemoveFurnaceMining Tests")
    class EffectRemoveFurnaceMiningTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveFurnaceMining effect = new EffectRemoveFurnaceMining();
            assertEffectIdentifier(effect, "REMOVE_FURNACE_MINING");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectRemoveFurnaceMining effect = new EffectRemoveFurnaceMining();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveFurnaceMining effect = new EffectRemoveFurnaceMining();
            String[] args = {"furnace_mining"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectAddVeinMining Tests")
    class EffectAddVeinMiningTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddVeinMining effect = new EffectAddVeinMining();
            assertEffectIdentifier(effect, "ADD_VEIN_MINING");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectAddVeinMining effect = new EffectAddVeinMining();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddVeinMining effect = new EffectAddVeinMining();
            // args: name, radius, chance, materialList
            String[] args = {"vein_mining", "3", "100.0", "STONE"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectRemoveVeinMining Tests")
    class EffectRemoveVeinMiningTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveVeinMining effect = new EffectRemoveVeinMining();
            assertEffectIdentifier(effect, "REMOVE_VEIN_MINING");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectRemoveVeinMining effect = new EffectRemoveVeinMining();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveVeinMining effect = new EffectRemoveVeinMining();
            String[] args = {"vein_mining"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectAddExplosionMining Tests")
    class EffectAddExplosionMiningTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddExplosionMining effect = new EffectAddExplosionMining();
            assertEffectIdentifier(effect, "ADD_EXPLOSION_MINING");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectAddExplosionMining effect = new EffectAddExplosionMining();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddExplosionMining effect = new EffectAddExplosionMining();
            // args: name, radius, chance, breakBlock
            String[] args = {"explosion_mining", "3", "100.0", "false"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectRemoveExplosionMining Tests")
    class EffectRemoveExplosionMiningTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveExplosionMining effect = new EffectRemoveExplosionMining();
            assertEffectIdentifier(effect, "REMOVE_EXPLOSION_MINING");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectRemoveExplosionMining effect = new EffectRemoveExplosionMining();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveExplosionMining effect = new EffectRemoveExplosionMining();
            String[] args = {"explosion_mining"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectAddBlockDropBonusMining Tests")
    class EffectAddBlockDropBonusMiningTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddBlockDropBonusMining effect = new EffectAddBlockDropBonusMining();
            assertEffectIdentifier(effect, "ADD_BLOCK_DROP_BONUS_MINING");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectAddBlockDropBonusMining effect = new EffectAddBlockDropBonusMining();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddBlockDropBonusMining effect = new EffectAddBlockDropBonusMining();
            // args: name, type (BonusType: ALL, ONE, NONE), require, reward, amount (format: "chance amount"), removeItem, [chance]
            String[] args = {"block_drop_bonus", "ALL", "STONE", "DIAMOND", "100 1", "false"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectRemoveBlockDropBonusMining Tests")
    class EffectRemoveBlockDropBonusMiningTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveBlockDropBonusMining effect = new EffectRemoveBlockDropBonusMining();
            assertEffectIdentifier(effect, "REMOVE_BLOCK_DROP_BONUS_MINING");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectRemoveBlockDropBonusMining effect = new EffectRemoveBlockDropBonusMining();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveBlockDropBonusMining effect = new EffectRemoveBlockDropBonusMining();
            String[] args = {"block_drop_bonus"};
            assertSetupSucceeds(effect, args);
        }
    }
}
