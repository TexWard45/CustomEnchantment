package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.attribute.RangeAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CECallerList - extends ArrayList of CECaller with result aggregation.
 */
@DisplayName("CECallerList Tests")
class CECallerListTest {

    private CECallerList callerList;

    @BeforeEach
    void setUp() {
        callerList = new CECallerList();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create empty list")
        void shouldCreateEmptyList() {
            assertTrue(callerList.isEmpty());
        }

        @Test
        @DisplayName("should extend ArrayList")
        void shouldExtendArrayList() {
            assertTrue(callerList instanceof ArrayList);
        }
    }

    @Nested
    @DisplayName("getResults Tests")
    class GetResultsTests {

        @Test
        @DisplayName("should return empty list when no callers")
        void shouldReturnEmptyWhenNoCallers() {
            List<CECallerResult> results = callerList.getResults();

            assertNotNull(results);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("should return results from all callers")
        void shouldReturnResultsFromAllCallers() {
            CECaller mockCaller1 = mock(CECaller.class);
            CECaller mockCaller2 = mock(CECaller.class);
            CECallerResult result1 = CECallerResult.instance();
            CECallerResult result2 = CECallerResult.instance();

            when(mockCaller1.getResult()).thenReturn(result1);
            when(mockCaller2.getResult()).thenReturn(result2);

            callerList.add(mockCaller1);
            callerList.add(mockCaller2);

            List<CECallerResult> results = callerList.getResults();

            assertEquals(2, results.size());
            assertSame(result1, results.get(0));
            assertSame(result2, results.get(1));
        }

        @Test
        @DisplayName("should return single result for single caller")
        void shouldReturnSingleResult() {
            CECaller mockCaller = mock(CECaller.class);
            CECallerResult result = CECallerResult.instance();
            when(mockCaller.getResult()).thenReturn(result);

            callerList.add(mockCaller);

            assertEquals(1, callerList.getResults().size());
        }
    }

    @Nested
    @DisplayName("getOptionDataList Tests")
    class GetOptionDataListTests {

        @Test
        @DisplayName("should return empty list when no callers")
        void shouldReturnEmptyWhenNoCallers() {
            assertTrue(callerList.getOptionDataList().isEmpty());
        }

        @Test
        @DisplayName("should aggregate option data from all callers")
        void shouldAggregateOptionData() {
            RangeAttribute attr1 = mock(RangeAttribute.class);
            RangeAttribute attr2 = mock(RangeAttribute.class);
            RangeAttribute attr3 = mock(RangeAttribute.class);

            CECallerResult result1 = CECallerResult.instance();
            result1.getOptionDataList().add(attr1);
            result1.getOptionDataList().add(attr2);

            CECallerResult result2 = CECallerResult.instance();
            result2.getOptionDataList().add(attr3);

            CECaller mockCaller1 = mock(CECaller.class);
            CECaller mockCaller2 = mock(CECaller.class);
            when(mockCaller1.getResult()).thenReturn(result1);
            when(mockCaller2.getResult()).thenReturn(result2);

            callerList.add(mockCaller1);
            callerList.add(mockCaller2);

            List<?> optionData = callerList.getOptionDataList();

            assertEquals(3, optionData.size());
        }

        @Test
        @DisplayName("should return empty when callers have no option data")
        void shouldReturnEmptyWhenNoOptionData() {
            CECaller mockCaller = mock(CECaller.class);
            when(mockCaller.getResult()).thenReturn(CECallerResult.instance());

            callerList.add(mockCaller);

            assertTrue(callerList.getOptionDataList().isEmpty());
        }
    }

    @Nested
    @DisplayName("ArrayList Operations Tests")
    class ArrayListOperationsTests {

        @Test
        @DisplayName("should support add and size")
        void shouldSupportAddAndSize() {
            callerList.add(mock(CECaller.class));
            callerList.add(mock(CECaller.class));

            assertEquals(2, callerList.size());
        }

        @Test
        @DisplayName("should support clear")
        void shouldSupportClear() {
            callerList.add(mock(CECaller.class));
            callerList.clear();

            assertTrue(callerList.isEmpty());
        }

        @Test
        @DisplayName("should support get by index")
        void shouldSupportGetByIndex() {
            CECaller caller = mock(CECaller.class);
            callerList.add(caller);

            assertSame(caller, callerList.get(0));
        }
    }
}
