package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionHook - abstract base class for condition implementations.
 */
@DisplayName("ConditionHook Tests")
class ConditionHookTest {

    private TestConditionHook conditionHook;

    @BeforeEach
    void setUp() {
        conditionHook = new TestConditionHook();
    }

    @Nested
    @DisplayName("Settings Tests")
    class SettingsTests {

        @Test
        @DisplayName("should set and get settings")
        void shouldSetAndGetSettings() {
            ConditionSettings settings = new ConditionSettings(Target.PLAYER, false);
            conditionHook.setSettings(settings);

            assertSame(settings, conditionHook.getSettings());
        }

        @Test
        @DisplayName("should return null settings initially")
        void shouldReturnNullSettingsInitially() {
            assertNull(conditionHook.getSettings());
        }
    }

    @Nested
    @DisplayName("updateAndMatch Tests")
    class UpdateAndMatchTests {

        @Test
        @DisplayName("should return match result when not negative")
        void shouldReturnMatchResultWhenNotNegative() {
            conditionHook.setSettings(new ConditionSettings(Target.PLAYER, false));
            conditionHook.setMatchResult(true);
            CEFunctionData data = mock(CEFunctionData.class);
            when(data.getTarget()).thenReturn(Target.PLAYER);

            assertTrue(conditionHook.updateAndMatch(data));
        }

        @Test
        @DisplayName("should negate match result when negative is true")
        void shouldNegateMatchResult() {
            conditionHook.setSettings(new ConditionSettings(Target.PLAYER, true));
            conditionHook.setMatchResult(true);
            CEFunctionData data = mock(CEFunctionData.class);
            when(data.getTarget()).thenReturn(Target.PLAYER);

            assertFalse(conditionHook.updateAndMatch(data));
        }

        @Test
        @DisplayName("should negate false to true when negative is true")
        void shouldNegateFalseToTrue() {
            conditionHook.setSettings(new ConditionSettings(Target.PLAYER, true));
            conditionHook.setMatchResult(false);
            CEFunctionData data = mock(CEFunctionData.class);
            when(data.getTarget()).thenReturn(Target.PLAYER);

            assertTrue(conditionHook.updateAndMatch(data));
        }

        @Test
        @DisplayName("should clone data when target differs")
        void shouldCloneDataWhenTargetDiffers() {
            conditionHook.setSettings(new ConditionSettings(Target.ENEMY, false));
            conditionHook.setMatchResult(true);

            CEFunctionData data = mock(CEFunctionData.class);
            CEFunctionData clonedData = mock(CEFunctionData.class);
            when(data.getTarget()).thenReturn(Target.PLAYER);
            when(data.clone()).thenReturn(clonedData);
            when(clonedData.getTarget()).thenReturn(Target.ENEMY);

            conditionHook.updateAndMatch(data);

            verify(data).clone();
        }

        @Test
        @DisplayName("should not clone data when target matches")
        void shouldNotCloneWhenTargetMatches() {
            conditionHook.setSettings(new ConditionSettings(Target.PLAYER, false));
            conditionHook.setMatchResult(true);

            CEFunctionData data = mock(CEFunctionData.class);
            when(data.getTarget()).thenReturn(Target.PLAYER);

            conditionHook.updateAndMatch(data);

            verify(data, never()).clone();
        }
    }

    @Nested
    @DisplayName("clone Tests")
    class CloneTests {

        @Test
        @DisplayName("should return a clone")
        void shouldReturnClone() {
            conditionHook.setSettings(new ConditionSettings(Target.PLAYER, false));

            ConditionHook cloned = conditionHook.clone();

            assertNotNull(cloned);
            assertNotSame(conditionHook, cloned);
        }
    }

    @Nested
    @DisplayName("register Tests")
    class RegisterTests {

        @Test
        @DisplayName("should register via Condition.register")
        void shouldRegisterViaCondition() {
            String uniqueId = "TEST_HOOK_REG_" + System.nanoTime();
            TestConditionHook hook = new TestConditionHook(uniqueId);

            boolean result = hook.register();

            assertTrue(result);
            assertTrue(Condition.isRegister(uniqueId));

            // Cleanup
            Condition.unregister(hook);
        }
    }

    /**
     * Concrete test implementation of ConditionHook.
     */
    private static class TestConditionHook extends ConditionHook {
        private String identify;
        private boolean matchResult = false;

        TestConditionHook() {
            this("TEST_CONDITION");
        }

        TestConditionHook(String identify) {
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
        public boolean match(CEFunctionData data) {
            return matchResult;
        }

        void setMatchResult(boolean result) {
            this.matchResult = result;
        }
    }
}
