package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for EffectSettings - holds target, delay, period, and flags for effect execution.
 */
@DisplayName("EffectSettings Tests")
class EffectSettingsTest {

    @Nested
    @DisplayName("Default Constructor Tests")
    class DefaultConstructorTests {

        @Test
        @DisplayName("should create with null fields")
        void shouldCreateWithNullFields() {
            EffectSettings settings = new EffectSettings();

            assertNull(settings.getName());
            assertNull(settings.getTarget());
            assertNull(settings.getTargetOther());
            assertNull(settings.getTargetFilter());
            assertEquals(0, settings.getDelay());
            assertEquals(0, settings.getPeriod());
            assertFalse(settings.isEffectAfterDead());
            assertFalse(settings.isEffectOnFakeSource());
        }
    }

    @Nested
    @DisplayName("Parameterized Constructor Tests")
    class ParameterizedConstructorTests {

        @Test
        @DisplayName("should create with all parameters")
        void shouldCreateWithAllParams() {
            TargetFilter mockFilter = mock(TargetFilter.class);
            EffectSettings settings = new EffectSettings(
                    "test_effect",
                    Target.PLAYER,
                    Target.ENEMY,
                    mockFilter,
                    10L,
                    20L,
                    true,
                    true
            );

            assertEquals("test_effect", settings.getName());
            assertEquals(Target.PLAYER, settings.getTarget());
            assertEquals(Target.ENEMY, settings.getTargetOther());
            assertSame(mockFilter, settings.getTargetFilter());
            assertEquals(10L, settings.getDelay());
            assertEquals(20L, settings.getPeriod());
            assertTrue(settings.isEffectAfterDead());
            assertTrue(settings.isEffectOnFakeSource());
        }
    }

    @Nested
    @DisplayName("Setter Tests")
    class SetterTests {

        @Test
        @DisplayName("should set name")
        void shouldSetName() {
            EffectSettings settings = new EffectSettings();
            settings.setName("new_name");

            assertEquals("new_name", settings.getName());
        }

        @Test
        @DisplayName("should set target")
        void shouldSetTarget() {
            EffectSettings settings = new EffectSettings();
            settings.setTarget(Target.ENEMY);

            assertEquals(Target.ENEMY, settings.getTarget());
        }

        @Test
        @DisplayName("should set target other")
        void shouldSetTargetOther() {
            EffectSettings settings = new EffectSettings();
            settings.setTargetOther(Target.PLAYER);

            assertEquals(Target.PLAYER, settings.getTargetOther());
        }

        @Test
        @DisplayName("should set target filter")
        void shouldSetTargetFilter() {
            EffectSettings settings = new EffectSettings();
            TargetFilter filter = mock(TargetFilter.class);
            settings.setTargetFilter(filter);

            assertSame(filter, settings.getTargetFilter());
        }

        @Test
        @DisplayName("should set delay")
        void shouldSetDelay() {
            EffectSettings settings = new EffectSettings();
            settings.setDelay(100L);

            assertEquals(100L, settings.getDelay());
        }

        @Test
        @DisplayName("should set period")
        void shouldSetPeriod() {
            EffectSettings settings = new EffectSettings();
            settings.setPeriod(50L);

            assertEquals(50L, settings.getPeriod());
        }

        @Test
        @DisplayName("should set effectAfterDead")
        void shouldSetEffectAfterDead() {
            EffectSettings settings = new EffectSettings();
            settings.setEffectAfterDead(true);

            assertTrue(settings.isEffectAfterDead());
        }

        @Test
        @DisplayName("should set effectOnFakeSource")
        void shouldSetEffectOnFakeSource() {
            EffectSettings settings = new EffectSettings();
            settings.setEffectOnFakeSource(true);

            assertTrue(settings.isEffectOnFakeSource());
        }
    }

    @Nested
    @DisplayName("clone Tests")
    class CloneTests {

        @Test
        @DisplayName("should create independent copy")
        void shouldCreateIndependentCopy() {
            TargetFilter mockFilter = mock(TargetFilter.class);
            EffectSettings original = new EffectSettings("test", Target.PLAYER, Target.ENEMY,
                    mockFilter, 10L, 20L, true, false);

            EffectSettings cloned = original.clone();

            assertNotSame(original, cloned);
            assertEquals(original.getName(), cloned.getName());
            assertEquals(original.getTarget(), cloned.getTarget());
            assertEquals(original.getTargetOther(), cloned.getTargetOther());
            assertEquals(original.getDelay(), cloned.getDelay());
            assertEquals(original.getPeriod(), cloned.getPeriod());
            assertEquals(original.isEffectAfterDead(), cloned.isEffectAfterDead());
            assertEquals(original.isEffectOnFakeSource(), cloned.isEffectOnFakeSource());
        }

        @Test
        @DisplayName("clone modifications should not affect original")
        void cloneModsShouldNotAffectOriginal() {
            EffectSettings original = new EffectSettings("orig", Target.PLAYER, Target.ENEMY,
                    null, 10L, 20L, false, false);

            EffectSettings cloned = original.clone();
            cloned.setName("modified");
            cloned.setDelay(999L);

            assertEquals("orig", original.getName());
            assertEquals(10L, original.getDelay());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle zero delay and period")
        void shouldHandleZeroDelayAndPeriod() {
            EffectSettings settings = new EffectSettings("test", Target.PLAYER, Target.ENEMY,
                    null, 0L, 0L, false, false);

            assertEquals(0L, settings.getDelay());
            assertEquals(0L, settings.getPeriod());
        }

        @Test
        @DisplayName("should handle negative delay")
        void shouldHandleNegativeDelay() {
            EffectSettings settings = new EffectSettings();
            settings.setDelay(-1L);

            assertEquals(-1L, settings.getDelay());
        }
    }
}
