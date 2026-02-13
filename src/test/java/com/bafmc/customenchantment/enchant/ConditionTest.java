package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Condition - registry and evaluator for ConditionHook instances.
 * Uses AND logic between ConditionOR groups, OR logic within each group.
 */
@DisplayName("Condition Tests")
class ConditionTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with empty conditions list")
        void shouldCreateWithEmptyList() {
            Condition condition = new Condition(Collections.emptyList());

            assertNotNull(condition);
        }

        @Test
        @DisplayName("should create with conditions list")
        void shouldCreateWithConditionsList() {
            ConditionOR or = new ConditionOR(Collections.emptyList());
            Condition condition = new Condition(Collections.singletonList(or));

            assertNotNull(condition);
        }
    }

    @Nested
    @DisplayName("check(CEFunctionData) Tests")
    class CheckDataTests {

        @Test
        @DisplayName("should return false when data is null")
        void shouldReturnFalseWhenDataNull() {
            Condition condition = new Condition(Collections.emptyList());

            assertFalse(condition.check((CEFunctionData) null));
        }

        @Test
        @DisplayName("should return true when no conditions (empty list)")
        void shouldReturnTrueWhenNoConditions() {
            Condition condition = new Condition(Collections.emptyList());
            CEFunctionData data = mock(CEFunctionData.class);

            assertTrue(condition.check(data));
        }

        @Test
        @DisplayName("should return true when empty ConditionOR group (isEmpty=true)")
        void shouldReturnTrueForEmptyConditionOR() {
            // An empty ConditionOR returns true (isEmpty check)
            ConditionOR emptyOR = new ConditionOR(Collections.emptyList());
            Condition condition = new Condition(Collections.singletonList(emptyOR));
            CEFunctionData data = mock(CEFunctionData.class);

            assertTrue(condition.check(data));
        }

        @Test
        @DisplayName("should return true when single hook matches in OR group")
        void shouldReturnTrueWhenSingleHookMatches() {
            ConditionHook hook = mock(ConditionHook.class);
            when(hook.updateAndMatch(any())).thenReturn(true);

            ConditionOR condOR = new ConditionOR(Collections.singletonList(hook));
            Condition condition = new Condition(Collections.singletonList(condOR));
            CEFunctionData data = mock(CEFunctionData.class);

            assertTrue(condition.check(data));
        }

        @Test
        @DisplayName("should return false when single hook fails in OR group")
        void shouldReturnFalseWhenSingleHookFails() {
            ConditionHook hook = mock(ConditionHook.class);
            when(hook.updateAndMatch(any())).thenReturn(false);

            ConditionOR condOR = new ConditionOR(Collections.singletonList(hook));
            Condition condition = new Condition(Collections.singletonList(condOR));
            CEFunctionData data = mock(CEFunctionData.class);

            assertFalse(condition.check(data));
        }

        @Test
        @DisplayName("should return true when any hook in OR group matches")
        void shouldReturnTrueWhenAnyInORMatches() {
            ConditionHook hookFalse = mock(ConditionHook.class);
            ConditionHook hookTrue = mock(ConditionHook.class);
            when(hookFalse.updateAndMatch(any())).thenReturn(false);
            when(hookTrue.updateAndMatch(any())).thenReturn(true);

            ConditionOR condOR = new ConditionOR(Arrays.asList(hookFalse, hookTrue));
            Condition condition = new Condition(Collections.singletonList(condOR));
            CEFunctionData data = mock(CEFunctionData.class);

            assertTrue(condition.check(data));
        }

        @Test
        @DisplayName("should require all AND groups to pass (all OR groups must match)")
        void shouldRequireAllANDGroups() {
            ConditionHook hook1 = mock(ConditionHook.class);
            ConditionHook hook2 = mock(ConditionHook.class);
            when(hook1.updateAndMatch(any())).thenReturn(true);
            when(hook2.updateAndMatch(any())).thenReturn(false);

            ConditionOR or1 = new ConditionOR(Collections.singletonList(hook1));
            ConditionOR or2 = new ConditionOR(Collections.singletonList(hook2));

            // AND logic: or1 passes, or2 fails => overall false
            Condition condition = new Condition(Arrays.asList(or1, or2));
            CEFunctionData data = mock(CEFunctionData.class);

            assertFalse(condition.check(data));
        }

        @Test
        @DisplayName("should pass when all AND groups pass")
        void shouldPassWhenAllANDGroupsPass() {
            ConditionHook hook1 = mock(ConditionHook.class);
            ConditionHook hook2 = mock(ConditionHook.class);
            when(hook1.updateAndMatch(any())).thenReturn(true);
            when(hook2.updateAndMatch(any())).thenReturn(true);

            ConditionOR or1 = new ConditionOR(Collections.singletonList(hook1));
            ConditionOR or2 = new ConditionOR(Collections.singletonList(hook2));

            Condition condition = new Condition(Arrays.asList(or1, or2));
            CEFunctionData data = mock(CEFunctionData.class);

            assertTrue(condition.check(data));
        }

        @Test
        @DisplayName("should handle exception in hook gracefully")
        void shouldHandleExceptionGracefully() {
            ConditionHook hookThrows = mock(ConditionHook.class);
            when(hookThrows.updateAndMatch(any())).thenThrow(new RuntimeException("test error"));

            ConditionOR condOR = new ConditionOR(Collections.singletonList(hookThrows));
            Condition condition = new Condition(Collections.singletonList(condOR));
            CEFunctionData data = mock(CEFunctionData.class);

            // Should not throw, should return false (single hook fails and OR is not empty)
            assertFalse(condition.check(data));
        }
    }

    @Nested
    @DisplayName("Static Registry Tests")
    class StaticRegistryTests {

        @Test
        @DisplayName("should register a condition hook")
        void shouldRegisterConditionHook() {
            String uniqueId = "TEST_COND_" + System.nanoTime();
            ConditionHook hook = mock(ConditionHook.class);
            when(hook.getIdentify()).thenReturn(uniqueId);

            boolean registered = Condition.register(hook);

            assertTrue(registered);
            assertTrue(Condition.isRegister(uniqueId));

            // Cleanup
            Condition.unregister(hook);
        }

        @Test
        @DisplayName("should not register null hook")
        void shouldNotRegisterNull() {
            assertFalse(Condition.register(null));
        }

        @Test
        @DisplayName("should not register duplicate hook")
        void shouldNotRegisterDuplicate() {
            String uniqueId = "TEST_DUP_COND_" + System.nanoTime();
            ConditionHook hook1 = mock(ConditionHook.class);
            ConditionHook hook2 = mock(ConditionHook.class);
            when(hook1.getIdentify()).thenReturn(uniqueId);
            when(hook2.getIdentify()).thenReturn(uniqueId);

            assertTrue(Condition.register(hook1));
            assertFalse(Condition.register(hook2));

            // Cleanup
            Condition.unregister(hook1);
        }

        @Test
        @DisplayName("should unregister a condition hook")
        void shouldUnregisterConditionHook() {
            String uniqueId = "TEST_UNREG_COND_" + System.nanoTime();
            ConditionHook hook = mock(ConditionHook.class);
            when(hook.getIdentify()).thenReturn(uniqueId);

            Condition.register(hook);
            Condition.unregister(hook);

            assertFalse(Condition.isRegister(uniqueId));
        }

        @Test
        @DisplayName("should handle unregister null gracefully")
        void shouldHandleUnregisterNull() {
            assertDoesNotThrow(() -> Condition.unregister(null));
        }

        @Test
        @DisplayName("should handle unregister hook with null identify")
        void shouldHandleUnregisterNullIdentify() {
            ConditionHook hook = mock(ConditionHook.class);
            when(hook.getIdentify()).thenReturn(null);

            assertDoesNotThrow(() -> Condition.unregister(hook));
        }

        @Test
        @DisplayName("isRegister(String) should return false for null")
        void isRegisterStringShouldReturnFalseForNull() {
            assertFalse(Condition.isRegister((String) null));
        }

        @Test
        @DisplayName("isRegister(ConditionHook) should return false for null")
        void isRegisterHookShouldReturnFalseForNull() {
            assertFalse(Condition.isRegister((ConditionHook) null));
        }

        @Test
        @DisplayName("should register case-insensitively")
        void shouldRegisterCaseInsensitively() {
            String uniqueId = "TEST_CASE_COND_" + System.nanoTime();
            ConditionHook hook = mock(ConditionHook.class);
            when(hook.getIdentify()).thenReturn(uniqueId.toUpperCase());

            Condition.register(hook);

            assertTrue(Condition.isRegister(uniqueId.toLowerCase()));

            // Cleanup
            Condition.unregister(hook);
        }
    }

    @Nested
    @DisplayName("get Tests")
    class GetTests {

        @Test
        @DisplayName("should return null for unregistered identifier")
        void shouldReturnNullForUnregistered() {
            assertNull(Condition.get("NONEXISTENT_COND_XYZ", new String[]{}));
        }

        @Test
        @DisplayName("should return cloned hook with args set up")
        void shouldReturnClonedHookWithArgs() {
            String uniqueId = "TEST_GET_COND_" + System.nanoTime();
            ConditionHook hook = mock(ConditionHook.class);
            ConditionHook clonedHook = mock(ConditionHook.class);
            when(hook.getIdentify()).thenReturn(uniqueId);
            when(hook.clone()).thenReturn(clonedHook);

            Condition.register(hook);

            String[] args = {"arg1", "arg2"};
            ConditionHook result = Condition.get(uniqueId, args);

            assertNotNull(result);
            assertSame(clonedHook, result);
            verify(clonedHook).setup(args);

            // Cleanup
            Condition.unregister(hook);
        }
    }
}
