package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.player.CEPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for EffectTaskSeparate - separates effect hooks into sync and async lists.
 */
@DisplayName("EffectTaskSeparate Tests")
class EffectTaskSeparateTest {

    private CEPlayer mockCaller;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        mockCaller = mock(CEPlayer.class);
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("Default Constructor Tests")
    class DefaultConstructorTests {

        @Test
        @DisplayName("should create with empty lists")
        void shouldCreateWithEmptyLists() {
            EffectTaskSeparate separate = new EffectTaskSeparate();

            assertTrue(separate.getEffectList().isEmpty());
            assertTrue(separate.getEffectAsyncList().isEmpty());
        }
    }

    @Nested
    @DisplayName("Parameterized Constructor Tests")
    class ParameterizedConstructorTests {

        @Test
        @DisplayName("should separate async hooks into async list")
        void shouldSeparateAsyncHooks() {
            EffectHook asyncHook = createMockHook(true);
            Effect effect = new Effect(Collections.singletonList(asyncHook));

            EffectTaskSeparate separate = new EffectTaskSeparate(mockCaller, effect, mockData);

            assertTrue(separate.getEffectList().isEmpty());
            assertEquals(1, separate.getEffectAsyncList().size());
        }

        @Test
        @DisplayName("should separate sync hooks into sync list")
        void shouldSeparateSyncHooks() {
            EffectHook syncHook = createMockHook(false);
            Effect effect = new Effect(Collections.singletonList(syncHook));

            EffectTaskSeparate separate = new EffectTaskSeparate(mockCaller, effect, mockData);

            assertEquals(1, separate.getEffectList().size());
            assertTrue(separate.getEffectAsyncList().isEmpty());
        }

        @Test
        @DisplayName("should separate mixed hooks correctly")
        void shouldSeparateMixedHooks() {
            EffectHook asyncHook = createMockHook(true);
            EffectHook syncHook = createMockHook(false);
            Effect effect = new Effect(Arrays.asList(asyncHook, syncHook));

            EffectTaskSeparate separate = new EffectTaskSeparate(mockCaller, effect, mockData);

            assertEquals(1, separate.getEffectList().size());
            assertEquals(1, separate.getEffectAsyncList().size());
        }
    }

    @Nested
    @DisplayName("add Tests")
    class AddTests {

        @Test
        @DisplayName("should add more hooks after construction")
        void shouldAddMoreHooks() {
            EffectTaskSeparate separate = new EffectTaskSeparate();

            EffectHook asyncHook1 = createMockHook(true);
            EffectHook asyncHook2 = createMockHook(true);
            EffectHook syncHook = createMockHook(false);

            separate.add(mockCaller, new Effect(Collections.singletonList(asyncHook1)), mockData);
            separate.add(mockCaller, new Effect(Arrays.asList(asyncHook2, syncHook)), mockData);

            assertEquals(1, separate.getEffectList().size());
            assertEquals(2, separate.getEffectAsyncList().size());
        }
    }

    @Nested
    @DisplayName("Empty Effect Tests")
    class EmptyEffectTests {

        @Test
        @DisplayName("should handle empty effect")
        void shouldHandleEmptyEffect() {
            Effect emptyEffect = new Effect(Collections.emptyList());

            EffectTaskSeparate separate = new EffectTaskSeparate(mockCaller, emptyEffect, mockData);

            assertTrue(separate.getEffectList().isEmpty());
            assertTrue(separate.getEffectAsyncList().isEmpty());
        }
    }

    private EffectHook createMockHook(boolean isAsync) {
        EffectHook hook = mock(EffectHook.class);
        when(hook.isAsync()).thenReturn(isAsync);
        EffectSettings settings = new EffectSettings("test", Target.PLAYER, Target.ENEMY,
                null, 0L, 0L, false, false);
        when(hook.getSettings()).thenReturn(settings);
        return hook;
    }
}
