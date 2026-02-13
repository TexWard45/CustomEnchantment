package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionInFactionTerriority - checks if player is in their faction territory.
 * Note: FactionAPI requires Factions library (compileOnly) which is not available at test time.
 * Tests focus on identifier, setup, and class structure. Match behavior depends on FactionAPI
 * which cannot be mocked without the Factions dependency classes loaded.
 */
@DisplayName("ConditionInFactionTerriority Tests")
class ConditionInFactionTerriorityTest {

    private ConditionInFactionTerriority condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionInFactionTerriority();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return IN_FACTION_TERRIORITY")
        void shouldReturnCorrectIdentifier() {
            assertEquals("IN_FACTION_TERRIORITY", condition.getIdentify());
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
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("should extend ConditionHook")
        void shouldExtendConditionHook() {
            assertInstanceOf(ConditionHook.class, condition);
        }

        @Test
        @DisplayName("should have match method accepting CEFunctionData")
        void shouldHaveMatchMethod() {
            try {
                var method = ConditionInFactionTerriority.class.getMethod("match", CEFunctionData.class);
                assertNotNull(method);
                assertEquals(boolean.class, method.getReturnType());
            } catch (NoSuchMethodException e) {
                fail("Should have match(CEFunctionData) method");
            }
        }

        @Test
        @DisplayName("should be cloneable via ConditionHook")
        void shouldBeCloneable() {
            ConditionHook cloned = condition.clone();
            assertNotNull(cloned);
            assertInstanceOf(ConditionInFactionTerriority.class, cloned);
            assertEquals(condition.getIdentify(), cloned.getIdentify());
        }
    }
}
