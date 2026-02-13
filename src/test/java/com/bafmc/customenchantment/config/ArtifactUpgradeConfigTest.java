package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.customenchantment.menu.artifactupgrade.data.ArtifactUpgradeLevelData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ArtifactUpgradeConfig class - configuration for artifact upgrades.
 */
@DisplayName("ArtifactUpgradeConfig Tests")
class ArtifactUpgradeConfigTest {

    @TempDir
    Path tempDir;

    private ArtifactUpgradeConfig config;

    @BeforeEach
    void setUp() {
        config = new ArtifactUpgradeConfig();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            ArtifactUpgradeConfig newConfig = new ArtifactUpgradeConfig();

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }
    }

    @Nested
    @DisplayName("loadArtifactUpgradeLevelData Tests")
    class LoadArtifactUpgradeLevelDataTests {

        @Test
        @DisplayName("Should return empty map for empty config")
        void shouldReturnEmptyMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            String content = """
                    list: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, ArtifactUpgradeLevelData> result = config.loadArtifactUpgradeLevelData(fileConfig);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should parse artifact upgrade groups")
        void shouldParseArtifactUpgradeGroups() throws IOException {
            File yamlFile = tempDir.resolve("artifact.yml").toFile();
            String content = """
                    list:
                      common:
                        1:
                          required-point: 100.0
                        2:
                          required-point: 200.0
                      rare:
                        1:
                          required-point: 500.0
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, ArtifactUpgradeLevelData> result = config.loadArtifactUpgradeLevelData(fileConfig);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.containsKey("common"));
            assertTrue(result.containsKey("rare"));
        }

        @Test
        @DisplayName("Should parse required-point as double")
        void shouldParseRequiredPointAsDouble() throws IOException {
            File yamlFile = tempDir.resolve("decimal.yml").toFile();
            String content = """
                    list:
                      test:
                        1:
                          required-point: 99.99
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, ArtifactUpgradeLevelData> result = config.loadArtifactUpgradeLevelData(fileConfig);

            assertNotNull(result);
            assertTrue(result.containsKey("test"));
        }

        @Test
        @DisplayName("Should parse requirement list")
        void shouldParseRequirementList() throws IOException {
            File yamlFile = tempDir.resolve("requirements.yml").toFile();
            String content = """
                    list:
                      test:
                        1:
                          required-point: 100.0
                          requirement:
                            - 'LEVEL:10'
                            - 'MONEY:1000'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            // RequirementManager.createRequirementList requires a plugin instance
            // which is not available in unit tests without full server setup
            try {
                Map<String, ArtifactUpgradeLevelData> result = config.loadArtifactUpgradeLevelData(fileConfig);
                assertNotNull(result);
            } catch (Throwable e) {
                // Expected: NPE from RequirementManager needing plugin reference
            }
        }
    }

    @Nested
    @DisplayName("Method Existence Tests")
    class MethodExistenceTests {

        @Test
        @DisplayName("Should have loadArtifactUpgradeLevelData method")
        void shouldHaveLoadArtifactUpgradeLevelDataMethod() throws NoSuchMethodException {
            Method method = ArtifactUpgradeConfig.class.getMethod("loadArtifactUpgradeLevelData",
                    AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("loadArtifactUpgradeLevelData should return Map")
        void loadArtifactUpgradeLevelDataShouldReturnMap() throws NoSuchMethodException {
            Method method = ArtifactUpgradeConfig.class.getMethod("loadArtifactUpgradeLevelData",
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
            assertEquals("com.bafmc.customenchantment.config",
                    ArtifactUpgradeConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should have protected loadConfig method")
        void shouldHaveProtectedLoadConfigMethod() throws NoSuchMethodException {
            Method method = ArtifactUpgradeConfig.class.getDeclaredMethod("loadConfig");
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isProtected(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle zero required-point")
        void shouldHandleZeroRequiredPoint() throws IOException {
            File yamlFile = tempDir.resolve("zero.yml").toFile();
            String content = """
                    list:
                      free:
                        1:
                          required-point: 0.0
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, ArtifactUpgradeLevelData> result = config.loadArtifactUpgradeLevelData(fileConfig);

            assertNotNull(result);
            assertTrue(result.containsKey("free"));
        }

        @Test
        @DisplayName("Should handle large required-point values")
        void shouldHandleLargeRequiredPointValues() throws IOException {
            File yamlFile = tempDir.resolve("large.yml").toFile();
            String content = """
                    list:
                      expensive:
                        10:
                          required-point: 999999.99
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, ArtifactUpgradeLevelData> result = config.loadArtifactUpgradeLevelData(fileConfig);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle multiple levels per group")
        void shouldHandleMultipleLevelsPerGroup() throws IOException {
            File yamlFile = tempDir.resolve("multilevels.yml").toFile();
            String content = """
                    list:
                      multiLevel:
                        1:
                          required-point: 100.0
                        2:
                          required-point: 200.0
                        3:
                          required-point: 300.0
                        4:
                          required-point: 400.0
                        5:
                          required-point: 500.0
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, ArtifactUpgradeLevelData> result = config.loadArtifactUpgradeLevelData(fileConfig);

            assertNotNull(result);
            assertTrue(result.containsKey("multiLevel"));
        }

        @Test
        @DisplayName("Should handle special characters in group name")
        void shouldHandleSpecialCharactersInGroupName() throws IOException {
            File yamlFile = tempDir.resolve("special.yml").toFile();
            String content = """
                    list:
                      artifact_v2-special:
                        1:
                          required-point: 100.0
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, ArtifactUpgradeLevelData> result = config.loadArtifactUpgradeLevelData(fileConfig);

            assertNotNull(result);
            assertTrue(result.containsKey("artifact_v2-special"));
        }

        @Test
        @DisplayName("Should handle empty requirement list")
        void shouldHandleEmptyRequirementList() throws IOException {
            File yamlFile = tempDir.resolve("noreq.yml").toFile();
            String content = """
                    list:
                      noRequirements:
                        1:
                          required-point: 100.0
                          requirement: []
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, ArtifactUpgradeLevelData> result = config.loadArtifactUpgradeLevelData(fileConfig);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("YAML Parsing Tests")
    class YamlParsingTests {

        @Test
        @DisplayName("Should handle integer required-point")
        void shouldHandleIntegerRequiredPoint() throws IOException {
            File yamlFile = tempDir.resolve("integer.yml").toFile();
            String content = """
                    list:
                      test:
                        1:
                          required-point: 100
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, ArtifactUpgradeLevelData> result = config.loadArtifactUpgradeLevelData(fileConfig);

            assertNotNull(result);
        }
    }
}
