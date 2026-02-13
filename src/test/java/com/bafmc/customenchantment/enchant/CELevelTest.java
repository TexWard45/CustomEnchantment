package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CELevel - holds a map of CEFunction instances for a specific enchant level.
 */
@DisplayName("CELevel Tests")
class CELevelTest {

    private CELevel ceLevel;
    private LinkedHashMap<String, CEFunction> functionMap;

    @BeforeEach
    void setUp() {
        functionMap = new LinkedHashMap<>();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with empty function map")
        void shouldCreateWithEmptyMap() {
            ceLevel = new CELevel(functionMap);

            assertNotNull(ceLevel);
            assertNotNull(ceLevel.getFunctionMap());
            assertTrue(ceLevel.getFunctionMap().isEmpty());
        }

        @Test
        @DisplayName("should create with populated function map")
        void shouldCreateWithPopulatedMap() {
            CEFunction mockFunc = mock(CEFunction.class);
            functionMap.put("func1", mockFunc);

            ceLevel = new CELevel(functionMap);

            assertEquals(1, ceLevel.getFunctionMap().size());
        }
    }

    @Nested
    @DisplayName("getFunctionList Tests")
    class GetFunctionListTests {

        @Test
        @DisplayName("should return empty list for empty map")
        void shouldReturnEmptyList() {
            ceLevel = new CELevel(functionMap);

            List<CEFunction> list = ceLevel.getFunctionList();

            assertNotNull(list);
            assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("should return functions in insertion order")
        void shouldReturnFunctionsInOrder() {
            CEFunction func1 = mock(CEFunction.class);
            CEFunction func2 = mock(CEFunction.class);
            CEFunction func3 = mock(CEFunction.class);

            functionMap.put("first", func1);
            functionMap.put("second", func2);
            functionMap.put("third", func3);

            ceLevel = new CELevel(functionMap);

            List<CEFunction> list = ceLevel.getFunctionList();
            assertEquals(3, list.size());
            assertSame(func1, list.get(0));
            assertSame(func2, list.get(1));
            assertSame(func3, list.get(2));
        }

        @Test
        @DisplayName("should return new ArrayList instance each call")
        void shouldReturnNewListInstance() {
            CEFunction func = mock(CEFunction.class);
            functionMap.put("func", func);

            ceLevel = new CELevel(functionMap);

            List<CEFunction> list1 = ceLevel.getFunctionList();
            List<CEFunction> list2 = ceLevel.getFunctionList();

            assertNotSame(list1, list2);
            assertEquals(list1, list2);
        }
    }

    @Nested
    @DisplayName("getFunctionMap Tests")
    class GetFunctionMapTests {

        @Test
        @DisplayName("should return the same map reference")
        void shouldReturnSameMapReference() {
            ceLevel = new CELevel(functionMap);

            assertSame(functionMap, ceLevel.getFunctionMap());
        }

        @Test
        @DisplayName("should reflect additions to the original map")
        void shouldReflectMapChanges() {
            ceLevel = new CELevel(functionMap);

            CEFunction func = mock(CEFunction.class);
            functionMap.put("newFunc", func);

            assertEquals(1, ceLevel.getFunctionMap().size());
            assertTrue(ceLevel.getFunctionMap().containsKey("newFunc"));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle single function")
        void shouldHandleSingleFunction() {
            CEFunction func = mock(CEFunction.class);
            functionMap.put("only", func);

            ceLevel = new CELevel(functionMap);

            assertEquals(1, ceLevel.getFunctionList().size());
            assertSame(func, ceLevel.getFunctionList().get(0));
        }

        @Test
        @DisplayName("should handle many functions")
        void shouldHandleManyFunctions() {
            for (int i = 0; i < 100; i++) {
                functionMap.put("func_" + i, mock(CEFunction.class));
            }

            ceLevel = new CELevel(functionMap);

            assertEquals(100, ceLevel.getFunctionList().size());
        }
    }
}
