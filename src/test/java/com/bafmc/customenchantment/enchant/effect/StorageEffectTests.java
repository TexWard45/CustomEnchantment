package com.bafmc.customenchantment.enchant.effect;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for storage-related effects:
 * - EffectNumberStorage
 * - EffectTextStorage
 * - EffectRemoveTask
 * - EffectRemoveTaskAsync
 */
@DisplayName("Storage Effects Tests")
class StorageEffectTests extends EffectBaseTest {

    @Nested
    @DisplayName("EffectNumberStorage Tests")
    class EffectNumberStorageTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectNumberStorage effect = new EffectNumberStorage();
            assertEffectIdentifier(effect, "NUMBER_STORAGE");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectNumberStorage effect = new EffectNumberStorage();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectNumberStorage effect = new EffectNumberStorage();
            String[] args = {"storage_key", "DELAY:100"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectNumberStorage effect = new EffectNumberStorage();
            String[] args = {"storage_key", "100"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle delay prefix")
        void shouldHandleDelayPrefix() {
            EffectNumberStorage effect = new EffectNumberStorage();
            String[] args = {"storage_key", "DELAY:50"};
            assertSetupSucceeds(effect, args);

            CEFunctionData data = createTestData(null);
            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectTextStorage Tests")
    class EffectTextStorageTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectTextStorage effect = new EffectTextStorage();
            assertEffectIdentifier(effect, "TEXT_STORAGE");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectTextStorage effect = new EffectTextStorage();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectTextStorage effect = new EffectTextStorage();
            String[] args = {"text_key", "storage_value"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectTextStorage effect = new EffectTextStorage();
            String[] args = {"text_key", "value"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should handle delay prefix with text")
        void shouldHandleDelayPrefixWithText() {
            EffectTextStorage effect = new EffectTextStorage();
            String[] args = {"text_key", "DELAY:50:my_text_value"};
            assertSetupSucceeds(effect, args);

            CEFunctionData data = createTestData(null);
            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectRemoveTask Tests")
    class EffectRemoveTaskTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveTask effect = new EffectRemoveTask();
            assertEffectIdentifier(effect, "REMOVE_TASK");
        }

        @Test
        @DisplayName("should not be async")
        void shouldNotBeAsync() {
            EffectRemoveTask effect = new EffectRemoveTask();
            assertFalse(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveTask effect = new EffectRemoveTask();
            String[] args = {"task_key"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectRemoveTask effect = new EffectRemoveTask();
            String[] args = {"task_key"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }

    @Nested
    @DisplayName("EffectRemoveTaskAsync Tests")
    class EffectRemoveTaskAsyncTests {

        @Test
        @DisplayName("should have correct identifier")
        void shouldHaveCorrectIdentifier() {
            EffectRemoveTaskAsync effect = new EffectRemoveTaskAsync();
            assertEffectIdentifier(effect, "REMOVE_TASK_ASYNC");
        }

        @Test
        @DisplayName("should be async")
        void shouldBeAsync() {
            EffectRemoveTaskAsync effect = new EffectRemoveTaskAsync();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectRemoveTaskAsync effect = new EffectRemoveTaskAsync();
            String[] args = {"task_key"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectRemoveTaskAsync effect = new EffectRemoveTaskAsync();
            String[] args = {"task_key"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }
    }
}
