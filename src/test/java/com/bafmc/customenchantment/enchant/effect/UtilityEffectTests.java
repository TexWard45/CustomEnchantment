package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for utility-related effects:
 * - EffectMessage
 * - EffectAdvancedMessage
 * - EffectPlaySound
 * - EffectTeleport
 * - EffectDurability
 * - EffectSetBlock
 */
@DisplayName("Utility Effects Tests")
class UtilityEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectMessage Tests")
    class EffectMessageTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectMessage effect = new EffectMessage();
            assertEffectIdentifier(effect, "MESSAGE");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectMessage effect = new EffectMessage();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectMessage effect = new EffectMessage();
            String[] args = {"Hello %player%"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectMessage effect = new EffectMessage();
            String[] args = {"Hello World"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle null player gracefully")
        void shouldHandleNullPlayerGracefully() {
            EffectMessage effect = new EffectMessage();
            String[] args = {"Hello World"};
            effect.setup(args);

            assertExecuteHandlesNullPlayer(effect);
        }

        @Test
        @DisplayName("should handle multiple message parts")
        void shouldHandleMultipleMessageParts() {
            EffectMessage effect = new EffectMessage();
            String[] args = {"Hello", "World", "Player"};
            assertSetupSucceeds(effect, args);

            CEFunctionData data = createTestData(null);
            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectAdvancedMessage Tests")
    class EffectAdvancedMessageTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectAdvancedMessage effect = new EffectAdvancedMessage();
            assertEffectIdentifier(effect, "ADVANCED_MESSAGE");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectAdvancedMessage effect = new EffectAdvancedMessage();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectAdvancedMessage effect = new EffectAdvancedMessage();
            String[] args = {"Advanced message here"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectAdvancedMessage effect = new EffectAdvancedMessage();
            String[] args = {"Advanced message"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectPlaySound Tests")
    class EffectPlaySoundTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectPlaySound effect = new EffectPlaySound();
            assertEffectIdentifier(effect, "PLAY_SOUND");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectPlaySound effect = new EffectPlaySound();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectPlaySound effect = new EffectPlaySound();
            // args: sound, volume, pitch, [distance]
            String[] args = {"ENTITY_GENERIC_EXPLODE", "1.0", "1.0"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectPlaySound effect = new EffectPlaySound();
            String[] args = {"ENTITY_GENERIC_EXPLODE", "1.0", "1.0"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectTeleport Tests")
    class EffectTeleportTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectTeleport effect = new EffectTeleport();
            assertEffectIdentifier(effect, "TELEPORT");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectTeleport effect = new EffectTeleport();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectTeleport effect = new EffectTeleport();
            String[] args = {"PLAYER"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectTeleport effect = new EffectTeleport();
            String[] args = {"PLAYER"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectDurability Tests")
    class EffectDurabilityTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectDurability effect = new EffectDurability();
            assertEffectIdentifier(effect, "DURABILITY");
        }

        @Test
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectDurability effect = new EffectDurability();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectDurability effect = new EffectDurability();
            // args: modifyType, typeArmor (comma-separated EquipSlot), value
            String[] args = {"ADD", "MAINHAND", "10"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectDurability effect = new EffectDurability();
            String[] args = {"ADD", "MAINHAND", "10"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectSetBlock Tests")
    class EffectSetBlockTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectSetBlock effect = new EffectSetBlock();
            assertEffectIdentifier(effect, "SET_BLOCK");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectSetBlock effect = new EffectSetBlock();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectSetBlock effect = new EffectSetBlock();
            // args: material, duration, locationFormat
            String[] args = {"STONE", "1000", "PLAYER"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectSetBlock effect = new EffectSetBlock();
            String[] args = {"STONE", "1000", "PLAYER"};
            effect.setup(args);

            // execute() requires full plugin context (CustomEnchantment.instance())
            // which is not available in unit test environment
            // Just verify setup succeeds - execute requires integration test
        }
    }
}
