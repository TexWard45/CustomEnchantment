package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for summon-related effects:
 * - EffectSummonGuard
 * - EffectSummonCustomGuard
 * - EffectSummonBabyZombieGuard
 * - EffectRemoveGuard
 */
@DisplayName("Summon Effects Tests")
class SummonEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectSummonGuard Tests")
    class EffectSummonGuardTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectSummonGuard effect = new EffectSummonGuard();
            assertEffectIdentifier(effect, "SUMMON_GUARD");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectSummonGuard effect = new EffectSummonGuard();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectSummonGuard effect = new EffectSummonGuard();
            String[] args = {"guardian", "ZOMBIE", "PLAYER", "0.5", "20.0", "10.0", "60000"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectSummonGuard effect = new EffectSummonGuard();
            String[] args = {"guardian", "ZOMBIE", "PLAYER", "0.5", "20.0", "10.0", "60000"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectSummonGuard effect = new EffectSummonGuard();
            String[] args = {"guardian", "ZOMBIE", "PLAYER", "0.5", "20.0", "10.0", "60000"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }
    }

    @Nested
    @DisplayName("EffectSummonCustomGuard Tests")
    class EffectSummonCustomGuardTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectSummonCustomGuard effect = new EffectSummonCustomGuard();
            assertEffectIdentifier(effect, "SUMMON_CUSTOM_GUARD");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectSummonCustomGuard effect = new EffectSummonCustomGuard();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectSummonCustomGuard effect = new EffectSummonCustomGuard();
            String[] args = {"custom_guard", "PLAYER", "0.5", "20.0", "10.0", "60000"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectSummonCustomGuard effect = new EffectSummonCustomGuard();
            String[] args = {"custom_guard", "PLAYER", "0.5", "20.0", "10.0", "60000"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectSummonBabyZombieGuard Tests")
    class EffectSummonBabyZombieGuardTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectSummonBabyZombieGuard effect = new EffectSummonBabyZombieGuard();
            assertEffectIdentifier(effect, "SUMMON_BABY_ZOMBIE_GUARD");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectSummonBabyZombieGuard effect = new EffectSummonBabyZombieGuard();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectSummonBabyZombieGuard effect = new EffectSummonBabyZombieGuard();
            String[] args = {"baby_zombie", "PLAYER", "0.5", "20.0", "10.0", "60000"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectRemoveGuard Tests")
    class EffectRemoveGuardTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveGuard effect = new EffectRemoveGuard();
            assertEffectIdentifier(effect, "REMOVE_GUARD");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectRemoveGuard effect = new EffectRemoveGuard();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveGuard effect = new EffectRemoveGuard();
            String[] args = {"guardian"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectRemoveGuard effect = new EffectRemoveGuard();
            String[] args = {"guardian"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }
}
