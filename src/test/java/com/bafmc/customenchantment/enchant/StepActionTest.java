package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for StepAction enum - controls execution flow (CONTINUE or BREAK).
 */
@DisplayName("StepAction Tests")
class StepActionTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValuesTests {

        @Test
        @DisplayName("should have exactly 2 step actions")
        void shouldHaveTwoValues() {
            assertEquals(2, StepAction.values().length);
        }

        @Test
        @DisplayName("CONTINUE should have isContinue=true and isBreak=false")
        void continueShouldHaveCorrectFlags() {
            assertTrue(StepAction.CONTINUE.isContinue());
            assertFalse(StepAction.CONTINUE.isBreak());
        }

        @Test
        @DisplayName("BREAK should have isContinue=false and isBreak=true")
        void breakShouldHaveCorrectFlags() {
            assertFalse(StepAction.BREAK.isContinue());
            assertTrue(StepAction.BREAK.isBreak());
        }
    }

    @Nested
    @DisplayName("valueOf(boolean) Tests")
    class ValueOfBooleanTests {

        @Test
        @DisplayName("should return BREAK when isBreak is true")
        void shouldReturnBreakWhenTrue() {
            assertEquals(StepAction.BREAK, StepAction.valueOf(true));
        }

        @Test
        @DisplayName("should return CONTINUE when isBreak is false")
        void shouldReturnContinueWhenFalse() {
            assertEquals(StepAction.CONTINUE, StepAction.valueOf(false));
        }
    }

    @Nested
    @DisplayName("valueOf(boolean, StepAction) Tests")
    class ValueOfBooleanStepActionTests {

        @ParameterizedTest(name = "isBreak={0}, oldAction={1} should return {2}")
        @CsvSource({
                "true,  CONTINUE, BREAK",
                "true,  BREAK,    BREAK",
                "false, BREAK,    BREAK",
                "false, CONTINUE, CONTINUE"
        })
        @DisplayName("should respect old step action override")
        void shouldRespectOldStepAction(boolean isBreak, String oldActionStr, String expectedStr) {
            StepAction oldAction = StepAction.valueOf(oldActionStr);
            StepAction expected = StepAction.valueOf(expectedStr);
            assertEquals(expected, StepAction.valueOf(isBreak, oldAction));
        }

        @Test
        @DisplayName("should propagate BREAK from old step action regardless of isBreak flag")
        void shouldPropagateBreakFromOldAction() {
            // If old action is BREAK, result is always BREAK
            assertEquals(StepAction.BREAK, StepAction.valueOf(false, StepAction.BREAK));
            assertEquals(StepAction.BREAK, StepAction.valueOf(true, StepAction.BREAK));
        }

        @Test
        @DisplayName("should use isBreak flag when old action is CONTINUE")
        void shouldUseIsBreakWhenOldIsContinue() {
            assertEquals(StepAction.CONTINUE, StepAction.valueOf(false, StepAction.CONTINUE));
            assertEquals(StepAction.BREAK, StepAction.valueOf(true, StepAction.CONTINUE));
        }
    }

    @Nested
    @DisplayName("isContinue and isBreak are mutually exclusive")
    class MutuallyExclusiveTests {

        @Test
        @DisplayName("CONTINUE flags should be mutually exclusive")
        void continueExclusive() {
            assertNotEquals(StepAction.CONTINUE.isContinue(), StepAction.CONTINUE.isBreak());
        }

        @Test
        @DisplayName("BREAK flags should be mutually exclusive")
        void breakExclusive() {
            assertNotEquals(StepAction.BREAK.isContinue(), StepAction.BREAK.isBreak());
        }
    }
}
