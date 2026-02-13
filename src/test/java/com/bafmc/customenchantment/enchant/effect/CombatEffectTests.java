package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for combat-related effects:
 * - EffectLightning
 * - EffectExplosion
 * - EffectOnFire
 * - EffectPull
 * - EffectFixedPull
 * - EffectShootArrow
 * - EffectWindCharge
 * - EffectEnableMultipleArrow
 * - EffectDisableMultipleArrow
 */
@DisplayName("Combat Effects Tests")
class CombatEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectLightning Tests")
    class EffectLightningTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectLightning effect = new EffectLightning();
            assertEffectIdentifier(effect, "LIGHTNING");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectLightning effect = new EffectLightning();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectLightning effect = new EffectLightning();
            String[] args = {"PLAYER"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectLightning effect = new EffectLightning();
            String[] args = {"PLAYER"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectExplosion Tests")
    class EffectExplosionTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectExplosion effect = new EffectExplosion();
            assertEffectIdentifier(effect, "EXPLOSION");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectExplosion effect = new EffectExplosion();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectExplosion effect = new EffectExplosion();
            // args: format, power, setFire, breakBlock
            String[] args = {"PLAYER", "3.0", "false", "false"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectExplosion effect = new EffectExplosion();
            String[] args = {"PLAYER", "3.0", "false", "false"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectOnFire Tests")
    class EffectOnFireTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectOnFire effect = new EffectOnFire();
            assertEffectIdentifier(effect, "ON_FIRE");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectOnFire effect = new EffectOnFire();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectOnFire effect = new EffectOnFire();
            String[] args = {"10-20"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectOnFire effect = new EffectOnFire();
            String[] args = {"10"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectPull Tests")
    class EffectPullTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectPull effect = new EffectPull();
            assertEffectIdentifier(effect, "PULL");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectPull effect = new EffectPull();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectPull effect = new EffectPull();
            String[] args = {"1.0"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectFixedPull Tests")
    class EffectFixedPullTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectFixedPull effect = new EffectFixedPull();
            assertEffectIdentifier(effect, "FIXED_PULL");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectFixedPull effect = new EffectFixedPull();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectFixedPull effect = new EffectFixedPull();
            String[] args = {"1.0"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectShootArrow Tests")
    class EffectShootArrowTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectShootArrow effect = new EffectShootArrow();
            assertEffectIdentifier(effect, "SHOOT_ARROW");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectShootArrow effect = new EffectShootArrow();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectShootArrow effect = new EffectShootArrow();
            // args: spawnLocationFormat, destinationLocationFormat, speed, spread, activeCE
            String[] args = {"PLAYER", "ENEMY", "1.5", "0.1", "false"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectWindCharge Tests")
    class EffectWindChargeTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectWindCharge effect = new EffectWindCharge();
            assertEffectIdentifier(effect, "WIND_CHARGE");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectWindCharge effect = new EffectWindCharge();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectWindCharge effect = new EffectWindCharge();
            // args: spawnLocationFormat, destinationLocationFormat, speed, spread
            String[] args = {"PLAYER", "ENEMY", "1.5", "0.1"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectEnableMultipleArrow Tests")
    class EffectEnableMultipleArrowTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectEnableMultipleArrow effect = new EffectEnableMultipleArrow();
            assertEffectIdentifier(effect, "ENABLE_MULTIPLE_ARROW");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectEnableMultipleArrow effect = new EffectEnableMultipleArrow();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectEnableMultipleArrow effect = new EffectEnableMultipleArrow();
            // args: amountRange, velocityMod, damageRatio, cooldown
            String[] args = {"3", "1.0", "0.5", "5000"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectDisableMultipleArrow Tests")
    class EffectDisableMultipleArrowTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectDisableMultipleArrow effect = new EffectDisableMultipleArrow();
            assertEffectIdentifier(effect, "DISABLE_MULTIPLE_ARROW");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectDisableMultipleArrow effect = new EffectDisableMultipleArrow();
            assertTrue(effect.isAsync());
        }
    }
}
