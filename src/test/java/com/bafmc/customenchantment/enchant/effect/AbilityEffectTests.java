package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for ability-related effects:
 * - EffectActiveAbility
 * - EffectDeactiveAbility
 * - EffectActiveDoubleJump
 * - EffectDeactiveDoubleJump
 * - EffectActiveDash
 * - EffectDeactiveDash
 * - EffectActiveFlash
 * - EffectDeactiveFlash
 * - EffectSetFlight
 */
@DisplayName("Ability Effects Tests")
class AbilityEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectActiveAbility Tests")
    class EffectActiveAbilityTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectActiveAbility effect = new EffectActiveAbility();
            assertEffectIdentifier(effect, "ACTIVE_ABILITY");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectActiveAbility effect = new EffectActiveAbility();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectActiveAbility effect = new EffectActiveAbility();
            String[] args = {"ability_name"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectActiveAbility effect = new EffectActiveAbility();
            String[] args = {"ability_name"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectDeactiveAbility Tests")
    class EffectDeactiveAbilityTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectDeactiveAbility effect = new EffectDeactiveAbility();
            assertEffectIdentifier(effect, "DEACTIVE_ABILITY");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectDeactiveAbility effect = new EffectDeactiveAbility();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectDeactiveAbility effect = new EffectDeactiveAbility();
            String[] args = {"ability_name"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectActiveDoubleJump Tests")
    class EffectActiveDoubleJumpTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectActiveDoubleJump effect = new EffectActiveDoubleJump();
            assertEffectIdentifier(effect, "ACTIVE_DOUBLE_JUMP");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectActiveDoubleJump effect = new EffectActiveDoubleJump();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectActiveDoubleJump effect = new EffectActiveDoubleJump();
            String[] args = {"1.0", "5000", "VERSION1", "particle"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectDeactiveDoubleJump Tests")
    class EffectDeactiveDoubleJumpTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectDeactiveDoubleJump effect = new EffectDeactiveDoubleJump();
            assertEffectIdentifier(effect, "DEACTIVE_DOUBLE_JUMP");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectDeactiveDoubleJump effect = new EffectDeactiveDoubleJump();
            assertFalse(effect.isAsync());
        }
    }

    @Nested
    @DisplayName("EffectActiveDash Tests")
    class EffectActiveDashTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectActiveDash effect = new EffectActiveDash();
            assertEffectIdentifier(effect, "ACTIVE_DASH");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectActiveDash effect = new EffectActiveDash();
            assertFalse(effect.isAsync());
        }
    }

    @Nested
    @DisplayName("EffectDeactiveDash Tests")
    class EffectDeactiveDashTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectDeactiveDash effect = new EffectDeactiveDash();
            assertEffectIdentifier(effect, "DEACTIVE_DASH");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectDeactiveDash effect = new EffectDeactiveDash();
            assertFalse(effect.isAsync());
        }
    }

    @Nested
    @DisplayName("EffectActiveFlash Tests")
    class EffectActiveFlashTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectActiveFlash effect = new EffectActiveFlash();
            assertEffectIdentifier(effect, "ACTIVE_FLASH");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectActiveFlash effect = new EffectActiveFlash();
            assertFalse(effect.isAsync());
        }
    }

    @Nested
    @DisplayName("EffectDeactiveFlash Tests")
    class EffectDeactiveFlashTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectDeactiveFlash effect = new EffectDeactiveFlash();
            assertEffectIdentifier(effect, "DEACTIVE_FLASH");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectDeactiveFlash effect = new EffectDeactiveFlash();
            assertFalse(effect.isAsync());
        }
    }

    @Nested
    @DisplayName("EffectSetFlight Tests")
    class EffectSetFlightTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectSetFlight effect = new EffectSetFlight();
            assertEffectIdentifier(effect, "SET_FLIGHT");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectSetFlight effect = new EffectSetFlight();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectSetFlight effect = new EffectSetFlight();
            String[] args = {"true"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectSetFlight effect = new EffectSetFlight();
            String[] args = {"true"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle enable and disable flight")
        void shouldHandleEnableAndDisableFlight() {
            EffectSetFlight effectEnable = new EffectSetFlight();
            String[] argsEnable = {"true"};
            effectEnable.setup(argsEnable);

            EffectSetFlight effectDisable = new EffectSetFlight();
            String[] argsDisable = {"false"};
            effectDisable.setup(argsDisable);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effectEnable, data);
            assertExecuteSucceeds(effectDisable, data);
        }
    }
}
