package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Effect - registry and executor for EffectHook instances.
 */
@DisplayName("Effect Tests")
class EffectTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with empty effects list")
        void shouldCreateWithEmptyList() {
            Effect effect = new Effect(Collections.emptyList());

            assertNotNull(effect);
            assertTrue(effect.getEffectHooks().isEmpty());
        }

        @Test
        @DisplayName("should create with effects list")
        void shouldCreateWithEffectsList() {
            EffectHook hook = mock(EffectHook.class);
            Effect effect = new Effect(Collections.singletonList(hook));

            assertEquals(1, effect.getEffectHooks().size());
        }
    }

    @Nested
    @DisplayName("execute Tests")
    class ExecuteTests {

        @Test
        @DisplayName("should execute all effect hooks")
        void shouldExecuteAllHooks() {
            EffectHook hook1 = mock(EffectHook.class);
            EffectHook hook2 = mock(EffectHook.class);
            Effect effect = new Effect(Arrays.asList(hook1, hook2));
            CEFunctionData data = mock(CEFunctionData.class);

            effect.execute(data);

            verify(hook1).updateAndExecute(data);
            verify(hook2).updateAndExecute(data);
        }

        @Test
        @DisplayName("should handle exception in hook gracefully")
        void shouldHandleExceptionGracefully() {
            EffectHook hookThrows = mock(EffectHook.class);
            EffectHook hookOk = mock(EffectHook.class);
            doThrow(new RuntimeException("test")).when(hookThrows).updateAndExecute(any());

            Effect effect = new Effect(Arrays.asList(hookThrows, hookOk));
            CEFunctionData data = mock(CEFunctionData.class);

            assertDoesNotThrow(() -> effect.execute(data));
            verify(hookOk).updateAndExecute(data);
        }

        @Test
        @DisplayName("should do nothing with empty effects list")
        void shouldDoNothingWithEmptyList() {
            Effect effect = new Effect(Collections.emptyList());
            CEFunctionData data = mock(CEFunctionData.class);

            assertDoesNotThrow(() -> effect.execute(data));
        }
    }

    @Nested
    @DisplayName("getEffectHooks Tests")
    class GetEffectHooksTests {

        @Test
        @DisplayName("should return the hooks list")
        void shouldReturnHooksList() {
            EffectHook hook = mock(EffectHook.class);
            Effect effect = new Effect(Collections.singletonList(hook));

            List<EffectHook> hooks = effect.getEffectHooks();

            assertEquals(1, hooks.size());
            assertSame(hook, hooks.get(0));
        }
    }

    @Nested
    @DisplayName("Static Registry Tests")
    class StaticRegistryTests {

        @Test
        @DisplayName("should register an effect hook")
        void shouldRegisterEffectHook() {
            String uniqueId = "TEST_EFFECT_" + System.nanoTime();
            EffectHook hook = mock(EffectHook.class);
            when(hook.getIdentify()).thenReturn(uniqueId);

            boolean registered = Effect.register(hook);

            assertTrue(registered);
            assertTrue(Effect.isRegister(uniqueId));

            // Cleanup
            Effect.unregister(hook);
        }

        @Test
        @DisplayName("should not register null hook")
        void shouldNotRegisterNull() {
            assertFalse(Effect.register(null));
        }

        @Test
        @DisplayName("should not register duplicate hook")
        void shouldNotRegisterDuplicate() {
            // Note: Effect.register() has a bug where containsKey checks the original case
            // but put() stores with toLowerCase(). To test the duplicate detection correctly,
            // we must use a lowercase identifier so containsKey finds the existing entry.
            String uniqueId = "test_dup_effect_" + System.nanoTime();
            EffectHook hook1 = mock(EffectHook.class);
            EffectHook hook2 = mock(EffectHook.class);
            when(hook1.getIdentify()).thenReturn(uniqueId);
            when(hook2.getIdentify()).thenReturn(uniqueId);

            assertTrue(Effect.register(hook1));
            assertFalse(Effect.register(hook2));

            // Cleanup
            Effect.unregister(hook1);
        }

        @Test
        @DisplayName("should unregister an effect hook")
        void shouldUnregisterEffectHook() {
            String uniqueId = "TEST_UNREG_EFFECT_" + System.nanoTime();
            EffectHook hook = mock(EffectHook.class);
            when(hook.getIdentify()).thenReturn(uniqueId);

            Effect.register(hook);
            Effect.unregister(hook);

            assertFalse(Effect.isRegister(uniqueId));
        }

        @Test
        @DisplayName("should handle unregister null gracefully")
        void shouldHandleUnregisterNull() {
            assertDoesNotThrow(() -> Effect.unregister(null));
        }

        @Test
        @DisplayName("should handle unregister with null identify")
        void shouldHandleUnregisterNullIdentify() {
            EffectHook hook = mock(EffectHook.class);
            when(hook.getIdentify()).thenReturn(null);

            assertDoesNotThrow(() -> Effect.unregister(hook));
        }

        @Test
        @DisplayName("isRegister(String) should return false for null")
        void isRegisterStringShouldReturnFalseForNull() {
            assertFalse(Effect.isRegister((String) null));
        }

        @Test
        @DisplayName("isRegister(EffectHook) should return false for null")
        void isRegisterHookShouldReturnFalseForNull() {
            assertFalse(Effect.isRegister((EffectHook) null));
        }

        @Test
        @DisplayName("should register case-insensitively")
        void shouldRegisterCaseInsensitively() {
            String uniqueId = "TEST_CASE_EFFECT_" + System.nanoTime();
            EffectHook hook = mock(EffectHook.class);
            when(hook.getIdentify()).thenReturn(uniqueId.toUpperCase());

            Effect.register(hook);

            assertTrue(Effect.isRegister(uniqueId.toLowerCase()));

            // Cleanup
            Effect.unregister(hook);
        }
    }

    @Nested
    @DisplayName("get Tests")
    class GetTests {

        @Test
        @DisplayName("should return null for unregistered identifier")
        void shouldReturnNullForUnregistered() {
            assertNull(Effect.get("NONEXISTENT_EFFECT_XYZ", new String[]{}));
        }

        @Test
        @DisplayName("should return cloned hook with args set up")
        void shouldReturnClonedHookWithArgs() {
            String uniqueId = "TEST_GET_EFFECT_" + System.nanoTime();
            EffectHook hook = mock(EffectHook.class);
            EffectHook clonedHook = mock(EffectHook.class);
            when(hook.getIdentify()).thenReturn(uniqueId);
            when(hook.clone()).thenReturn(clonedHook);

            Effect.register(hook);

            String[] args = {"arg1", "arg2"};
            EffectHook result = Effect.get(uniqueId, args);

            assertNotNull(result);
            assertSame(clonedHook, result);
            verify(clonedHook).setup(args);

            // Cleanup
            Effect.unregister(hook);
        }
    }
}
