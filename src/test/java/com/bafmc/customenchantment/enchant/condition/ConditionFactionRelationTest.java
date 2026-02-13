package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.enchant.CEFunctionData;
import com.bafmc.customenchantment.enchant.ConditionHook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionFactionRelation - checks if faction relation between player and enemy matches.
 * Note: FactionAPI requires Factions library (compileOnly) which is not available at test time.
 * Tests focus on identifier, class structure, and setup. Match behavior depends on FactionAPI
 * and Relation enum which cannot be loaded without the Factions dependency.
 */
@DisplayName("ConditionFactionRelation Tests")
class ConditionFactionRelationTest {

    private ConditionFactionRelation condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionFactionRelation();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return FACTION_RELATION")
        void shouldReturnCorrectIdentifier() {
            assertEquals("FACTION_RELATION", condition.getIdentify());
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
        @DisplayName("should have list field for relation types")
        void shouldHaveListField() {
            try {
                var field = ConditionFactionRelation.class.getDeclaredField("list");
                field.setAccessible(true);
                assertNotNull(field);
                assertEquals(java.util.List.class, field.getType());
            } catch (NoSuchFieldException e) {
                fail("Should have 'list' field");
            }
        }

        @Test
        @DisplayName("should have match method accepting CEFunctionData")
        void shouldHaveMatchMethod() {
            try {
                var method = ConditionFactionRelation.class.getMethod("match", CEFunctionData.class);
                assertNotNull(method);
                assertEquals(boolean.class, method.getReturnType());
            } catch (NoSuchMethodException e) {
                fail("Should have match(CEFunctionData) method");
            }
        }

        @Test
        @DisplayName("should have setup method accepting String array")
        void shouldHaveSetupMethod() {
            try {
                var method = ConditionFactionRelation.class.getMethod("setup", String[].class);
                assertNotNull(method);
            } catch (NoSuchMethodException e) {
                fail("Should have setup(String[]) method");
            }
        }

        @Test
        @DisplayName("should be cloneable via ConditionHook")
        void shouldBeCloneable() {
            ConditionHook cloned = condition.clone();
            assertNotNull(cloned);
            assertInstanceOf(ConditionFactionRelation.class, cloned);
            assertEquals(condition.getIdentify(), cloned.getIdentify());
        }
    }
}
