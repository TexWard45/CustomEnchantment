package com.bafmc.customenchantment.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ITrade interface - import/export data transfer contract.
 */
@DisplayName("ITrade Tests")
class ITradeTest {

    /**
     * Simple data class for testing.
     */
    static class TestData {
        private String value;
        private int number;

        public TestData() {
            this.value = "";
            this.number = 0;
        }

        public TestData(String value, int number) {
            this.value = value;
            this.number = number;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

    /**
     * Test implementation of ITrade interface.
     */
    static class TestTrade implements ITrade<TestData> {
        private String internalValue;
        private int internalNumber;

        public TestTrade() {
            this.internalValue = "";
            this.internalNumber = 0;
        }

        public TestTrade(String value, int number) {
            this.internalValue = value;
            this.internalNumber = number;
        }

        @Override
        public void importFrom(TestData source) {
            if (source != null) {
                this.internalValue = source.getValue();
                this.internalNumber = source.getNumber();
            }
        }

        @Override
        public TestData exportTo() {
            return new TestData(internalValue, internalNumber);
        }

        public String getInternalValue() {
            return internalValue;
        }

        public int getInternalNumber() {
            return internalNumber;
        }
    }

    @Nested
    @DisplayName("Interface Contract Tests")
    class InterfaceContractTests {

        @Test
        @DisplayName("Should implement importFrom method")
        void shouldImplementImportFromMethod() {
            TestTrade trade = new TestTrade();
            TestData source = new TestData("imported", 42);

            trade.importFrom(source);

            assertEquals("imported", trade.getInternalValue());
            assertEquals(42, trade.getInternalNumber());
        }

        @Test
        @DisplayName("Should implement exportTo method")
        void shouldImplementExportToMethod() {
            TestTrade trade = new TestTrade("exported", 100);

            TestData result = trade.exportTo();

            assertNotNull(result);
            assertEquals("exported", result.getValue());
            assertEquals(100, result.getNumber());
        }

        @Test
        @DisplayName("importFrom and exportTo should be symmetric")
        void importFromAndExportToShouldBeSymmetric() {
            TestData original = new TestData("symmetric", 999);

            TestTrade trade = new TestTrade();
            trade.importFrom(original);
            TestData exported = trade.exportTo();

            assertEquals(original.getValue(), exported.getValue());
            assertEquals(original.getNumber(), exported.getNumber());
        }
    }

    @Nested
    @DisplayName("Import Tests")
    class ImportTests {

        @Test
        @DisplayName("importFrom should update internal state")
        void importFromShouldUpdateInternalState() {
            TestTrade trade = new TestTrade("old", 1);

            trade.importFrom(new TestData("new", 2));

            assertEquals("new", trade.getInternalValue());
            assertEquals(2, trade.getInternalNumber());
        }

        @Test
        @DisplayName("importFrom should handle null source gracefully")
        void importFromShouldHandleNullSourceGracefully() {
            TestTrade trade = new TestTrade("initial", 10);

            trade.importFrom(null);

            // Should not change internal state with null source
            assertEquals("initial", trade.getInternalValue());
            assertEquals(10, trade.getInternalNumber());
        }

        @Test
        @DisplayName("importFrom should handle source with null fields")
        void importFromShouldHandleSourceWithNullFields() {
            TestTrade trade = new TestTrade();
            TestData source = new TestData(null, 5);

            trade.importFrom(source);

            assertNull(trade.getInternalValue());
            assertEquals(5, trade.getInternalNumber());
        }

        @Test
        @DisplayName("Multiple importFrom calls should overwrite data")
        void multipleImportFromCallsShouldOverwriteData() {
            TestTrade trade = new TestTrade();

            trade.importFrom(new TestData("first", 1));
            assertEquals("first", trade.getInternalValue());

            trade.importFrom(new TestData("second", 2));
            assertEquals("second", trade.getInternalValue());

            trade.importFrom(new TestData("third", 3));
            assertEquals("third", trade.getInternalValue());
        }
    }

    @Nested
    @DisplayName("Export Tests")
    class ExportTests {

        @Test
        @DisplayName("exportTo should create new instance")
        void exportToShouldCreateNewInstance() {
            TestTrade trade = new TestTrade("value", 42);

            TestData export1 = trade.exportTo();
            TestData export2 = trade.exportTo();

            assertNotSame(export1, export2);
        }

        @Test
        @DisplayName("exportTo should export current state")
        void exportToShouldExportCurrentState() {
            TestTrade trade = new TestTrade("current", 100);

            TestData result = trade.exportTo();

            assertEquals("current", result.getValue());
            assertEquals(100, result.getNumber());
        }

        @Test
        @DisplayName("exportTo should handle empty/default state")
        void exportToShouldHandleEmptyDefaultState() {
            TestTrade trade = new TestTrade();

            TestData result = trade.exportTo();

            assertEquals("", result.getValue());
            assertEquals(0, result.getNumber());
        }

        @Test
        @DisplayName("Multiple exportTo calls should return consistent data")
        void multipleExportToCallsShouldReturnConsistentData() {
            TestTrade trade = new TestTrade("consistent", 50);

            TestData export1 = trade.exportTo();
            TestData export2 = trade.exportTo();
            TestData export3 = trade.exportTo();

            assertEquals(export1.getValue(), export2.getValue());
            assertEquals(export2.getValue(), export3.getValue());
            assertEquals(export1.getNumber(), export2.getNumber());
            assertEquals(export2.getNumber(), export3.getNumber());
        }
    }

    @Nested
    @DisplayName("Round Trip Tests")
    class RoundTripTests {

        @Test
        @DisplayName("Data should survive import/export round trip")
        void dataShouldSurviveImportExportRoundTrip() {
            TestData original = new TestData("round trip", 12345);

            // First trade
            TestTrade trade1 = new TestTrade();
            trade1.importFrom(original);
            TestData intermediate = trade1.exportTo();

            // Second trade
            TestTrade trade2 = new TestTrade();
            trade2.importFrom(intermediate);
            TestData final_ = trade2.exportTo();

            assertEquals(original.getValue(), final_.getValue());
            assertEquals(original.getNumber(), final_.getNumber());
        }

        @Test
        @DisplayName("Multiple round trips should preserve data")
        void multipleRoundTripsShouldPreserveData() {
            TestData data = new TestData("preserve", 999);

            for (int i = 0; i < 10; i++) {
                TestTrade trade = new TestTrade();
                trade.importFrom(data);
                data = trade.exportTo();
            }

            assertEquals("preserve", data.getValue());
            assertEquals(999, data.getNumber());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty string value")
        void shouldHandleEmptyStringValue() {
            TestTrade trade = new TestTrade("", 0);

            TestData result = trade.exportTo();

            assertEquals("", result.getValue());
        }

        @Test
        @DisplayName("Should handle special characters")
        void shouldHandleSpecialCharacters() {
            TestData source = new TestData("!@#$%^&*()_+-=[]{}|;':\",./<>?", -1);

            TestTrade trade = new TestTrade();
            trade.importFrom(source);
            TestData result = trade.exportTo();

            assertEquals(source.getValue(), result.getValue());
        }

        @Test
        @DisplayName("Should handle very large numbers")
        void shouldHandleVeryLargeNumbers() {
            TestData source = new TestData("large", Integer.MAX_VALUE);

            TestTrade trade = new TestTrade();
            trade.importFrom(source);
            TestData result = trade.exportTo();

            assertEquals(Integer.MAX_VALUE, result.getNumber());
        }

        @Test
        @DisplayName("Should handle negative numbers")
        void shouldHandleNegativeNumbers() {
            TestData source = new TestData("negative", Integer.MIN_VALUE);

            TestTrade trade = new TestTrade();
            trade.importFrom(source);
            TestData result = trade.exportTo();

            assertEquals(Integer.MIN_VALUE, result.getNumber());
        }
    }

    @Nested
    @DisplayName("Type Checking Tests")
    class TypeCheckingTests {

        @Test
        @DisplayName("ITrade implementation should be assignable to ITrade")
        void iTradeImplementationShouldBeAssignableToITrade() {
            ITrade<TestData> trade = new TestTrade("test", 1);

            assertNotNull(trade);
            assertTrue(trade instanceof ITrade);
        }

        @Test
        @DisplayName("exportTo should return correct generic type")
        void exportToShouldReturnCorrectGenericType() {
            ITrade<TestData> trade = new TestTrade("test", 1);

            TestData result = trade.exportTo();

            assertTrue(result instanceof TestData);
        }
    }
}
