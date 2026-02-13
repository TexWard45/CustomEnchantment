package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionOR - holds a list of ConditionHook instances for OR logic.
 */
@DisplayName("ConditionOR Tests")
class ConditionORTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with empty list")
        void shouldCreateWithEmptyList() {
            ConditionOR conditionOR = new ConditionOR(Collections.emptyList());

            assertNotNull(conditionOR);
            assertTrue(conditionOR.isEmpty());
        }

        @Test
        @DisplayName("should create with hooks list")
        void shouldCreateWithHooksList() {
            ConditionHook hook = mock(ConditionHook.class);
            ConditionOR conditionOR = new ConditionOR(Collections.singletonList(hook));

            assertFalse(conditionOR.isEmpty());
            assertEquals(1, conditionOR.getConditionHooks().size());
        }
    }

    @Nested
    @DisplayName("getConditionHooks Tests")
    class GetConditionHooksTests {

        @Test
        @DisplayName("should return the hooks list")
        void shouldReturnHooksList() {
            ConditionHook hook1 = mock(ConditionHook.class);
            ConditionHook hook2 = mock(ConditionHook.class);
            List<ConditionHook> hooks = Arrays.asList(hook1, hook2);

            ConditionOR conditionOR = new ConditionOR(hooks);

            assertEquals(2, conditionOR.getConditionHooks().size());
            assertSame(hook1, conditionOR.getConditionHooks().get(0));
            assertSame(hook2, conditionOR.getConditionHooks().get(1));
        }
    }

    @Nested
    @DisplayName("isEmpty Tests")
    class IsEmptyTests {

        @Test
        @DisplayName("should return true for empty list")
        void shouldReturnTrueForEmptyList() {
            ConditionOR conditionOR = new ConditionOR(new ArrayList<>());

            assertTrue(conditionOR.isEmpty());
        }

        @Test
        @DisplayName("should return false for non-empty list")
        void shouldReturnFalseForNonEmptyList() {
            ConditionHook hook = mock(ConditionHook.class);
            ConditionOR conditionOR = new ConditionOR(Collections.singletonList(hook));

            assertFalse(conditionOR.isEmpty());
        }

        @Test
        @DisplayName("should return false for multiple hooks")
        void shouldReturnFalseForMultipleHooks() {
            List<ConditionHook> hooks = Arrays.asList(
                    mock(ConditionHook.class),
                    mock(ConditionHook.class),
                    mock(ConditionHook.class)
            );
            ConditionOR conditionOR = new ConditionOR(hooks);

            assertFalse(conditionOR.isEmpty());
        }
    }
}
