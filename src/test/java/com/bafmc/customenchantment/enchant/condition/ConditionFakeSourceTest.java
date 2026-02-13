package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionFakeSource - checks if the damage source is fake/synthetic.
 */
@DisplayName("ConditionFakeSource Tests")
class ConditionFakeSourceTest {

    private ConditionFakeSource condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionFakeSource();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return FAKE_SOURCE")
        void shouldReturnCorrectIdentifier() {
            assertEquals("FAKE_SOURCE", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("setup Tests")
    class SetupTests {

        @Test
        @DisplayName("should accept empty args without error")
        void shouldAcceptEmptyArgs() {
            assertDoesNotThrow(() -> condition.setup(new String[]{}));
        }
    }

    @Nested
    @DisplayName("match Tests")
    class MatchTests {

        @Test
        @DisplayName("should return true when source is fake")
        void shouldReturnTrueWhenFakeSource() {
            when(mockData.isFakeSource()).thenReturn(true);

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when source is not fake")
        void shouldReturnFalseWhenNotFakeSource() {
            when(mockData.isFakeSource()).thenReturn(false);

            assertFalse(condition.match(mockData));
        }
    }
}
