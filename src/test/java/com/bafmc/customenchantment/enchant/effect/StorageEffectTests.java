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
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectNumberStorage effect = new EffectNumberStorage();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectNumberStorage effect = new EffectNumberStorage();
            // args: type (ADD/SUB/REMOVE/SET/CLEAR), key, [value]
            String[] args = {"SET", "storage_key", "100"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectNumberStorage effect = new EffectNumberStorage();
            String[] args = {"SET", "storage_key", "100"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should setup with REMOVE type without value")
        void shouldSetupWithRemoveType() {
            EffectNumberStorage effect = new EffectNumberStorage();
            // REMOVE type does not need value arg
            String[] args = {"REMOVE", "storage_key"};
            assertSetupSucceeds(effect, args);
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
        @DisplayName("should be async by default")
        void shouldBeAsyncByDefault() {
            EffectTextStorage effect = new EffectTextStorage();
            assertTrue(effect.isAsync());
        }

        @Test
        @DisplayName("should setup with valid args")
        void shouldSetupWithValidArgs() {
            EffectTextStorage effect = new EffectTextStorage();
            // args: type (SET/REMOVE), key, [value]
            String[] args = {"SET", "text_key", "storage_value"};
            assertSetupSucceeds(effect, args);
        }

        @Test
        @DisplayName("should execute without throwing exception")
        void shouldExecuteWithoutThrowingException() {
            EffectTextStorage effect = new EffectTextStorage();
            String[] args = {"SET", "text_key", "value"};
            effect.setup(args);

            CEFunctionData data = createTestData(null);

            assertExecuteSucceeds(effect, data);
        }

        @Test
        @DisplayName("should setup with REMOVE type without value")
        void shouldSetupWithRemoveType() {
            EffectTextStorage effect = new EffectTextStorage();
            // REMOVE type does not need value arg
            String[] args = {"REMOVE", "text_key"};
            assertSetupSucceeds(effect, args);
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
