package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for attribute-related effects:
 * - EffectAddAttribute
 * - EffectRemoveAttribute
 * - EffectAddAutoAttribute
 * - EffectRemoveAutoAttribute
 * - EffectAddCustomAttribute
 * - EffectRemoveCustomAttribute
 */
@DisplayName("Attribute Effects Tests")
class AttributeEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectAddAttribute Tests")
    class EffectAddAttributeTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddAttribute effect = new EffectAddAttribute();
            assertEffectIdentifier(effect, "ADD_ATTRIBUTE");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectAddAttribute effect = new EffectAddAttribute();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddAttribute effect = new EffectAddAttribute();
            String[] args = {"GENERIC_MAX_HEALTH", "health_modifier", "5.0", "0"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectAddAttribute effect = new EffectAddAttribute();
            String[] args = {"GENERIC_MAX_HEALTH", "health_modifier", "5.0", "0"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectAddAttribute effect = new EffectAddAttribute();
            String[] args = {"GENERIC_MAX_HEALTH", "health_modifier", "5.0", "0"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }
    }

    @Nested
    @DisplayName("EffectRemoveAttribute Tests")
    class EffectRemoveAttributeTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveAttribute effect = new EffectRemoveAttribute();
            assertEffectIdentifier(effect, "REMOVE_ATTRIBUTE");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectRemoveAttribute effect = new EffectRemoveAttribute();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveAttribute effect = new EffectRemoveAttribute();
            String[] args = {"GENERIC_MAX_HEALTH", "health_modifier"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectRemoveAttribute effect = new EffectRemoveAttribute();
            String[] args = {"GENERIC_MAX_HEALTH", "health_modifier"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectAddAutoAttribute Tests")
    class EffectAddAutoAttributeTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddAutoAttribute effect = new EffectAddAutoAttribute();
            assertEffectIdentifier(effect, "ADD_AUTO_ATTRIBUTE");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddAutoAttribute effect = new EffectAddAutoAttribute();
            String[] args = {"GENERIC_MAX_HEALTH", "health_mod", "0", "5.0", "0"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectRemoveAutoAttribute Tests")
    class EffectRemoveAutoAttributeTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveAutoAttribute effect = new EffectRemoveAutoAttribute();
            assertEffectIdentifier(effect, "REMOVE_AUTO_ATTRIBUTE");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveAutoAttribute effect = new EffectRemoveAutoAttribute();
            String[] args = {"GENERIC_MAX_HEALTH", "health_mod", "0"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectAddCustomAttribute Tests")
    class EffectAddCustomAttributeTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAddCustomAttribute effect = new EffectAddCustomAttribute();
            assertEffectIdentifier(effect, "ADD_CUSTOM_ATTRIBUTE");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAddCustomAttribute effect = new EffectAddCustomAttribute();
            // args: attributeName, CustomAttributeType name, range, [operation]
            String[] args = {"custom_attr", "OPTION_ATTACK", "5.0", "0"};
            assertSetupSucceeds(effect, args);
        }
    }

    @Nested
    @DisplayName("EffectRemoveCustomAttribute Tests")
    class EffectRemoveCustomAttributeTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveCustomAttribute effect = new EffectRemoveCustomAttribute();
            assertEffectIdentifier(effect, "REMOVE_CUSTOM_ATTRIBUTE");
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveCustomAttribute effect = new EffectRemoveCustomAttribute();
            String[] args = {"custom_attr", "modifier_name"};
            assertSetupSucceeds(effect, args);
        }
    }
}
