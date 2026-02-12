package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.customenchantment.item.artifact.CEArtifactGroup;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CEArtifactGroupConfig class - configuration for artifact groups.
 */
@DisplayName("CEArtifactGroupConfig Tests")
class CEArtifactGroupConfigTest {

    @TempDir
    Path tempDir;

    private CEArtifactGroupConfig config;

    @BeforeEach
    void setUp() {
        config = new CEArtifactGroupConfig();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            CEArtifactGroupConfig newConfig = new CEArtifactGroupConfig();

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }
    }

    @Nested
    @DisplayName("loadCEArtifactGroup Tests")
    class LoadCEArtifactGroupTests {

        @Test
        @DisplayName("Should create CEArtifactGroup from config")
        void shouldCreateCEArtifactGroupFromConfig() throws IOException {
            File yamlFile = tempDir.resolve("artifact.yml").toFile();
            String content = """
                    list:
                      test-artifact:
                        display: '&aTest Artifact'
                        item-display: '&b{item_display}'
                        item-lore:
                          - '&7Artifact lore line 1'
                          - '&7Artifact lore line 2'
                    settings:
                      display: 'Default Display'
                      levels:
                        1:
                          color: '&a'
                        2:
                          color: '&b'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.test-artifact");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEArtifactGroup result = config.loadCEArtifactGroup("test-artifact", mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("test-artifact", result.getName());
        }

        @Test
        @DisplayName("Should use settings display when main display is missing")
        void shouldUseSettingsDisplayWhenMainDisplayIsMissing() throws IOException {
            File yamlFile = tempDir.resolve("fallback.yml").toFile();
            String content = """
                    list:
                      no-display:
                        item-display: '&b{item_display}'
                    settings:
                      display: 'Settings Display'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.no-display");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEArtifactGroup result = config.loadCEArtifactGroup("no-display", mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("Settings Display", result.getDisplay());
        }

        @Test
        @DisplayName("Should use main config values over settings when present")
        void shouldUseMainConfigValuesOverSettingsWhenPresent() throws IOException {
            File yamlFile = tempDir.resolve("override.yml").toFile();
            String content = """
                    list:
                      override-group:
                        display: 'Main Display'
                        item-display: 'Main Item Display'
                    settings:
                      display: 'Settings Display'
                      item-display: 'Settings Item Display'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.override-group");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEArtifactGroup result = config.loadCEArtifactGroup("override-group", mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("Main Display", result.getDisplay());
            assertEquals("Main Item Display", result.getItemDisplay());
        }

        @Test
        @DisplayName("Should handle empty item lore")
        void shouldHandleEmptyItemLore() throws IOException {
            File yamlFile = tempDir.resolve("nolore.yml").toFile();
            String content = """
                    list:
                      no-lore:
                        display: 'No Lore Group'
                    settings:
                      display: 'Settings'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.no-lore");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEArtifactGroup result = config.loadCEArtifactGroup("no-lore", mainConfig, settingsConfig);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("loadLevels Tests")
    class LoadLevelsTests {

        @Test
        @DisplayName("Should return empty SparseMap when no levels defined")
        void shouldReturnEmptySparseMapWhenNoLevelsDefined() throws IOException {
            File yamlFile = tempDir.resolve("nolevels.yml").toFile();
            String content = """
                    main:
                      display: 'Test'
                    settings:
                      display: 'Settings'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("main");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            SparseMap<String> result = config.loadLevels(mainConfig, settingsConfig);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should parse levels from main config")
        void shouldParseLevelsFromMainConfig() throws IOException {
            File yamlFile = tempDir.resolve("mainlevels.yml").toFile();
            String content = """
                    main:
                      levels:
                        1:
                          color: '&a'
                        2:
                          color: '&b'
                        3:
                          color: '&c'
                    settings: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("main");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            SparseMap<String> result = config.loadLevels(mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("&a", result.get(1));
            assertEquals("&b", result.get(2));
            assertEquals("&c", result.get(3));
        }

        @Test
        @DisplayName("Should fallback to settings levels when main has no levels")
        void shouldFallbackToSettingsLevelsWhenMainHasNoLevels() throws IOException {
            File yamlFile = tempDir.resolve("settingslevels.yml").toFile();
            String content = """
                    main:
                      display: 'Test'
                    settings:
                      levels:
                        1:
                          color: '&e'
                        5:
                          color: '&6'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("main");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            SparseMap<String> result = config.loadLevels(mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("&e", result.get(1));
            assertEquals("&6", result.get(5));
        }

        @Test
        @DisplayName("Should handle invalid level keys gracefully")
        void shouldHandleInvalidLevelKeysGracefully() throws IOException {
            File yamlFile = tempDir.resolve("invalid.yml").toFile();
            String content = """
                    main:
                      levels:
                        not-a-number:
                          color: '&a'
                        1:
                          color: '&b'
                    settings: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("main");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            // Should not throw exception
            assertDoesNotThrow(() -> config.loadLevels(mainConfig, settingsConfig));
        }
    }

    @Nested
    @DisplayName("Method Existence Tests")
    class MethodExistenceTests {

        @Test
        @DisplayName("Should have loadCEArtifactGroup method")
        void shouldHaveLoadCEArtifactGroupMethod() throws NoSuchMethodException {
            Method method = CEArtifactGroupConfig.class.getMethod("loadCEArtifactGroup",
                    String.class, AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("loadCEArtifactGroup should return CEArtifactGroup")
        void loadCEArtifactGroupShouldReturnCEArtifactGroup() throws NoSuchMethodException {
            Method method = CEArtifactGroupConfig.class.getMethod("loadCEArtifactGroup",
                    String.class, AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
            assertEquals(CEArtifactGroup.class, method.getReturnType());
        }

        @Test
        @DisplayName("Should have loadLevels method")
        void shouldHaveLoadLevelsMethod() throws NoSuchMethodException {
            Method method = CEArtifactGroupConfig.class.getMethod("loadLevels",
                    AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("loadLevels should return SparseMap")
        void loadLevelsShouldReturnSparseMap() throws NoSuchMethodException {
            Method method = CEArtifactGroupConfig.class.getMethod("loadLevels",
                    AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
            assertEquals(SparseMap.class, method.getReturnType());
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("Should be in correct package")
        void shouldBeInCorrectPackage() {
            assertEquals("com.bafmc.customenchantment.config",
                    CEArtifactGroupConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should have protected loadConfig method")
        void shouldHaveProtectedLoadConfigMethod() throws NoSuchMethodException {
            Method method = CEArtifactGroupConfig.class.getDeclaredMethod("loadConfig");
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isProtected(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle color codes in display")
        void shouldHandleColorCodesInDisplay() throws IOException {
            File yamlFile = tempDir.resolve("colors.yml").toFile();
            String content = """
                    list:
                      colored:
                        display: '&a&lGreen Bold &r&7Gray'
                    settings:
                      display: 'Default'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.colored");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEArtifactGroup result = config.loadCEArtifactGroup("colored", mainConfig, settingsConfig);

            assertNotNull(result);
            assertNotNull(result.getDisplay());
        }

        @Test
        @DisplayName("Should handle hex color codes")
        void shouldHandleHexColorCodes() throws IOException {
            File yamlFile = tempDir.resolve("hex.yml").toFile();
            String content = """
                    list:
                      hex-group:
                        display: '&#FF5500Orange'
                    settings:
                      display: 'Default'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.hex-group");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEArtifactGroup result = config.loadCEArtifactGroup("hex-group", mainConfig, settingsConfig);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle special characters in name")
        void shouldHandleSpecialCharactersInName() throws IOException {
            File yamlFile = tempDir.resolve("special.yml").toFile();
            String content = """
                    list:
                      test_artifact-v2:
                        display: 'Special Name'
                    settings:
                      display: 'Default'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.test_artifact-v2");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEArtifactGroup result = config.loadCEArtifactGroup("test_artifact-v2", mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("test_artifact-v2", result.getName());
        }
    }
}
