package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.api.MaterialList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CEEnchant - the main enchant class holding enchant metadata.
 */
@DisplayName("CEEnchant Tests")
class CEEnchantTest {

    private CEEnchant ceEnchant;
    private CEDisplay mockDisplay;
    private CELevelMap mockLevelMap;
    private MaterialList mockMaterialList;
    private List<String> blacklist;

    @BeforeEach
    void setUp() {
        mockDisplay = mock(CEDisplay.class);
        mockLevelMap = mock(CELevelMap.class);
        mockMaterialList = mock(MaterialList.class);
        blacklist = Arrays.asList("enchant_a", "enchant_b");

        ceEnchant = new CEEnchant(
                "TestEnchant",
                "Legendary",
                5,
                100,
                10,
                mockDisplay,
                mockLevelMap,
                mockMaterialList,
                "warrior_set",
                "combat_book",
                blacklist
        );
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with all parameters")
        void shouldCreateWithAllParams() {
            assertNotNull(ceEnchant);
        }

        @Test
        @DisplayName("should store all fields correctly")
        void shouldStoreAllFields() {
            assertEquals("TestEnchant", ceEnchant.getName());
            assertEquals("Legendary", ceEnchant.getGroupName());
            assertEquals(5, ceEnchant.getMaxLevel());
            assertEquals(100, ceEnchant.getValuable());
            assertEquals(10, ceEnchant.getEnchantPoint());
        }
    }

    @Nested
    @DisplayName("getName Tests")
    class GetNameTests {

        @Test
        @DisplayName("should return enchant name")
        void shouldReturnName() {
            assertEquals("TestEnchant", ceEnchant.getName());
        }
    }

    @Nested
    @DisplayName("getGroupName Tests")
    class GetGroupNameTests {

        @Test
        @DisplayName("should return group name")
        void shouldReturnGroupName() {
            assertEquals("Legendary", ceEnchant.getGroupName());
        }
    }

    @Nested
    @DisplayName("getMaxLevel Tests")
    class GetMaxLevelTests {

        @Test
        @DisplayName("should return max level")
        void shouldReturnMaxLevel() {
            assertEquals(5, ceEnchant.getMaxLevel());
        }

        @Test
        @DisplayName("should handle max level of 1")
        void shouldHandleMaxLevelOne() {
            CEEnchant singleLevel = new CEEnchant("Single", "Common", 1, 10, 5,
                    mockDisplay, mockLevelMap, mockMaterialList, null, null, Collections.emptyList());

            assertEquals(1, singleLevel.getMaxLevel());
        }
    }

    @Nested
    @DisplayName("getValuable Tests")
    class GetValuableTests {

        @Test
        @DisplayName("should return valuable")
        void shouldReturnValuable() {
            assertEquals(100, ceEnchant.getValuable());
        }
    }

    @Nested
    @DisplayName("getEnchantPoint Tests")
    class GetEnchantPointTests {

        @Test
        @DisplayName("should return enchant point")
        void shouldReturnEnchantPoint() {
            assertEquals(10, ceEnchant.getEnchantPoint());
        }
    }

    @Nested
    @DisplayName("getCEDisplay Tests")
    class GetCEDisplayTests {

        @Test
        @DisplayName("should return display object")
        void shouldReturnDisplay() {
            assertSame(mockDisplay, ceEnchant.getCEDisplay());
        }
    }

    @Nested
    @DisplayName("getCELevel Tests")
    class GetCELevelTests {

        @Test
        @DisplayName("should delegate to level map get(int)")
        void shouldDelegateToLevelMap() {
            CELevel mockLevel = mock(CELevel.class);
            when(mockLevelMap.get(1)).thenReturn(mockLevel);

            CELevel result = ceEnchant.getCELevel(1);

            assertSame(mockLevel, result);
            verify(mockLevelMap).get(1);
        }

        @Test
        @DisplayName("should return null when level not found")
        void shouldReturnNullWhenLevelNotFound() {
            when(mockLevelMap.get(99)).thenReturn(null);

            assertNull(ceEnchant.getCELevel(99));
        }
    }

    @Nested
    @DisplayName("getCELevelMap Tests")
    class GetCELevelMapTests {

        @Test
        @DisplayName("should return level map")
        void shouldReturnLevelMap() {
            assertSame(mockLevelMap, ceEnchant.getCELevelMap());
        }
    }

    @Nested
    @DisplayName("getAppliesMaterialList Tests")
    class GetAppliesMaterialListTests {

        @Test
        @DisplayName("should return material list")
        void shouldReturnMaterialList() {
            assertSame(mockMaterialList, ceEnchant.getAppliesMaterialList());
        }
    }

    @Nested
    @DisplayName("getSet Tests")
    class GetSetTests {

        @Test
        @DisplayName("should return set name")
        void shouldReturnSet() {
            assertEquals("warrior_set", ceEnchant.getSet());
        }

        @Test
        @DisplayName("should return null when no set")
        void shouldReturnNullWhenNoSet() {
            CEEnchant noSet = new CEEnchant("Test", "Group", 1, 10, 5,
                    mockDisplay, mockLevelMap, mockMaterialList, null, null, Collections.emptyList());

            assertNull(noSet.getSet());
        }
    }

    @Nested
    @DisplayName("getBookType Tests")
    class GetBookTypeTests {

        @Test
        @DisplayName("should return book type")
        void shouldReturnBookType() {
            assertEquals("combat_book", ceEnchant.getBookType());
        }
    }

    @Nested
    @DisplayName("getEnchantBlacklist Tests")
    class GetEnchantBlacklistTests {

        @Test
        @DisplayName("should return blacklist")
        void shouldReturnBlacklist() {
            assertEquals(blacklist, ceEnchant.getEnchantBlacklist());
            assertEquals(2, ceEnchant.getEnchantBlacklist().size());
        }

        @Test
        @DisplayName("should handle empty blacklist")
        void shouldHandleEmptyBlacklist() {
            CEEnchant noBlacklist = new CEEnchant("Test", "Group", 1, 10, 5,
                    mockDisplay, mockLevelMap, mockMaterialList, null, null, Collections.emptyList());

            assertTrue(noBlacklist.getEnchantBlacklist().isEmpty());
        }
    }
}
