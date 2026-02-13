package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for potion-related effects:
 * - EffectAddPotion
 * - EffectRemovePotion
 * - EffectAddRandomPotion
 * - EffectRemoveRandomPotion
 * - EffectAddForeverPotion
 * - EffectRemoveForeverPotion
 * - EffectBlockForeverPotion
 * - EffectUnblockForeverPotion
 */
@DisplayName("Potion Effects Tests")
class PotionEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectAddPotion Tests")
    class EffectAddPotionTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddPotion effect = new EffectAddPotion();
            assertEffectIdentifier(effect, "ADD_POTION");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectAddPotion effect = new EffectAddPotion();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddPotion effect = new EffectAddPotion();
            String[] args = {"SPEED", "1-2", "100-200"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectAddPotion effect = new EffectAddPotion();
            String[] args = {"SPEED", "1", "100"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectAddPotion effect = new EffectAddPotion();
            String[] args = {"SPEED", "1", "100"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }

        @Test
        @DisplayName("should handle multiple potions")
        void shouldHandleMultiplePotions() {
            EffectAddPotion effect = new EffectAddPotion();
            String[] args = {"SPEED,STRENGTH", "1-2", "100-200"};
            assertSetupSucceeds(effect, args);

            assertExecuteHandlesNullPlayer(effect);
        }
    }

    @Nested
    @DisplayName("EffectRemovePotion Tests")
    class EffectRemovePotionTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemovePotion effect = new EffectRemovePotion();
            assertEffectIdentifier(effect, "REMOVE_POTION");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectRemovePotion effect = new EffectRemovePotion();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemovePotion effect = new EffectRemovePotion();
            String[] args = {"SPEED"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectRemovePotion effect = new EffectRemovePotion();
            String[] args = {"SPEED"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectAddRandomPotion Tests")
    class EffectAddRandomPotionTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddRandomPotion effect = new EffectAddRandomPotion();
            assertEffectIdentifier(effect, "ADD_RANDOM_POTION");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddRandomPotion effect = new EffectAddRandomPotion();
            String[] args = {"SPEED,STRENGTH,HASTE", "1-2", "100-200"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectAddRandomPotion effect = new EffectAddRandomPotion();
            String[] args = {"SPEED,STRENGTH", "1", "100"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectRemoveRandomPotion Tests")
    class EffectRemoveRandomPotionTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveRandomPotion effect = new EffectRemoveRandomPotion();
            assertEffectIdentifier(effect, "REMOVE_RANDOM_POTION");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveRandomPotion effect = new EffectRemoveRandomPotion();
            String[] args = {"SPEED,STRENGTH"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectAddForeverPotion Tests")
    class EffectAddForeverPotionTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddForeverPotion effect = new EffectAddForeverPotion();
            assertEffectIdentifier(effect, "ADD_FOREVER_POTION");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddForeverPotion effect = new EffectAddForeverPotion();
            String[] args = {"SPEED", "1"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectRemoveForeverPotion Tests")
    class EffectRemoveForeverPotionTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveForeverPotion effect = new EffectRemoveForeverPotion();
            assertEffectIdentifier(effect, "REMOVE_FOREVER_POTION");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveForeverPotion effect = new EffectRemoveForeverPotion();
            String[] args = {"SPEED"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectBlockForeverPotion Tests")
    class EffectBlockForeverPotionTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectBlockForeverPotion effect = new EffectBlockForeverPotion();
            assertEffectIdentifier(effect, "BLOCK_FOREVER_POTION");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectBlockForeverPotion effect = new EffectBlockForeverPotion();
            String[] args = {"SPEED"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectUnblockForeverPotion Tests")
    class EffectUnblockForeverPotionTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectUnblockForeverPotion effect = new EffectUnblockForeverPotion();
            assertEffectIdentifier(effect, "UNBLOCK_FOREVER_POTION");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectUnblockForeverPotion effect = new EffectUnblockForeverPotion();
            String[] args = {"SPEED"};
            assertSetupSucceeds(effect, args);
        }
    }
}
