package com.bafmc.customenchantment.enchant.condition;

import com.bafmc.customenchantment.api.EntityTypeList;
import com.bafmc.customenchantment.enchant.CEFunctionData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConditionEntityType - checks if living entity matches expected entity type.
 */
@DisplayName("ConditionEntityType Tests")
class ConditionEntityTypeTest {

    private ConditionEntityType condition;
    private CEFunctionData mockData;

    @BeforeEach
    void setUp() {
        condition = new ConditionEntityType();
        mockData = mock(CEFunctionData.class);
    }

    @Nested
    @DisplayName("getIdentify Tests")
    class GetIdentifyTests {

        @Test
        @DisplayName("should return ENTITY_TYPE")
        void shouldReturnCorrectIdentifier() {
            assertEquals("ENTITY_TYPE", condition.getIdentify());
        }
    }

    @Nested
    @DisplayName("match Tests - Null Entity")
    class NullEntityTests {

        @Test
        @DisplayName("should return false when living entity is null")
        void shouldReturnFalseWhenEntityNull() {
            when(mockData.getLivingEntity()).thenReturn(null);

            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);

            try {
                var field = ConditionEntityType.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            assertFalse(condition.match(mockData));
        }
    }

    @Nested
    @DisplayName("match Tests - With Entity")
    class WithEntityTests {

        @Test
        @DisplayName("should return true when entity type is in list")
        void shouldReturnTrueWhenEntityTypeMatches() {
            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.getType()).thenReturn(EntityType.ZOMBIE);

            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);
            list.add(EntityType.SKELETON);

            try {
                var field = ConditionEntityType.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            assertTrue(condition.match(mockData));
        }

        @Test
        @DisplayName("should return false when entity type is not in list")
        void shouldReturnFalseWhenEntityTypeDoesNotMatch() {
            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.getType()).thenReturn(EntityType.CREEPER);

            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.ZOMBIE);
            list.add(EntityType.SKELETON);

            try {
                var field = ConditionEntityType.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            assertFalse(condition.match(mockData));
        }

        @Test
        @DisplayName("should return true for PLAYER entity type")
        void shouldReturnTrueForPlayerEntityType() {
            LivingEntity mockEntity = mock(LivingEntity.class);
            when(mockData.getLivingEntity()).thenReturn(mockEntity);
            when(mockEntity.getType()).thenReturn(EntityType.PLAYER);

            EntityTypeList list = new EntityTypeList();
            list.add(EntityType.PLAYER);

            try {
                var field = ConditionEntityType.class.getDeclaredField("list");
                field.setAccessible(true);
                field.set(condition, list);
            } catch (Exception e) {
                fail("Failed to set list field: " + e.getMessage());
            }

            assertTrue(condition.match(mockData));
        }
    }
}
