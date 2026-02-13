package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EffectHook - abstract base class for effect implementations.
 * Tests clone, register, isAsync, and get(args, start) utility.
 */
@DisplayName("EffectHook Tests")
class EffectHookTest {

    private TestEffectHook effectHook;

    @BeforeEach
    void setUp() {
        effectHook = new TestEffectHook();
    }

    @Nested
    @DisplayName("Settings Tests")
    class SettingsTests {

        @Test
        @DisplayName("should set and get settings")
        void shouldSetAndGetSettings() {
            EffectSettings settings = new EffectSettings();
            effectHook.setSettings(settings);

            assertSame(settings, effectHook.getSettings());
        }

        @Test
        @DisplayName("should return null settings initially")
        void shouldReturnNullSettingsInitially() {
            assertNull(effectHook.getSettings());
        }
    }

    @Nested
    @DisplayName("isAsync Tests")
    class IsAsyncTests {

        @Test
        @DisplayName("should return true by default")
        void shouldReturnTrueByDefault() {
            assertTrue(effectHook.isAsync());
        }
    }

    @Nested
    @DisplayName("isForceEffectOnEnemyDead Tests")
    class IsForceEffectOnEnemyDeadTests {

        @Test
        @DisplayName("should return false by default")
        void shouldReturnFalseByDefault() {
            assertFalse(effectHook.isForceEffectOnEnemyDead());
        }
    }

    @Nested
    @DisplayName("clone Tests")
    class CloneTests {

        @Test
        @DisplayName("should return a clone")
        void shouldReturnClone() {
            EffectHook cloned = effectHook.clone();

            assertNotNull(cloned);
            assertNotSame(effectHook, cloned);
        }

        @Test
        @DisplayName("cloned hook should have same identify")
        void clonedShouldHaveSameIdentify() {
            EffectHook cloned = effectHook.clone();

            assertEquals(effectHook.getIdentify(), cloned.getIdentify());
        }
    }

    @Nested
    @DisplayName("register Tests")
    class RegisterTests {

        @Test
        @DisplayName("should register via Effect.register")
        void shouldRegisterViaEffect() {
            String uniqueId = "TEST_EFFECT_HOOK_REG_" + System.nanoTime();
            TestEffectHook hook = new TestEffectHook(uniqueId);

            boolean result = hook.register();

            assertTrue(result);
            assertTrue(Effect.isRegister(uniqueId));

            // Cleanup
            Effect.unregister(hook);
        }
    }

    @Nested
    @DisplayName("get(args, start) Tests")
    class GetArgsTests {

        @Test
        @DisplayName("should concatenate args from start index")
        void shouldConcatenateFromStart() {
            String[] args = {"a", "b", "c", "d"};

            String result = effectHook.get(args, 1);

            assertEquals("b:c:d", result);
        }

        @Test
        @DisplayName("should return single arg when start is last index")
        void shouldReturnSingleArg() {
            String[] args = {"a", "b", "c"};

            String result = effectHook.get(args, 2);

            assertEquals("c", result);
        }

        @Test
        @DisplayName("should return all args when start is 0")
        void shouldReturnAllArgs() {
            String[] args = {"x", "y", "z"};

            String result = effectHook.get(args, 0);

            assertEquals("x:y:z", result);
        }

        @Test
        @DisplayName("should return empty string when start exceeds args length")
        void shouldReturnEmptyWhenStartExceeds() {
            String[] args = {"a", "b"};

            String result = effectHook.get(args, 5);

            assertEquals("", result);
        }
    }

    /**
     * Concrete test implementation of EffectHook.
     */
    private static class TestEffectHook extends EffectHook {
        private String identify;

        TestEffectHook() {
            this("TEST_EFFECT");
        }

        TestEffectHook(String identify) {
            this.identify = identify;
        }

        @Override
        public String getIdentify() {
            return identify;
        }

        @Override
        public void setup(String[] args) {
            // no-op for testing
        }

        @Override
        public void execute(CEFunctionData data) {
            // no-op for testing
        }
    }
}
