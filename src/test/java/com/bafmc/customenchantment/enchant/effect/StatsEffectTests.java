package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for player stats-related effects:
 * - EffectHealth
 * - EffectFood
 * - EffectOxygen
 * - EffectExp
 * - EffectAbsorptionHeart
 */
@DisplayName("Stats Effects Tests")
class StatsEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectHealth Tests")
    class EffectHealthTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectHealth effect = new EffectHealth();
            assertEffectIdentifier(effect, "HEALTH");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectHealth effect = new EffectHealth();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectHealth effect = new EffectHealth();
            // args: modifyType, format
            String[] args = {"ADD", "5-10"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectHealth effect = new EffectHealth();
            String[] args = {"ADD", "5"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectHealth effect = new EffectHealth();
            String[] args = {"ADD", "5"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }
    }

    @Nested
    @DisplayName("EffectFood Tests")
    class EffectFoodTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectFood effect = new EffectFood();
            assertEffectIdentifier(effect, "FOOD");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectFood effect = new EffectFood();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectFood effect = new EffectFood();
            // args: modifyType, format
            String[] args = {"ADD", "3-5"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectFood effect = new EffectFood();
            String[] args = {"ADD", "3"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectOxygen Tests")
    class EffectOxygenTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectOxygen effect = new EffectOxygen();
            assertEffectIdentifier(effect, "OXYGEN");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectOxygen effect = new EffectOxygen();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectOxygen effect = new EffectOxygen();
            // args: modifyType, format
            String[] args = {"ADD", "10-20"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectOxygen effect = new EffectOxygen();
            String[] args = {"ADD", "10"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectExp Tests")
    class EffectExpTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectExp effect = new EffectExp();
            assertEffectIdentifier(effect, "EXP");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectExp effect = new EffectExp();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectExp effect = new EffectExp();
            // args: modifyType, format
            String[] args = {"ADD", "100-200"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectExp effect = new EffectExp();
            String[] args = {"ADD", "100"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectAbsorptionHeart Tests")
    class EffectAbsorptionHeartTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAbsorptionHeart effect = new EffectAbsorptionHeart();
            assertEffectIdentifier(effect, "ABSORPTION_HEART");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectAbsorptionHeart effect = new EffectAbsorptionHeart();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAbsorptionHeart effect = new EffectAbsorptionHeart();
            // args: modifyType, format, [limit]
            String[] args = {"ADD", "2.0-4.0"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectAbsorptionHeart effect = new EffectAbsorptionHeart();
            String[] args = {"ADD", "2.0"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }
}
