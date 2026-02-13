package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.player.CEPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for EffectData - wraps an EffectHook with scheduling data (delay, period, tick).
 */
@DisplayName("EffectData Tests")
class EffectDataTest {

    private EffectData effectData;
    private CEPlayer mockCaller;
    private EffectHook mockEffectHook;
    private EffectSettings mockSettings;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        mockCaller = mock(CEPlayer.class);
        mockEffectHook = mock(EffectHook.class);
        mockSettings = new EffectSettings("test_effect", Target.PLAYER, Target.ENEMY,
                null, 5L, 10L, false, false);
        mockData = mock(CEFunctionData.class);

        when(mockEffectHook.getSettings()).thenReturn(mockSettings);

        effectData = new EffectData(mockCaller, mockEffectHook, mockData);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with caller, hook, and data")
        void shouldCreateWithParams() {
            assertNotNull(effectData);
            assertSame(mockCaller, effectData.getCaller());
        }

        @Test
        @DisplayName("should not be running initially")
        void shouldNotBeRunningInitially() {
            assertFalse(effectData.isRunning());
        }

        @Test
        @DisplayName("should not be marked for removal initially")
        void shouldNotBeRemovedInitially() {
            assertFalse(effectData.isRemove());
        }
    }

    @Nested
    @DisplayName("getName Tests")
    class GetNameTests {

        @Test
        @DisplayName("should return name from cloned settings")
        void shouldReturnNameFromSettings() {
            assertEquals("test_effect", effectData.getName());
        }
    }

    @Nested
    @DisplayName("hasDelay Tests")
    class HasDelayTests {

        @Test
        @DisplayName("should return true when delay > 0")
        void shouldReturnTrueWhenHasDelay() {
            assertTrue(effectData.hasDelay());
        }

        @Test
        @DisplayName("should return false when delay is 0")
        void shouldReturnFalseWhenNoDelay() {
            EffectSettings noDelay = new EffectSettings("no_delay", Target.PLAYER, Target.ENEMY,
                    null, 0L, 10L, false, false);
            when(mockEffectHook.getSettings()).thenReturn(noDelay);

            EffectData data = new EffectData(mockCaller, mockEffectHook, mockData);

            assertFalse(data.hasDelay());
        }
    }

    @Nested
    @DisplayName("hasPeriod Tests")
    class HasPeriodTests {

        @Test
        @DisplayName("should return true when period > 0")
        void shouldReturnTrueWhenHasPeriod() {
            assertTrue(effectData.hasPeriod());
        }

        @Test
        @DisplayName("should return false when period is 0")
        void shouldReturnFalseWhenNoPeriod() {
            EffectSettings noPeriod = new EffectSettings("no_period", Target.PLAYER, Target.ENEMY,
                    null, 5L, 0L, false, false);
            when(mockEffectHook.getSettings()).thenReturn(noPeriod);

            EffectData data = new EffectData(mockCaller, mockEffectHook, mockData);

            assertFalse(data.hasPeriod());
        }
    }

    @Nested
    @DisplayName("hasScheduler Tests")
    class HasSchedulerTests {

        @Test
        @DisplayName("should return true when has delay")
        void shouldReturnTrueWhenHasDelay() {
            assertTrue(effectData.hasScheduler());
        }

        @Test
        @DisplayName("should return true when has period only")
        void shouldReturnTrueWhenHasPeriodOnly() {
            EffectSettings periodOnly = new EffectSettings("period_only", Target.PLAYER, Target.ENEMY,
                    null, 0L, 10L, false, false);
            when(mockEffectHook.getSettings()).thenReturn(periodOnly);

            EffectData data = new EffectData(mockCaller, mockEffectHook, mockData);

            assertTrue(data.hasScheduler());
        }

        @Test
        @DisplayName("should return false when no delay and no period")
        void shouldReturnFalseWhenNoScheduler() {
            EffectSettings noScheduler = new EffectSettings("no_sched", Target.PLAYER, Target.ENEMY,
                    null, 0L, 0L, false, false);
            when(mockEffectHook.getSettings()).thenReturn(noScheduler);

            EffectData data = new EffectData(mockCaller, mockEffectHook, mockData);

            assertFalse(data.hasScheduler());
        }
    }

    @Nested
    @DisplayName("tick Tests")
    class TickTests {

        @Test
        @DisplayName("should not be able to active delay at tick 0")
        void shouldNotActiveDelayAtTick0() {
            assertFalse(effectData.canActiveDelay());
        }

        @Test
        @DisplayName("should be able to active delay after enough ticks")
        void shouldActiveDelayAfterEnoughTicks() {
            for (int i = 0; i < 5; i++) {
                effectData.tick();
            }

            assertTrue(effectData.canActiveDelay());
        }

        @Test
        @DisplayName("should be able to active period at period intervals")
        void shouldActivePeriodAtIntervals() {
            // Period is 10, so tick must be a multiple of 10
            for (int i = 0; i < 10; i++) {
                effectData.tick();
            }

            assertTrue(effectData.canActivePeriod());
        }

        @Test
        @DisplayName("should not be able to active period between intervals")
        void shouldNotActivePeriodBetweenIntervals() {
            for (int i = 0; i < 7; i++) {
                effectData.tick();
            }

            assertFalse(effectData.canActivePeriod());
        }
    }

    @Nested
    @DisplayName("TaskName Tests")
    class TaskNameTests {

        @Test
        @DisplayName("should not have task name initially")
        void shouldNotHaveTaskNameInitially() {
            assertFalse(effectData.hasTaskName());
            assertNull(effectData.getTaskName());
        }

        @Test
        @DisplayName("should set and get task name")
        void shouldSetAndGetTaskName() {
            effectData.setTaskName("my_task");

            assertTrue(effectData.hasTaskName());
            assertEquals("my_task", effectData.getTaskName());
        }
    }

    @Nested
    @DisplayName("updateAndExecute Tests")
    class UpdateAndExecuteTests {

        @Test
        @DisplayName("should delegate to effectHook updateAndExecute")
        void shouldDelegateToHook() {
            effectData.updateAndExecute();

            verify(mockEffectHook).updateAndExecute(mockData);
        }

        @Test
        @DisplayName("should set running to true after execution")
        void shouldSetRunningTrue() {
            effectData.updateAndExecute();

            assertTrue(effectData.isRunning());
        }
    }

    @Nested
    @DisplayName("Remove and Running Flags Tests")
    class FlagTests {

        @Test
        @DisplayName("should set and get remove flag")
        void shouldSetAndGetRemove() {
            effectData.setRemove(true);
            assertTrue(effectData.isRemove());

            effectData.setRemove(false);
            assertFalse(effectData.isRemove());
        }

        @Test
        @DisplayName("should set and get running flag")
        void shouldSetAndGetRunning() {
            effectData.setRunning(true);
            assertTrue(effectData.isRunning());

            effectData.setRunning(false);
            assertFalse(effectData.isRunning());
        }
    }
}
