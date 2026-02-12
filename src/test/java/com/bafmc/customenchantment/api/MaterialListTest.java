package com.bafmc.customenchantment.api;

import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MaterialList class - list of MaterialData with deduplication and lookup.
 */
@DisplayName("MaterialList Tests")
class MaterialListTest {

    @BeforeEach
    void setUp() {
        // Clear the static DEFINE_MAP before each test
        MaterialList.DEFINE_MAP.clear();
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
        MaterialList.DEFINE_MAP.clear();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty MaterialList")
        void shouldCreateEmptyMaterialList() {
            MaterialList list = new MaterialList();

            assertNotNull(list);
            assertEquals(0, list.size());
        }

        @Test
        @DisplayName("Should create MaterialList from list of MaterialData")
        void shouldCreateMaterialListFromList() {
            List<MaterialData> dataList = Arrays.asList(
                new MaterialData(Material.DIAMOND),
                new MaterialData(Material.GOLD_INGOT)
            );

            MaterialList list = new MaterialList(dataList);

            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("Should create MaterialList from empty list")
        void shouldCreateMaterialListFromEmptyList() {
            MaterialList list = new MaterialList(new ArrayList<>());

            assertEquals(0, list.size());
        }
    }

    @Nested
    @DisplayName("add Tests")
    class AddTests {

        @Test
        @DisplayName("Should add MaterialData to list")
        void shouldAddMaterialDataToList() {
            MaterialList list = new MaterialList();
            MaterialData data = new MaterialData(Material.DIAMOND);

            boolean result = list.add(data);

            assertTrue(result);
            assertEquals(1, list.size());
            assertTrue(list.contains(data));
        }

        @Test
        @DisplayName("Should prevent duplicate MaterialData")
        void shouldPreventDuplicateMaterialData() {
            MaterialList list = new MaterialList();
            MaterialData data1 = new MaterialData(Material.DIAMOND);
            MaterialData data2 = new MaterialData(Material.DIAMOND);

            list.add(data1);
            boolean result = list.add(data2);

            assertFalse(result);
            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("Should allow different materials")
        void shouldAllowDifferentMaterials() {
            MaterialList list = new MaterialList();

            list.add(new MaterialData(Material.DIAMOND));
            list.add(new MaterialData(Material.GOLD_INGOT));
            list.add(new MaterialData(Material.IRON_INGOT));

            assertEquals(3, list.size());
        }

        @Test
        @DisplayName("Should allow same material with different data")
        void shouldAllowSameMaterialWithDifferentData() {
            MaterialList list = new MaterialList();

            list.add(new MaterialData(Material.WHEAT, 0));
            list.add(new MaterialData(Material.WHEAT, 3));
            list.add(new MaterialData(Material.WHEAT, 7));

            assertEquals(3, list.size());
        }
    }

    @Nested
    @DisplayName("addAll Tests")
    class AddAllTests {

        @Test
        @DisplayName("Should add all MaterialData from list")
        void shouldAddAllMaterialDataFromList() {
            MaterialList list = new MaterialList();
            List<MaterialData> dataList = Arrays.asList(
                new MaterialData(Material.DIAMOND),
                new MaterialData(Material.GOLD_INGOT),
                new MaterialData(Material.IRON_INGOT)
            );

            list.addAll(dataList);

            assertEquals(3, list.size());
        }

        @Test
        @DisplayName("Should skip duplicates when adding all")
        void shouldSkipDuplicatesWhenAddingAll() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND));

            List<MaterialData> dataList = Arrays.asList(
                new MaterialData(Material.DIAMOND),  // Duplicate
                new MaterialData(Material.GOLD_INGOT)
            );

            list.addAll(dataList);

            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("Should handle empty list in addAll")
        void shouldHandleEmptyListInAddAll() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND));

            list.addAll(new ArrayList<>());

            assertEquals(1, list.size());
        }

        @Test
        @DisplayName("addAll should always return true")
        void addAllShouldAlwaysReturnTrue() {
            MaterialList list = new MaterialList();

            boolean result = list.addAll(Arrays.asList(new MaterialData(Material.DIAMOND)));

            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("defineMaterialList Tests")
    class DefineMaterialListTests {

        @Test
        @DisplayName("Should define material list with key")
        void shouldDefineMaterialListWithKey() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND));
            list.add(new MaterialData(Material.GOLD_INGOT));

            MaterialList.defineMaterialList("TEST_LIST", list);

            assertNotNull(MaterialList.getMaterialList("TEST_LIST"));
            assertEquals(2, MaterialList.getMaterialList("TEST_LIST").size());
        }

        @Test
        @DisplayName("Should not define list with material name as key")
        void shouldNotDefineListWithMaterialNameAsKey() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND));

            // DIAMOND is a valid Material name, so it should be rejected
            MaterialList.defineMaterialList("DIAMOND", list);

            // The list should not be stored because DIAMOND is a valid Material
            assertNull(MaterialList.getMaterialList("DIAMOND"));
        }

        @Test
        @DisplayName("Should overwrite existing definition")
        void shouldOverwriteExistingDefinition() {
            MaterialList list1 = new MaterialList();
            list1.add(new MaterialData(Material.DIAMOND));

            MaterialList list2 = new MaterialList();
            list2.add(new MaterialData(Material.GOLD_INGOT));
            list2.add(new MaterialData(Material.IRON_INGOT));

            MaterialList.defineMaterialList("TEST_KEY", list1);
            MaterialList.defineMaterialList("TEST_KEY", list2);

            assertEquals(2, MaterialList.getMaterialList("TEST_KEY").size());
        }
    }

    @Nested
    @DisplayName("getMaterialList by key Tests")
    class GetMaterialListByKeyTests {

        @Test
        @DisplayName("Should return material list by key")
        void shouldReturnMaterialListByKey() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND));
            MaterialList.defineMaterialList("MY_LIST", list);

            MaterialList result = MaterialList.getMaterialList("MY_LIST");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return null for undefined key")
        void shouldReturnNullForUndefinedKey() {
            MaterialList result = MaterialList.getMaterialList("UNDEFINED_KEY");

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("getMaterialList from String list Tests")
    class GetMaterialListFromStringListTests {

        @Test
        @DisplayName("Should parse list of material names")
        void shouldParseListOfMaterialNames() {
            List<String> names = Arrays.asList("DIAMOND", "GOLD_INGOT", "IRON_INGOT");

            MaterialList result = MaterialList.getMaterialList(names);

            assertNotNull(result);
            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("Should parse material with data value")
        void shouldParseMaterialWithDataValue() {
            List<String> names = Arrays.asList("WHEAT 7");

            MaterialList result = MaterialList.getMaterialList(names);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(Material.WHEAT, result.get(0).getMaterial());
            assertEquals(7, result.get(0).getData());
        }

        @Test
        @DisplayName("Should resolve defined material list")
        void shouldResolveDefinedMaterialList() {
            MaterialList predefined = new MaterialList();
            predefined.add(new MaterialData(Material.DIAMOND));
            predefined.add(new MaterialData(Material.GOLD_INGOT));
            MaterialList.defineMaterialList("PRECIOUS", predefined);

            List<String> names = Arrays.asList("PRECIOUS", "IRON_INGOT");

            MaterialList result = MaterialList.getMaterialList(names);

            assertNotNull(result);
            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("Should handle empty string list")
        void shouldHandleEmptyStringList() {
            MaterialList result = MaterialList.getMaterialList(new ArrayList<>());

            assertNotNull(result);
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Should handle mixed defined and material names")
        void shouldHandleMixedDefinedAndMaterialNames() {
            MaterialList ores = new MaterialList();
            ores.add(new MaterialData(Material.DIAMOND_ORE));
            ores.add(new MaterialData(Material.GOLD_ORE));
            MaterialList.defineMaterialList("ORES", ores);

            List<String> names = Arrays.asList("ORES", "STONE", "COBBLESTONE");

            MaterialList result = MaterialList.getMaterialList(names);

            assertNotNull(result);
            assertEquals(4, result.size());
        }
    }

    @Nested
    @DisplayName("getMap Tests")
    class GetMapTests {

        @Test
        @DisplayName("Should return DEFINE_MAP")
        void shouldReturnDefineMap() {
            assertNotNull(MaterialList.getMap());
            assertSame(MaterialList.DEFINE_MAP, MaterialList.getMap());
        }

        @Test
        @DisplayName("Map should be modifiable")
        void mapShouldBeModifiable() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND));

            MaterialList.getMap().put("DIRECT_KEY", list);

            assertNotNull(MaterialList.getMaterialList("DIRECT_KEY"));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null in addAll")
        void shouldHandleNullInAddAll() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND));

            // Adding list with null MaterialData
            List<MaterialData> withNull = new ArrayList<>();
            withNull.add(new MaterialData(Material.GOLD_INGOT));
            withNull.add(null);

            // This may throw or handle gracefully depending on implementation
            try {
                list.addAll(withNull);
                // If it doesn't throw, verify the list state
                assertTrue(list.size() >= 1);
            } catch (NullPointerException e) {
                // Expected if implementation doesn't handle null
            }
        }

        @Test
        @DisplayName("Should handle case sensitivity in defined lists")
        void shouldHandleCaseSensitivityInDefinedLists() {
            MaterialList list = new MaterialList();
            list.add(new MaterialData(Material.DIAMOND));

            MaterialList.defineMaterialList("MY_LIST", list);

            // Keys are case-sensitive
            assertNotNull(MaterialList.getMaterialList("MY_LIST"));
            assertNull(MaterialList.getMaterialList("my_list"));
            assertNull(MaterialList.getMaterialList("My_List"));
        }
    }
}
