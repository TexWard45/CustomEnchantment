package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for damage-related effects:
 * - EffectDealDamage
 * - EffectTrueDamage
 */
@DisplayName("Damage Effects Tests")
class DamageEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectDealDamage Tests")
    class EffectDealDamageTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectDealDamage effect = new EffectDealDamage();
            assertEffectIdentifier(effect, "DEAL_DAMAGE");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectDealDamage effect = new EffectDealDamage();
            String[] args = {"5-10", "true", "true", "true", "true", "true"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectDealDamage effect = new EffectDealDamage();
            String[] args = {"5", "false", "false", "false", "false", "false"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectDealDamage effect = new EffectDealDamage();
            String[] args = {"5", "false", "false", "false", "false", "false"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }

        @Test
        @DisplayName("should handle different damage calculation options")
        void shouldHandleDifferentDamageCalculationOptions() {
            EffectDealDamage effect = new EffectDealDamage();
            String[] args = {"10", "true", "false", "true", "false", "true"};
            assertSetupSucceeds(effect, args);

            CEFunctionData data = createTestData(null);
            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectTrueDamage Tests")
    class EffectTrueDamageTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectTrueDamage effect = new EffectTrueDamage();
            assertEffectIdentifier(effect, "TRUE_DAMAGE");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectTrueDamage effect = new EffectTrueDamage();
            String[] args = {"5-10"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectTrueDamage effect = new EffectTrueDamage();
            String[] args = {"5"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectTrueDamage effect = new EffectTrueDamage();
            String[] args = {"5"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }
    }
}
