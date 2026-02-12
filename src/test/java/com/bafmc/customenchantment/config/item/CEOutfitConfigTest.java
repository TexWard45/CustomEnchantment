package com.bafmc.customenchantment.config.item;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.customenchantment.config.AbstractConfig;
import com.bafmc.customenchantment.item.outfit.CEOutfitData;
import com.bafmc.customenchantment.item.outfit.CEOutfitStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CEOutfitConfig class - configuration for outfit items.
 */
@DisplayName("CEOutfitConfig Tests")
class CEOutfitConfigTest {

    @TempDir
    Path tempDir;

    @Mock
    private CEOutfitStorage mockStorage;

    private CEOutfitConfig config;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        config = new CEOutfitConfig(mockStorage);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with storage parameter")
        void shouldCreateInstanceWithStorageParameter() {
            CEOutfitConfig newConfig = new CEOutfitConfig(mockStorage);

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }

        @Test
        @DisplayName("Constructor should accept CEOutfitStorage")
        void constructorShouldAcceptCEOutfitStorage() {
            try {
                CEOutfitConfig.class.getConstructor(CEOutfitStorage.class);
            } catch (NoSuchMethodException e) {
                fail("Constructor should accept CEOutfitStorage parameter");
            }
        }
    }

    @Nested
    @DisplayName("loadCEOutfitSpecialDisplayData Tests")
    class LoadCEOutfitSpecialDisplayDataTests {

        @Test
        @DisplayName("Should return empty list for empty config")
        void shouldReturnEmptyListForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            Files.writeString(yamlFile.toPath(), "empty: {}");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("empty");

            List<CEOutfitData.SpecialDisplayData> result = config.loadCEOutfitSpecialDisplayData(section);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should parse special display data with type and display")
        void shouldParseSpecialDisplayDataWithTypeAndDisplay() throws IOException {
            File yamlFile = tempDir.resolve("special.yml").toFile();
            String content = """
                    special:
                      sword:
                        type:
                          - DIAMOND_SWORD
                          - IRON_SWORD
                        display: '&aSword Display'
                      axe:
                        type:
                          - DIAMOND_AXE
                        display: '&bAxe Display'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("special");

            List<CEOutfitData.SpecialDisplayData> result = config.loadCEOutfitSpecialDisplayData(section);

            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("loadOutfitConfigByLevelMap Tests")
    class LoadOutfitConfigByLevelMapTests {

        @Test
        @DisplayName("Should return empty map for empty config")
        void shouldReturnEmptyMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            Files.writeString(yamlFile.toPath(), "empty: {}");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("empty");

            Map<Integer, CEOutfitData.ConfigByLevelData> result = config.loadOutfitConfigByLevelMap(section);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should parse levels with skins")
        void shouldParseLevelsWithSkins() throws IOException {
            File yamlFile = tempDir.resolve("levels.yml").toFile();
            String content = """
                    levels:
                      1:
                        skins:
                          helmet: 'skin_helmet_1'
                          chestplate: 'skin_chest_1'
                      2:
                        skins:
                          helmet: 'skin_helmet_2'
                          chestplate: 'skin_chest_2'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("levels");

            Map<Integer, CEOutfitData.ConfigByLevelData> result = config.loadOutfitConfigByLevelMap(section);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.containsKey(1));
            assertTrue(result.containsKey(2));
        }

        @Test
        @DisplayName("Should parse custom types")
        void shouldParseCustomTypes() throws IOException {
            File yamlFile = tempDir.resolve("custom.yml").toFile();
            String content = """
                    levels:
                      1:
                        custom-types:
                          sword:
                            list:
                              - 'enchant1'
                              - 'enchant2'
                          axe:
                            list:
                              - 'enchant3'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("levels");

            Map<Integer, CEOutfitData.ConfigByLevelData> result = config.loadOutfitConfigByLevelMap(section);

            assertNotNull(result);
            assertTrue(result.containsKey(1));
        }

        @Test
        @DisplayName("Should handle invalid level keys gracefully")
        void shouldHandleInvalidLevelKeysGracefully() throws IOException {
            File yamlFile = tempDir.resolve("invalid.yml").toFile();
            String content = """
                    levels:
                      not-a-number:
                        skins:
                          helmet: 'skin'
                      1:
                        skins:
                          helmet: 'skin'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("levels");

            // Should not throw exception
            assertDoesNotThrow(() -> config.loadOutfitConfigByLevelMap(section));
        }
    }

    @Nested
    @DisplayName("Method Existence Tests")
    class MethodExistenceTests {

        @Test
        @DisplayName("Should have loadCEOutfitSpecialDisplayData method")
        void shouldHaveLoadCEOutfitSpecialDisplayDataMethod() throws NoSuchMethodException {
            Method method = CEOutfitConfig.class.getMethod("loadCEOutfitSpecialDisplayData",
                    AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("loadCEOutfitSpecialDisplayData should return List")
        void loadCEOutfitSpecialDisplayDataShouldReturnList() throws NoSuchMethodException {
            Method method = CEOutfitConfig.class.getMethod("loadCEOutfitSpecialDisplayData",
                    AdvancedConfigurationSection.class);
            assertEquals(List.class, method.getReturnType());
        }

        @Test
        @DisplayName("Should have loadOutfitConfigByLevelMap method")
        void shouldHaveLoadOutfitConfigByLevelMapMethod() throws NoSuchMethodException {
            Method method = CEOutfitConfig.class.getMethod("loadOutfitConfigByLevelMap",
                    AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("loadOutfitConfigByLevelMap should return Map")
        void loadOutfitConfigByLevelMapShouldReturnMap() throws NoSuchMethodException {
            Method method = CEOutfitConfig.class.getMethod("loadOutfitConfigByLevelMap",
                    AdvancedConfigurationSection.class);
            assertEquals(Map.class, method.getReturnType());
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("Should be in correct package")
        void shouldBeInCorrectPackage() {
            assertEquals("com.bafmc.customenchantment.config.item",
                    CEOutfitConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should have protected loadConfig method")
        void shouldHaveProtectedLoadConfigMethod() throws NoSuchMethodException {
            Method method = CEOutfitConfig.class.getDeclaredMethod("loadConfig");
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isProtected(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty skins map")
        void shouldHandleEmptySkinsMap() throws IOException {
            File yamlFile = tempDir.resolve("noskins.yml").toFile();
            String content = """
                    levels:
                      1:
                        skins: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("levels");

            Map<Integer, CEOutfitData.ConfigByLevelData> result = config.loadOutfitConfigByLevelMap(section);

            assertNotNull(result);
            assertTrue(result.containsKey(1));
        }

        @Test
        @DisplayName("Should handle empty custom-types map")
        void shouldHandleEmptyCustomTypesMap() throws IOException {
            File yamlFile = tempDir.resolve("notypes.yml").toFile();
            String content = """
                    levels:
                      1:
                        custom-types: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("levels");

            Map<Integer, CEOutfitData.ConfigByLevelData> result = config.loadOutfitConfigByLevelMap(section);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle special display with empty type list")
        void shouldHandleSpecialDisplayWithEmptyTypeList() throws IOException {
            File yamlFile = tempDir.resolve("emptytype.yml").toFile();
            String content = """
                    special:
                      item:
                        type: []
                        display: 'Display'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("special");

            List<CEOutfitData.SpecialDisplayData> result = config.loadCEOutfitSpecialDisplayData(section);

            assertNotNull(result);
        }
    }
}
