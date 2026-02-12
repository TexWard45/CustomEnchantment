package com.bafmc.customenchantment.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ILine interface - line serialization/deserialization contract.
 */
@DisplayName("ILine Tests")
class ILineTest {

    /**
     * Test implementation of ILine interface for testing purposes.
     */
    static class TestLine implements ILine {
        private String data;

        public TestLine() {
            this.data = "";
        }

        public TestLine(String data) {
            this.data = data;
        }

        @Override
        public String toLine() {
            return data;
        }

        @Override
        public void fromLine(String line) {
            this.data = line;
        }

        public String getData() {
            return data;
        }
    }

    @Nested
    @DisplayName("Interface Contract Tests")
    class InterfaceContractTests {

        @Test
        @DisplayName("Should implement toLine method")
        void shouldImplementToLineMethod() {
            TestLine line = new TestLine("test data");

            String result = line.toLine();

            assertEquals("test data", result);
        }

        @Test
        @DisplayName("Should implement fromLine method")
        void shouldImplementFromLineMethod() {
            TestLine line = new TestLine();

            line.fromLine("loaded data");

            assertEquals("loaded data", line.getData());
        }

        @Test
        @DisplayName("toLine and fromLine should be symmetric")
        void toLineAndFromLineShouldBeSymmetric() {
            TestLine original = new TestLine("symmetric test");
            String serialized = original.toLine();

            TestLine restored = new TestLine();
            restored.fromLine(serialized);

            assertEquals(original.getData(), restored.getData());
        }
    }

    @Nested
    @DisplayName("Null Handling Tests")
    class NullHandlingTests {

        @Test
        @DisplayName("fromLine should handle null gracefully")
        void fromLineShouldHandleNullGracefully() {
            TestLine line = new TestLine("initial");

            line.fromLine(null);

            assertNull(line.getData());
        }

        @Test
        @DisplayName("toLine should handle null data")
        void toLineShouldHandleNullData() {
            TestLine line = new TestLine(null);

            String result = line.toLine();

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty string")
        void shouldHandleEmptyString() {
            TestLine line = new TestLine("");

            assertEquals("", line.toLine());

            TestLine restored = new TestLine();
            restored.fromLine("");
            assertEquals("", restored.getData());
        }

        @Test
        @DisplayName("Should handle whitespace only string")
        void shouldHandleWhitespaceOnlyString() {
            TestLine line = new TestLine("   ");

            assertEquals("   ", line.toLine());
        }

        @Test
        @DisplayName("Should handle special characters")
        void shouldHandleSpecialCharacters() {
            TestLine line = new TestLine("!@#$%^&*()_+-=[]{}|;':\",./<>?");

            assertEquals("!@#$%^&*()_+-=[]{}|;':\",./<>?", line.toLine());
        }

        @Test
        @DisplayName("Should handle newlines in data")
        void shouldHandleNewlinesInData() {
            TestLine line = new TestLine("line1\nline2\nline3");

            assertEquals("line1\nline2\nline3", line.toLine());
        }

        @Test
        @DisplayName("Should handle very long strings")
        void shouldHandleVeryLongStrings() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                sb.append("a");
            }
            String longString = sb.toString();

            TestLine line = new TestLine(longString);

            assertEquals(longString, line.toLine());
            assertEquals(10000, line.toLine().length());
        }
    }

    @Nested
    @DisplayName("Multiple Call Tests")
    class MultipleCallTests {

        @Test
        @DisplayName("Multiple toLine calls should return same value")
        void multipleToLineCallsShouldReturnSameValue() {
            TestLine line = new TestLine("consistent");

            assertEquals(line.toLine(), line.toLine());
            assertEquals("consistent", line.toLine());
            assertEquals("consistent", line.toLine());
        }

        @Test
        @DisplayName("fromLine should overwrite previous data")
        void fromLineShouldOverwritePreviousData() {
            TestLine line = new TestLine("initial");

            assertEquals("initial", line.getData());

            line.fromLine("updated");
            assertEquals("updated", line.getData());

            line.fromLine("final");
            assertEquals("final", line.getData());
        }
    }

    @Nested
    @DisplayName("Type Checking Tests")
    class TypeCheckingTests {

        @Test
        @DisplayName("ILine implementation should be assignable to ILine")
        void iLineImplementationShouldBeAssignableToILine() {
            ILine line = new TestLine("test");

            assertNotNull(line);
            assertTrue(line instanceof ILine);
        }

        @Test
        @DisplayName("toLine should return String type")
        void toLineShouldReturnStringType() {
            ILine line = new TestLine("test");

            Object result = line.toLine();

            assertTrue(result instanceof String);
        }
    }
}
