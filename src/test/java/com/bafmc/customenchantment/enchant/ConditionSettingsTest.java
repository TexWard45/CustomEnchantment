package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ConditionSettings - holds target and negative flag for conditions.
 */
@DisplayName("ConditionSettings Tests")
class ConditionSettingsTest {

    @Nested
    @DisplayName("Default Constructor Tests")
    class DefaultConstructorTests {

        @Test
        @DisplayName("should create with null target")
        void shouldCreateWithNullTarget() {
            ConditionSettings settings = new ConditionSettings();

            assertNull(settings.getTarget());
        }

        @Test
        @DisplayName("should create with negative false")
        void shouldCreateWithNegativeFalse() {
            ConditionSettings settings = new ConditionSettings();

            assertFalse(settings.isNegative());
        }
    }

    @Nested
    @DisplayName("Parameterized Constructor Tests")
    class ParameterizedConstructorTests {

        @ParameterizedTest(name = "should set target to {0}")
        @EnumSource(Target.class)
        @DisplayName("should set target from constructor")
        void shouldSetTarget(Target target) {
            ConditionSettings settings = new ConditionSettings(target, false);

            assertEquals(target, settings.getTarget());
        }

        @Test
        @DisplayName("should set negative true from constructor")
        void shouldSetNegativeTrue() {
            ConditionSettings settings = new ConditionSettings(Target.PLAYER, true);

            assertTrue(settings.isNegative());
        }

        @Test
        @DisplayName("should set negative false from constructor")
        void shouldSetNegativeFalse() {
            ConditionSettings settings = new ConditionSettings(Target.ENEMY, false);

            assertFalse(settings.isNegative());
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("should set target via setter")
        void shouldSetTargetViaSetter() {
            ConditionSettings settings = new ConditionSettings();

            settings.setTarget(Target.ENEMY);

            assertEquals(Target.ENEMY, settings.getTarget());
        }

        @Test
        @DisplayName("should set negative via setter")
        void shouldSetNegativeViaSetter() {
            ConditionSettings settings = new ConditionSettings();

            settings.setNegative(true);

            assertTrue(settings.isNegative());
        }

        @Test
        @DisplayName("should allow changing target after construction")
        void shouldAllowChangingTarget() {
            ConditionSettings settings = new ConditionSettings(Target.PLAYER, false);

            settings.setTarget(Target.ENEMY);

            assertEquals(Target.ENEMY, settings.getTarget());
        }

        @Test
        @DisplayName("should allow toggling negative")
        void shouldAllowTogglingNegative() {
            ConditionSettings settings = new ConditionSettings(Target.PLAYER, false);

            settings.setNegative(true);
            assertTrue(settings.isNegative());

            settings.setNegative(false);
            assertFalse(settings.isNegative());
        }

        @Test
        @DisplayName("should allow setting target to null")
        void shouldAllowNullTarget() {
            ConditionSettings settings = new ConditionSettings(Target.PLAYER, false);

            settings.setTarget(null);

            assertNull(settings.getTarget());
        }
    }
}
