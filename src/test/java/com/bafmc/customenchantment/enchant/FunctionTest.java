package com.bafmc.customenchantment.enchant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Function - combines Condition, Effect, and CEFunctionData for execution.
 */
@DisplayName("Function Tests")
class FunctionTest {

    private Condition mockCondition;
    private Effect mockEffect;
    private CEFunctionData mockData;
    private Function function;

    @BeforeEach
    void setUp() {
        mockCondition = mock(Condition.class);
        mockEffect = mock(Effect.class);
        mockData = mock(CEFunctionData.class);

        function = new Function(mockCondition, mockEffect, mockData);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with all parameters")
        void shouldCreateWithAllParams() {
            assertNotNull(function);
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("should return condition")
        void shouldReturnCondition() {
            assertSame(mockCondition, function.getCondition());
        }

        @Test
        @DisplayName("should return effect")
        void shouldReturnEffect() {
            assertSame(mockEffect, function.getEffect());
        }

        @Test
        @DisplayName("should return data")
        void shouldReturnData() {
            assertSame(mockData, function.getData());
        }
    }

    @Nested
    @DisplayName("call Tests")
    class CallTests {

        @Test
        @DisplayName("should execute effect when condition passes")
        void shouldExecuteWhenConditionPasses() {
            when(mockCondition.check(mockData)).thenReturn(true);

            function.call();

            verify(mockEffect).execute(mockData);
        }

        @Test
        @DisplayName("should not execute effect when condition fails")
        void shouldNotExecuteWhenConditionFails() {
            when(mockCondition.check(mockData)).thenReturn(false);

            function.call();

            verify(mockEffect, never()).execute(any());
        }

        @Test
        @DisplayName("should always check condition")
        void shouldAlwaysCheckCondition() {
            when(mockCondition.check(mockData)).thenReturn(false);

            function.call();

            verify(mockCondition).check(mockData);
        }

        @Test
        @DisplayName("should pass data to both condition and effect")
        void shouldPassDataToBoth() {
            when(mockCondition.check(mockData)).thenReturn(true);

            function.call();

            verify(mockCondition).check(mockData);
            verify(mockEffect).execute(mockData);
        }
    }
}
