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
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectAddBlockBonus effect = new EffectAddBlockBonus();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddBlockBonus effect = new EffectAddBlockBonus();
            // args: type, name, materialList, range, [operation], [chance]
            String[] args = {"EXP", "block_bonus", "STONE", "10-20"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectAddBlockBonus effect = new EffectAddBlockBonus();
            String[] args = {"EXP", "block_bonus", "STONE", "10"};
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
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectRemoveBlockBonus effect = new EffectRemoveBlockBonus();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveBlockBonus effect = new EffectRemoveBlockBonus();
            // args: type, name
            String[] args = {"EXP", "block_bonus"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectRemoveBlockBonus effect = new EffectRemoveBlockBonus();
            String[] args = {"EXP", "block_bonus"};
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
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectAddMobBonus effect = new EffectAddMobBonus();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddMobBonus effect = new EffectAddMobBonus();
            // args: type, name, entityTypeList, range, [operation], [chance]
            String[] args = {"EXP", "mob_bonus", "ZOMBIE", "5-15"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectAddMobBonus effect = new EffectAddMobBonus();
            String[] args = {"EXP", "mob_bonus", "ZOMBIE", "5"};
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
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectRemoveMobBonus effect = new EffectRemoveMobBonus();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveMobBonus effect = new EffectRemoveMobBonus();
            // args: type, name
            String[] args = {"EXP", "mob_bonus"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectRemoveMobBonus effect = new EffectRemoveMobBonus();
            String[] args = {"EXP", "mob_bonus"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }
}
