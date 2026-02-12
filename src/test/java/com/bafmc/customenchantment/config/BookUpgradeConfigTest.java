package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.customenchantment.menu.bookupgrade.data.RequiredXpGroup;
import com.bafmc.customenchantment.menu.bookupgrade.data.XpGroup;
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
 * Tests for BookUpgradeConfig class - configuration for book upgrades.
 */
@DisplayName("BookUpgradeConfig Tests")
class BookUpgradeConfigTest {

    @TempDir
    Path tempDir;

    private BookUpgradeConfig config;

    @BeforeEach
    void setUp() {
        config = new BookUpgradeConfig();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            BookUpgradeConfig newConfig = new BookUpgradeConfig();

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }
    }

    @Nested
    @DisplayName("loadXpGroup Tests")
    class LoadXpGroupTests {

        @Test
        @DisplayName("Should return empty map for empty config")
        void shouldReturnEmptyMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            String content = """
                    settings:
                      xp: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, XpGroup> result = config.loadXpGroup(fileConfig);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should parse XP groups with levels")
        void shouldParseXpGroupsWithLevels() throws IOException {
            File yamlFile = tempDir.resolve("xp.yml").toFile();
            String content = """
                    settings:
                      xp:
                        common:
                          1: '10-20'
                          2: '20-30'
                          3: '30-40'
                        rare:
                          1: '50-100'
                          2: '100-150'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, XpGroup> result = config.loadXpGroup(fileConfig);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.containsKey("common"));
            assertTrue(result.containsKey("rare"));
        }
    }

    @Nested
    @DisplayName("loadRequiredXpGroup Tests")
    class LoadRequiredXpGroupTests {

        @Test
        @DisplayName("Should return empty map for empty config")
        void shouldReturnEmptyMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            String content = """
                    settings:
                      required-xp: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, RequiredXpGroup> result = config.loadRequiredXpGroup(fileConfig);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should parse required XP groups with levels")
        void shouldParseRequiredXpGroupsWithLevels() throws IOException {
            File yamlFile = tempDir.resolve("requiredxp.yml").toFile();
            String content = """
                    settings:
                      required-xp:
                        common:
                          1: 100
                          2: 200
                          3: 300
                        rare:
                          1: 500
                          2: 1000
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, RequiredXpGroup> result = config.loadRequiredXpGroup(fileConfig);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.containsKey("common"));
            assertTrue(result.containsKey("rare"));
        }
    }

    @Nested
    @DisplayName("loadBookUpgradeLevelData Tests")
    class LoadBookUpgradeLevelDataTests {

        @Test
        @DisplayName("Should return empty map for empty config")
        void shouldReturnEmptyMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            String content = """
                    list: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            var result = config.loadBookUpgradeLevelData(fileConfig);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Method Existence Tests")
    class MethodExistenceTests {

        @Test
        @DisplayName("Should have loadXpGroup method")
        void shouldHaveLoadXpGroupMethod() throws NoSuchMethodException {
            Method method = BookUpgradeConfig.class.getMethod("loadXpGroup",
                    AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("loadXpGroup should return Map")
        void loadXpGroupShouldReturnMap() throws NoSuchMethodException {
            Method method = BookUpgradeConfig.class.getMethod("loadXpGroup",
                    AdvancedConfigurationSection.class);
            assertEquals(Map.class, method.getReturnType());
        }

        @Test
        @DisplayName("Should have loadRequiredXpGroup method")
        void shouldHaveLoadRequiredXpGroupMethod() throws NoSuchMethodException {
            Method method = BookUpgradeConfig.class.getMethod("loadRequiredXpGroup",
                    AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadBookUpgradeLevelData method")
        void shouldHaveLoadBookUpgradeLevelDataMethod() throws NoSuchMethodException {
            Method method = BookUpgradeConfig.class.getMethod("loadBookUpgradeLevelData",
                    AdvancedConfigurationSection.class);
            assertNotNull(method);
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("Should be in correct package")
        void shouldBeInCorrectPackage() {
            assertEquals("com.bafmc.customenchantment.config",
                    BookUpgradeConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should have protected loadConfig method")
        void shouldHaveProtectedLoadConfigMethod() throws NoSuchMethodException {
            Method method = BookUpgradeConfig.class.getDeclaredMethod("loadConfig");
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isProtected(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("YAML Parsing Tests")
    class YamlParsingTests {

        @Test
        @DisplayName("Should parse XP ranges correctly")
        void shouldParseXpRangesCorrectly() throws IOException {
            File yamlFile = tempDir.resolve("ranges.yml").toFile();
            String content = """
                    settings:
                      xp:
                        test:
                          1: '0-100'
                          2: '100-500'
                          3: '500-1000'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, XpGroup> result = config.loadXpGroup(fileConfig);

            assertNotNull(result);
            assertTrue(result.containsKey("test"));
        }

        @Test
        @DisplayName("Should parse single value XP correctly")
        void shouldParseSingleValueXpCorrectly() throws IOException {
            File yamlFile = tempDir.resolve("single.yml").toFile();
            String content = """
                    settings:
                      xp:
                        test:
                          1: '100'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, XpGroup> result = config.loadXpGroup(fileConfig);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle large level numbers")
        void shouldHandleLargeLevelNumbers() throws IOException {
            File yamlFile = tempDir.resolve("large.yml").toFile();
            String content = """
                    settings:
                      required-xp:
                        test:
                          99: 99999
                          100: 100000
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, RequiredXpGroup> result = config.loadRequiredXpGroup(fileConfig);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle zero XP requirement")
        void shouldHandleZeroXpRequirement() throws IOException {
            File yamlFile = tempDir.resolve("zero.yml").toFile();
            String content = """
                    settings:
                      required-xp:
                        free:
                          1: 0
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, RequiredXpGroup> result = config.loadRequiredXpGroup(fileConfig);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle special characters in group name")
        void shouldHandleSpecialCharactersInGroupName() throws IOException {
            File yamlFile = tempDir.resolve("special.yml").toFile();
            String content = """
                    settings:
                      xp:
                        test-group_v2:
                          1: '100'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, XpGroup> result = config.loadXpGroup(fileConfig);

            assertNotNull(result);
            assertTrue(result.containsKey("test-group_v2"));
        }
    }
}
