package com.bafmc.customenchantment.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CompareOperation enum - comparison operations for numeric values.
 */
@DisplayName("CompareOperation Tests")
class CompareOperationTest {

    @Nested
    @DisplayName("getOperation Tests")
    class GetOperationTests {

        @Test
        @DisplayName("Should return SMALLER for '<'")
        void shouldReturnSmallerForLessThan() {
            assertEquals(CompareOperation.SMALLER, CompareOperation.getOperation("<"));
        }

        @Test
        @DisplayName("Should return BIGGER for '>'")
        void shouldReturnBiggerForGreaterThan() {
            assertEquals(CompareOperation.BIGGER, CompareOperation.getOperation(">"));
        }

        @Test
        @DisplayName("Should return NOT_EQUALS for '!='")
        void shouldReturnNotEqualsForNotEquals() {
            assertEquals(CompareOperation.NOT_EQUALS, CompareOperation.getOperation("!="));
        }

        @Test
        @DisplayName("Should return EQUALS for '='")
        void shouldReturnEqualsForEquals() {
            assertEquals(CompareOperation.EQUALS, CompareOperation.getOperation("="));
        }

        @Test
        @DisplayName("Should return EQUALSIGNORECASE for '=='")
        void shouldReturnEqualsIgnoreCaseForDoubleEquals() {
            assertEquals(CompareOperation.EQUALSIGNORECASE, CompareOperation.getOperation("=="));
        }

        @Test
        @DisplayName("Should return SMALLEREQUALS for '<='")
        void shouldReturnSmallerEqualsForLessThanOrEqual() {
            assertEquals(CompareOperation.SMALLEREQUALS, CompareOperation.getOperation("<="));
        }

        @Test
        @DisplayName("Should return BIGGEREQUALS for '>='")
        void shouldReturnBiggerEqualsForGreaterThanOrEqual() {
            assertEquals(CompareOperation.BIGGEREQUALS, CompareOperation.getOperation(">="));
        }

        @Test
        @DisplayName("Should return null for invalid operation")
        void shouldReturnNullForInvalidOperation() {
            assertNull(CompareOperation.getOperation("invalid"));
        }

        @Test
        @DisplayName("Should return null for empty string")
        void shouldReturnNullForEmptyString() {
            assertNull(CompareOperation.getOperation(""));
        }

        @Test
        @DisplayName("Should return null for null input")
        void shouldReturnNullForNullInput() {
            assertNull(CompareOperation.getOperation(null));
        }
    }

    @Nested
    @DisplayName("Double compare Tests")
    class DoubleCompareTests {

        @Test
        @DisplayName("SMALLER should return true when arg0 < arg1")
        void smaller_shouldReturnTrueWhenArg0LessThanArg1() {
            assertTrue(CompareOperation.compare(5.0, 10.0, CompareOperation.SMALLER));
        }

        @Test
        @DisplayName("SMALLER should return false when arg0 >= arg1")
        void smaller_shouldReturnFalseWhenArg0GreaterOrEqual() {
            assertFalse(CompareOperation.compare(10.0, 5.0, CompareOperation.SMALLER));
            assertFalse(CompareOperation.compare(5.0, 5.0, CompareOperation.SMALLER));
        }

        @Test
        @DisplayName("BIGGER should return true when arg0 > arg1")
        void bigger_shouldReturnTrueWhenArg0GreaterThanArg1() {
            assertTrue(CompareOperation.compare(10.0, 5.0, CompareOperation.BIGGER));
        }

        @Test
        @DisplayName("BIGGER should return false when arg0 <= arg1")
        void bigger_shouldReturnFalseWhenArg0LessOrEqual() {
            assertFalse(CompareOperation.compare(5.0, 10.0, CompareOperation.BIGGER));
            assertFalse(CompareOperation.compare(5.0, 5.0, CompareOperation.BIGGER));
        }

        @Test
        @DisplayName("EQUALS should return true when arg0 == arg1")
        void equals_shouldReturnTrueWhenEqual() {
            assertTrue(CompareOperation.compare(5.0, 5.0, CompareOperation.EQUALS));
        }

        @Test
        @DisplayName("EQUALS should return false when arg0 != arg1")
        void equals_shouldReturnFalseWhenNotEqual() {
            assertFalse(CompareOperation.compare(5.0, 10.0, CompareOperation.EQUALS));
        }

        @Test
        @DisplayName("EQUALSIGNORECASE should behave same as EQUALS for doubles")
        void equalsIgnoreCase_shouldBehaveSameAsEquals() {
            assertTrue(CompareOperation.compare(5.0, 5.0, CompareOperation.EQUALSIGNORECASE));
            assertFalse(CompareOperation.compare(5.0, 10.0, CompareOperation.EQUALSIGNORECASE));
        }

        @Test
        @DisplayName("NOT_EQUALS should return true when arg0 != arg1")
        void notEquals_shouldReturnTrueWhenNotEqual() {
            assertTrue(CompareOperation.compare(5.0, 10.0, CompareOperation.NOT_EQUALS));
        }

        @Test
        @DisplayName("NOT_EQUALS should return false when arg0 == arg1")
        void notEquals_shouldReturnFalseWhenEqual() {
            assertFalse(CompareOperation.compare(5.0, 5.0, CompareOperation.NOT_EQUALS));
        }

        @Test
        @DisplayName("SMALLEREQUALS should return true when arg0 <= arg1")
        void smallerEquals_shouldReturnTrueWhenLessOrEqual() {
            assertTrue(CompareOperation.compare(5.0, 10.0, CompareOperation.SMALLEREQUALS));
            assertTrue(CompareOperation.compare(5.0, 5.0, CompareOperation.SMALLEREQUALS));
        }

        @Test
        @DisplayName("SMALLEREQUALS should return false when arg0 > arg1")
        void smallerEquals_shouldReturnFalseWhenGreater() {
            assertFalse(CompareOperation.compare(10.0, 5.0, CompareOperation.SMALLEREQUALS));
        }

        @Test
        @DisplayName("BIGGEREQUALS should return true when arg0 >= arg1")
        void biggerEquals_shouldReturnTrueWhenGreaterOrEqual() {
            assertTrue(CompareOperation.compare(10.0, 5.0, CompareOperation.BIGGEREQUALS));
            assertTrue(CompareOperation.compare(5.0, 5.0, CompareOperation.BIGGEREQUALS));
        }

        @Test
        @DisplayName("BIGGEREQUALS should return false when arg0 < arg1")
        void biggerEquals_shouldReturnFalseWhenLess() {
            assertFalse(CompareOperation.compare(5.0, 10.0, CompareOperation.BIGGEREQUALS));
        }
    }

    @Nested
    @DisplayName("Integer compare Tests")
    class IntegerCompareTests {

        @Test
        @DisplayName("SMALLER should return true when arg0 < arg1")
        void smaller_shouldReturnTrueWhenArg0LessThanArg1() {
            assertTrue(CompareOperation.compare(5, 10, CompareOperation.SMALLER));
        }

        @Test
        @DisplayName("SMALLER should return false when arg0 >= arg1")
        void smaller_shouldReturnFalseWhenArg0GreaterOrEqual() {
            assertFalse(CompareOperation.compare(10, 5, CompareOperation.SMALLER));
            assertFalse(CompareOperation.compare(5, 5, CompareOperation.SMALLER));
        }

        @Test
        @DisplayName("BIGGER should return true when arg0 > arg1")
        void bigger_shouldReturnTrueWhenArg0GreaterThanArg1() {
            assertTrue(CompareOperation.compare(10, 5, CompareOperation.BIGGER));
        }

        @Test
        @DisplayName("BIGGER should return false when arg0 <= arg1")
        void bigger_shouldReturnFalseWhenArg0LessOrEqual() {
            assertFalse(CompareOperation.compare(5, 10, CompareOperation.BIGGER));
            assertFalse(CompareOperation.compare(5, 5, CompareOperation.BIGGER));
        }

        @Test
        @DisplayName("EQUALS should return true when arg0 == arg1")
        void equals_shouldReturnTrueWhenEqual() {
            assertTrue(CompareOperation.compare(5, 5, CompareOperation.EQUALS));
        }

        @Test
        @DisplayName("EQUALS should return false when arg0 != arg1")
        void equals_shouldReturnFalseWhenNotEqual() {
            assertFalse(CompareOperation.compare(5, 10, CompareOperation.EQUALS));
        }

        @Test
        @DisplayName("EQUALSIGNORECASE should behave same as EQUALS for integers")
        void equalsIgnoreCase_shouldBehaveSameAsEquals() {
            assertTrue(CompareOperation.compare(5, 5, CompareOperation.EQUALSIGNORECASE));
            assertFalse(CompareOperation.compare(5, 10, CompareOperation.EQUALSIGNORECASE));
        }

        @Test
        @DisplayName("NOT_EQUALS should return true when arg0 != arg1")
        void notEquals_shouldReturnTrueWhenNotEqual() {
            assertTrue(CompareOperation.compare(5, 10, CompareOperation.NOT_EQUALS));
        }

        @Test
        @DisplayName("NOT_EQUALS should return false when arg0 == arg1")
        void notEquals_shouldReturnFalseWhenEqual() {
            assertFalse(CompareOperation.compare(5, 5, CompareOperation.NOT_EQUALS));
        }

        @Test
        @DisplayName("SMALLEREQUALS should return true when arg0 <= arg1")
        void smallerEquals_shouldReturnTrueWhenLessOrEqual() {
            assertTrue(CompareOperation.compare(5, 10, CompareOperation.SMALLEREQUALS));
            assertTrue(CompareOperation.compare(5, 5, CompareOperation.SMALLEREQUALS));
        }

        @Test
        @DisplayName("SMALLEREQUALS should return false when arg0 > arg1")
        void smallerEquals_shouldReturnFalseWhenGreater() {
            assertFalse(CompareOperation.compare(10, 5, CompareOperation.SMALLEREQUALS));
        }

        @Test
        @DisplayName("BIGGEREQUALS should return true when arg0 >= arg1")
        void biggerEquals_shouldReturnTrueWhenGreaterOrEqual() {
            assertTrue(CompareOperation.compare(10, 5, CompareOperation.BIGGEREQUALS));
            assertTrue(CompareOperation.compare(5, 5, CompareOperation.BIGGEREQUALS));
        }

        @Test
        @DisplayName("BIGGEREQUALS should return false when arg0 < arg1")
        void biggerEquals_shouldReturnFalseWhenLess() {
            assertFalse(CompareOperation.compare(5, 10, CompareOperation.BIGGEREQUALS));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle zero values for double comparison")
        void shouldHandleZeroValuesDouble() {
            assertTrue(CompareOperation.compare(0.0, 0.0, CompareOperation.EQUALS));
            assertTrue(CompareOperation.compare(-1.0, 0.0, CompareOperation.SMALLER));
            assertTrue(CompareOperation.compare(1.0, 0.0, CompareOperation.BIGGER));
        }

        @Test
        @DisplayName("Should handle zero values for integer comparison")
        void shouldHandleZeroValuesInteger() {
            assertTrue(CompareOperation.compare(0, 0, CompareOperation.EQUALS));
            assertTrue(CompareOperation.compare(-1, 0, CompareOperation.SMALLER));
            assertTrue(CompareOperation.compare(1, 0, CompareOperation.BIGGER));
        }

        @Test
        @DisplayName("Should handle negative values for double comparison")
        void shouldHandleNegativeValuesDouble() {
            assertTrue(CompareOperation.compare(-10.0, -5.0, CompareOperation.SMALLER));
            assertTrue(CompareOperation.compare(-5.0, -10.0, CompareOperation.BIGGER));
            assertTrue(CompareOperation.compare(-5.0, -5.0, CompareOperation.EQUALS));
        }

        @Test
        @DisplayName("Should handle negative values for integer comparison")
        void shouldHandleNegativeValuesInteger() {
            assertTrue(CompareOperation.compare(-10, -5, CompareOperation.SMALLER));
            assertTrue(CompareOperation.compare(-5, -10, CompareOperation.BIGGER));
            assertTrue(CompareOperation.compare(-5, -5, CompareOperation.EQUALS));
        }

        @Test
        @DisplayName("Should handle very large double values")
        void shouldHandleVeryLargeDoubleValues() {
            assertTrue(CompareOperation.compare(Double.MAX_VALUE, Double.MAX_VALUE, CompareOperation.EQUALS));
            assertTrue(CompareOperation.compare(0.0, Double.MAX_VALUE, CompareOperation.SMALLER));
        }

        @Test
        @DisplayName("Should handle very small double values")
        void shouldHandleVerySmallDoubleValues() {
            assertTrue(CompareOperation.compare(Double.MIN_VALUE, Double.MIN_VALUE, CompareOperation.EQUALS));
            assertTrue(CompareOperation.compare(Double.MIN_VALUE, 1.0, CompareOperation.SMALLER));
        }

        @Test
        @DisplayName("Should handle Integer.MAX_VALUE")
        void shouldHandleIntegerMaxValue() {
            assertTrue(CompareOperation.compare(Integer.MAX_VALUE, Integer.MAX_VALUE, CompareOperation.EQUALS));
            assertTrue(CompareOperation.compare(0, Integer.MAX_VALUE, CompareOperation.SMALLER));
        }

        @Test
        @DisplayName("Should handle Integer.MIN_VALUE")
        void shouldHandleIntegerMinValue() {
            assertTrue(CompareOperation.compare(Integer.MIN_VALUE, Integer.MIN_VALUE, CompareOperation.EQUALS));
            assertTrue(CompareOperation.compare(Integer.MIN_VALUE, 0, CompareOperation.SMALLER));
        }
    }

    @Nested
    @DisplayName("Enum Property Tests")
    class EnumPropertyTests {

        @Test
        @DisplayName("All operations should have correct operation string")
        void allOperationsShouldHaveCorrectOperationString() {
            assertEquals("<", CompareOperation.SMALLER.operation);
            assertEquals(">", CompareOperation.BIGGER.operation);
            assertEquals("!=", CompareOperation.NOT_EQUALS.operation);
            assertEquals("=", CompareOperation.EQUALS.operation);
            assertEquals("==", CompareOperation.EQUALSIGNORECASE.operation);
            assertEquals("<=", CompareOperation.SMALLEREQUALS.operation);
            assertEquals(">=", CompareOperation.BIGGEREQUALS.operation);
        }

        @Test
        @DisplayName("Should have exactly 7 operations")
        void shouldHaveExactly7Operations() {
            assertEquals(7, CompareOperation.values().length);
        }

        @Test
        @DisplayName("getOperation should be inverse of operation property")
        void getOperationShouldBeInverseOfOperationProperty() {
            for (CompareOperation op : CompareOperation.values()) {
                assertEquals(op, CompareOperation.getOperation(op.operation));
            }
        }
    }
}
