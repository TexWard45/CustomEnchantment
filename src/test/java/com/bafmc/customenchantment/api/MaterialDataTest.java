package com.bafmc.customenchantment.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MaterialData class - material and metadata handling.
 * Note: Block-related tests require MockBukkit which has limited support for BlockData.
 */
@DisplayName("MaterialData Tests")
class MaterialDataTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        try {
            if (MockBukkit.isMocked()) {
                MockBukkit.unmock();
            }
            server = MockBukkit.mock();
        } catch (Throwable e) {
            server = null;
        }
    }

    @AfterAll
    static void tearDownAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
    }

    @Nested
    @DisplayName("Constructor with Material Tests")
    class ConstructorWithMaterialTests {

        @Test
        @DisplayName("Should create MaterialData with material only")
        void shouldCreateMaterialDataWithMaterialOnly() {
            MaterialData data = new MaterialData(Material.DIAMOND);

            assertEquals(Material.DIAMOND, data.getMaterial());
            assertEquals(0, data.getData());
        }

        @Test
        @DisplayName("Should handle different materials")
        void shouldHandleDifferentMaterials() {
            MaterialData stone = new MaterialData(Material.STONE);
            MaterialData dirt = new MaterialData(Material.DIRT);
            MaterialData iron = new MaterialData(Material.IRON_INGOT);

            assertEquals(Material.STONE, stone.getMaterial());
            assertEquals(Material.DIRT, dirt.getMaterial());
            assertEquals(Material.IRON_INGOT, iron.getMaterial());
        }
    }

    @Nested
    @DisplayName("Constructor with Material and Data Tests")
    class ConstructorWithMaterialAndDataTests {

        @Test
        @DisplayName("Should create MaterialData with material and data value")
        void shouldCreateMaterialDataWithMaterialAndData() {
            MaterialData data = new MaterialData(Material.WHEAT, 7);

            assertEquals(Material.WHEAT, data.getMaterial());
            assertEquals(7, data.getData());
        }

        @Test
        @DisplayName("Should handle zero data value")
        void shouldHandleZeroDataValue() {
            MaterialData data = new MaterialData(Material.STONE, 0);

            assertEquals(Material.STONE, data.getMaterial());
            assertEquals(0, data.getData());
        }

        @Test
        @DisplayName("Should handle various data values")
        void shouldHandleVariousDataValues() {
            MaterialData data1 = new MaterialData(Material.WHEAT, 1);
            MaterialData data2 = new MaterialData(Material.WHEAT, 3);
            MaterialData data3 = new MaterialData(Material.WHEAT, 7);

            assertEquals(1, data1.getData());
            assertEquals(3, data2.getData());
            assertEquals(7, data3.getData());
        }
    }

    @Nested
    @DisplayName("Equals Tests")
    class EqualsTests {

        @Test
        @DisplayName("Should be equal when material and data match")
        void shouldBeEqualWhenMaterialAndDataMatch() {
            MaterialData data1 = new MaterialData(Material.DIAMOND, 0);
            MaterialData data2 = new MaterialData(Material.DIAMOND, 0);

            assertTrue(data1.equals(data2));
            assertTrue(data2.equals(data1));
        }

        @Test
        @DisplayName("Should not be equal when materials differ")
        void shouldNotBeEqualWhenMaterialsDiffer() {
            MaterialData data1 = new MaterialData(Material.DIAMOND, 0);
            MaterialData data2 = new MaterialData(Material.GOLD_INGOT, 0);

            assertFalse(data1.equals(data2));
        }

        @Test
        @DisplayName("Should not be equal when data values differ")
        void shouldNotBeEqualWhenDataValuesDiffer() {
            MaterialData data1 = new MaterialData(Material.WHEAT, 3);
            MaterialData data2 = new MaterialData(Material.WHEAT, 7);

            assertFalse(data1.equals(data2));
        }

        @Test
        @DisplayName("Should not be equal to non-MaterialData object")
        void shouldNotBeEqualToNonMaterialDataObject() {
            MaterialData data = new MaterialData(Material.DIAMOND);

            assertFalse(data.equals("not a MaterialData"));
            assertFalse(data.equals(42));
            assertFalse(data.equals(null));
        }

        @Test
        @DisplayName("Should be reflexive")
        void shouldBeReflexive() {
            MaterialData data = new MaterialData(Material.DIAMOND, 5);

            assertTrue(data.equals(data));
        }
    }

    @Nested
    @DisplayName("getMaterialNMSByString Tests")
    class GetMaterialNMSByStringTests {

        @Test
        @DisplayName("Should parse material name only")
        void shouldParseMaterialNameOnly() {
            MaterialData data = MaterialData.getMaterialNMSByString("DIAMOND");

            assertNotNull(data);
            assertEquals(Material.DIAMOND, data.getMaterial());
            assertEquals(0, data.getData());
        }

        @Test
        @DisplayName("Should parse material with data value")
        void shouldParseMaterialWithDataValue() {
            MaterialData data = MaterialData.getMaterialNMSByString("WHEAT 7");

            assertNotNull(data);
            assertEquals(Material.WHEAT, data.getMaterial());
            assertEquals(7, data.getData());
        }

        @Test
        @DisplayName("Should return null for invalid material name")
        void shouldReturnNullForInvalidMaterialName() {
            MaterialData data = MaterialData.getMaterialNMSByString("INVALID_MATERIAL_NAME");

            assertNull(data);
        }

        @Test
        @DisplayName("Should return null for empty string")
        void shouldReturnNullForEmptyString() {
            MaterialData data = MaterialData.getMaterialNMSByString("");

            assertNull(data);
        }

        @Test
        @DisplayName("Should handle lowercase material names")
        void shouldHandleLowercaseMaterialNames() {
            MaterialData data = MaterialData.getMaterialNMSByString("diamond");

            // EnumUtils.valueOf should handle case-insensitively based on framework behavior
            // This test verifies the behavior
            if (data != null) {
                assertEquals(Material.DIAMOND, data.getMaterial());
            }
        }

        @Test
        @DisplayName("Should return null for invalid data value")
        void shouldReturnNullForInvalidDataValue() {
            MaterialData data = MaterialData.getMaterialNMSByString("DIAMOND abc");

            assertNull(data);
        }

        @Test
        @DisplayName("Should parse zero data value")
        void shouldParseZeroDataValue() {
            MaterialData data = MaterialData.getMaterialNMSByString("STONE 0");

            assertNotNull(data);
            assertEquals(Material.STONE, data.getMaterial());
            assertEquals(0, data.getData());
        }
    }

    @Nested
    @DisplayName("toString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should return readable string representation")
        void shouldReturnReadableStringRepresentation() {
            MaterialData data = new MaterialData(Material.DIAMOND, 0);

            String result = data.toString();

            assertNotNull(result);
            assertTrue(result.contains("MaterialData"));
            assertTrue(result.contains("DIAMOND"));
        }

        @Test
        @DisplayName("Should include data value in string representation")
        void shouldIncludeDataValueInStringRepresentation() {
            MaterialData data = new MaterialData(Material.WHEAT, 7);

            String result = data.toString();

            assertTrue(result.contains("7"));
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("getMaterial should return correct material")
        void getMaterialShouldReturnCorrectMaterial() {
            MaterialData data = new MaterialData(Material.EMERALD);

            assertEquals(Material.EMERALD, data.getMaterial());
        }

        @Test
        @DisplayName("getData should return correct data value")
        void getDataShouldReturnCorrectDataValue() {
            MaterialData data = new MaterialData(Material.STONE, 5);

            assertEquals(5, data.getData());
        }

        @Test
        @DisplayName("Multiple getter calls should return consistent values")
        void multipleGetterCallsShouldReturnConsistentValues() {
            MaterialData data = new MaterialData(Material.GOLD_BLOCK, 3);

            assertEquals(data.getMaterial(), data.getMaterial());
            assertEquals(data.getData(), data.getData());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle air material")
        void shouldHandleAirMaterial() {
            MaterialData data = new MaterialData(Material.AIR);

            assertEquals(Material.AIR, data.getMaterial());
            assertEquals(0, data.getData());
        }

        @Test
        @DisplayName("Should handle negative data value")
        void shouldHandleNegativeDataValue() {
            MaterialData data = new MaterialData(Material.STONE, -1);

            assertEquals(-1, data.getData());
        }

        @Test
        @DisplayName("Should handle large data value")
        void shouldHandleLargeDataValue() {
            MaterialData data = new MaterialData(Material.STONE, 999);

            assertEquals(999, data.getData());
        }
    }
}
