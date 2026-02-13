package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for bonus-related effects:
 * - EffectAddBlockBonus
 * - EffectRemoveBlockBonus
 * - EffectAddMobBonus
 * - EffectRemoveMobBonus
 */
@DisplayName("Bonus Effects Tests")
class BonusEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectAddBlockBonus Tests")
    class EffectAddBlockBonusTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddBlockBonus effect = new EffectAddBlockBonus();
            assertEffectIdentifier(effect, "ADD_BLOCK_BONUS");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectAddBlockBonus effect = new EffectAddBlockBonus();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddBlockBonus effect = new EffectAddBlockBonus();
            String[] args = {"STONE", "10-20"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectAddBlockBonus effect = new EffectAddBlockBonus();
            String[] args = {"STONE", "10"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectRemoveBlockBonus Tests")
    class EffectRemoveBlockBonusTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveBlockBonus effect = new EffectRemoveBlockBonus();
            assertEffectIdentifier(effect, "REMOVE_BLOCK_BONUS");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectRemoveBlockBonus effect = new EffectRemoveBlockBonus();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveBlockBonus effect = new EffectRemoveBlockBonus();
            String[] args = {"STONE"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectRemoveBlockBonus effect = new EffectRemoveBlockBonus();
            String[] args = {"STONE"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectAddMobBonus Tests")
    class EffectAddMobBonusTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddMobBonus effect = new EffectAddMobBonus();
            assertEffectIdentifier(effect, "ADD_MOB_BONUS");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectAddMobBonus effect = new EffectAddMobBonus();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddMobBonus effect = new EffectAddMobBonus();
            String[] args = {"ZOMBIE", "5-15"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectAddMobBonus effect = new EffectAddMobBonus();
            String[] args = {"ZOMBIE", "5"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectRemoveMobBonus Tests")
    class EffectRemoveMobBonusTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveMobBonus effect = new EffectRemoveMobBonus();
            assertEffectIdentifier(effect, "REMOVE_MOB_BONUS");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectRemoveMobBonus effect = new EffectRemoveMobBonus();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveMobBonus effect = new EffectRemoveMobBonus();
            String[] args = {"ZOMBIE"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectRemoveMobBonus effect = new EffectRemoveMobBonus();
            String[] args = {"ZOMBIE"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }
}
