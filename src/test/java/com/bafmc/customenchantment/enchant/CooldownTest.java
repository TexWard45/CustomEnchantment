package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Cooldown - manages time-based cooldown tracking.
 */
@DisplayName("Cooldown Tests")
class CooldownTest {

    private Cooldown cooldown;

    @BeforeEach
    void setUp() {
        cooldown = new Cooldown(5000L); // 5 second cooldown
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with countdown value")
        void shouldCreateWithCountdown() {
            Cooldown cd = new Cooldown(3000L);

            assertEquals(3000L, cd.getCountdown());
        }

        @Test
        @DisplayName("should create with zero countdown")
        void shouldCreateWithZeroCountdown() {
            Cooldown cd = new Cooldown(0L);

            assertEquals(0L, cd.getCountdown());
        }

        @Test
        @DisplayName("should not be in cooldown initially")
        void shouldNotBeInCooldownInitially() {
            assertFalse(cooldown.isInCooldown());
        }

        @Test
        @DisplayName("start should be 0 initially")
        void startShouldBeZeroInitially() {
            assertEquals(0L, cooldown.getStart());
        }

        @Test
        @DisplayName("end should be 0 initially")
        void endShouldBeZeroInitially() {
            assertEquals(0L, cooldown.getEnd());
        }
    }

    @Nested
    @DisplayName("start Tests")
    class StartTests {

        @Test
        @DisplayName("should be in cooldown after start")
        void shouldBeInCooldownAfterStart() {
            cooldown.start();

            assertTrue(cooldown.isInCooldown());
        }

        @Test
        @DisplayName("should set start time")
        void shouldSetStartTime() {
            long before = System.currentTimeMillis();
            cooldown.start();
            long after = System.currentTimeMillis();

            assertTrue(cooldown.getStart() >= before);
            assertTrue(cooldown.getStart() <= after);
        }

        @Test
        @DisplayName("should set end time to start plus countdown")
        void shouldSetEndTime() {
            cooldown.start();

            assertEquals(cooldown.getStart() + 5000L, cooldown.getEnd());
        }
    }

    @Nested
    @DisplayName("stop Tests")
    class StopTests {

        @Test
        @DisplayName("should not be in cooldown after stop")
        void shouldNotBeInCooldownAfterStop() {
            cooldown.start();
            cooldown.stop();

            assertFalse(cooldown.isInCooldown());
        }

        @Test
        @DisplayName("should reset start to 0")
        void shouldResetStartToZero() {
            cooldown.start();
            cooldown.stop();

            assertEquals(0L, cooldown.getStart());
        }

        @Test
        @DisplayName("should reset end to 0")
        void shouldResetEndToZero() {
            cooldown.start();
            cooldown.stop();

            assertEquals(0L, cooldown.getEnd());
        }
    }

    @Nested
    @DisplayName("isInCooldown Tests")
    class IsInCooldownTests {

        @Test
        @DisplayName("should return false when never started")
        void shouldReturnFalseWhenNeverStarted() {
            assertFalse(cooldown.isInCooldown());
        }

        @Test
        @DisplayName("should return false after stop")
        void shouldReturnFalseAfterStop() {
            cooldown.start();
            cooldown.stop();

            assertFalse(cooldown.isInCooldown());
        }

        @Test
        @DisplayName("should return true immediately after start")
        void shouldReturnTrueAfterStart() {
            cooldown.start();

            assertTrue(cooldown.isInCooldown());
        }

        @Test
        @DisplayName("should return false when cooldown expired with zero duration")
        void shouldReturnFalseForExpiredZeroDuration() throws InterruptedException {
            Cooldown zeroCd = new Cooldown(0L);
            zeroCd.start();

            // With zero duration, end = start + 0 = start.
            // isInCooldown returns false when currentTimeMillis() > end.
            // If check runs in the same millisecond, currentTimeMillis() == end,
            // so we wait 1ms to guarantee expiration.
            Thread.sleep(1);

            assertFalse(zeroCd.isInCooldown());
        }
    }

    @Nested
    @DisplayName("setStart Tests")
    class SetStartTests {

        @Test
        @DisplayName("should set start and end manually")
        void shouldSetStartAndEnd() {
            cooldown.setStart(1000L, 6000L);

            assertEquals(1000L, cooldown.getStart());
            assertEquals(6000L, cooldown.getEnd());
        }
    }

    @Nested
    @DisplayName("setCountdown Tests")
    class SetCountdownTests {

        @Test
        @DisplayName("should update countdown value")
        void shouldUpdateCountdown() {
            cooldown.setCountdown(10000L);

            assertEquals(10000L, cooldown.getCountdown());
        }
    }

    @Nested
    @DisplayName("clone Tests")
    class CloneTests {

        @Test
        @DisplayName("should create independent copy")
        void shouldCreateIndependentCopy() {
            cooldown.start();

            Cooldown cloned = cooldown.clone();

            assertNotSame(cooldown, cloned);
            assertEquals(cooldown.getCountdown(), cloned.getCountdown());
            assertEquals(cooldown.getStart(), cloned.getStart());
            assertEquals(cooldown.getEnd(), cloned.getEnd());
        }

        @Test
        @DisplayName("clone modifications should not affect original")
        void cloneModificationsShouldNotAffectOriginal() {
            Cooldown cloned = cooldown.clone();
            cloned.setCountdown(99999L);

            assertEquals(5000L, cooldown.getCountdown());
            assertEquals(99999L, cloned.getCountdown());
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("should return formatted string")
        void shouldReturnFormattedString() {
            String str = cooldown.toString();

            assertTrue(str.contains("Cooldown"));
            assertTrue(str.contains("start="));
            assertTrue(str.contains("end="));
            assertTrue(str.contains("countdown=5000"));
        }
    }
}
