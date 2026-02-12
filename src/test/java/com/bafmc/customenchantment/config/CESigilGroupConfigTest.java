package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.customenchantment.item.sigil.CESigilGroup;
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
 * Tests for CESigilGroupConfig class - configuration for sigil groups.
 */
@DisplayName("CESigilGroupConfig Tests")
class CESigilGroupConfigTest {

    @TempDir
    Path tempDir;

    private CESigilGroupConfig config;

    @BeforeEach
    void setUp() {
        config = new CESigilGroupConfig();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            CESigilGroupConfig newConfig = new CESigilGroupConfig();

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }
    }

    @Nested
    @DisplayName("loadCESigilGroup Tests")
    class LoadCESigilGroupTests {

        @Test
        @DisplayName("Should create CESigilGroup from config")
        void shouldCreateCESigilGroupFromConfig() throws IOException {
            File yamlFile = tempDir.resolve("sigil.yml").toFile();
            String content = """
                    list:
                      test-sigil:
                        display: '&aTest Sigil'
                        item-display: '&b{item_display}'
                        item-lore:
                          - '&7Sigil lore line 1'
                          - '&7Sigil lore line 2'
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
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.test-sigil");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CESigilGroup result = config.loadCESigilGroup("test-sigil", mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("test-sigil", result.getName());
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

            CESigilGroup result = config.loadCESigilGroup("no-display", mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("Settings Display", result.getDisplay());
        }

        @Test
        @DisplayName("Should set all builder properties correctly")
        void shouldSetAllBuilderPropertiesCorrectly() throws IOException {
            File yamlFile = tempDir.resolve("full.yml").toFile();
            String content = """
                    list:
                      full-sigil:
                        display: 'Full Sigil Display'
                        item-display: 'Full Item Display'
                        item-lore:
                          - 'Lore 1'
                          - 'Lore 2'
                        levels:
                          1:
                            color: '&a'
                    settings:
                      display: 'Default'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.full-sigil");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CESigilGroup result = config.loadCESigilGroup("full-sigil", mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("full-sigil", result.getName());
            assertEquals("Full Sigil Display", result.getDisplay());
            assertEquals("Full Item Display", result.getItemDisplay());
            assertNotNull(result.getItemLore());
            assertEquals(2, result.getItemLore().size());
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
        @DisplayName("Should parse levels with various colors")
        void shouldParseLevelsWithVariousColors() throws IOException {
            File yamlFile = tempDir.resolve("colors.yml").toFile();
            String content = """
                    main:
                      levels:
                        1:
                          color: '&a'
                        2:
                          color: '&b'
                        3:
                          color: '&c'
                        4:
                          color: '&d'
                        5:
                          color: '&e'
                    settings: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("main");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            SparseMap<String> result = config.loadLevels(mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals(5, result.size());
        }

        @Test
        @DisplayName("Should handle non-sequential level numbers")
        void shouldHandleNonSequentialLevelNumbers() throws IOException {
            File yamlFile = tempDir.resolve("nonseq.yml").toFile();
            String content = """
                    main:
                      levels:
                        1:
                          color: '&a'
                        5:
                          color: '&b'
                        10:
                          color: '&c'
                        50:
                          color: '&d'
                    settings: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("main");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            SparseMap<String> result = config.loadLevels(mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("&a", result.get(1));
            assertEquals("&b", result.get(5));
            assertEquals("&c", result.get(10));
            assertEquals("&d", result.get(50));
        }
    }

    @Nested
    @DisplayName("Method Existence Tests")
    class MethodExistenceTests {

        @Test
        @DisplayName("Should have loadCESigilGroup method")
        void shouldHaveLoadCESigilGroupMethod() throws NoSuchMethodException {
            Method method = CESigilGroupConfig.class.getMethod("loadCESigilGroup",
                    String.class, AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("loadCESigilGroup should return CESigilGroup")
        void loadCESigilGroupShouldReturnCESigilGroup() throws NoSuchMethodException {
            Method method = CESigilGroupConfig.class.getMethod("loadCESigilGroup",
                    String.class, AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
            assertEquals(CESigilGroup.class, method.getReturnType());
        }

        @Test
        @DisplayName("Should have loadLevels method")
        void shouldHaveLoadLevelsMethod() throws NoSuchMethodException {
            Method method = CESigilGroupConfig.class.getMethod("loadLevels",
                    AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
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
                    CESigilGroupConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should have protected loadConfig method")
        void shouldHaveProtectedLoadConfigMethod() throws NoSuchMethodException {
            Method method = CESigilGroupConfig.class.getDeclaredMethod("loadConfig");
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isProtected(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle special characters in display")
        void shouldHandleSpecialCharactersInDisplay() throws IOException {
            File yamlFile = tempDir.resolve("special.yml").toFile();
            String content = """
                    list:
                      special-display:
                        display: '&a[Special] &lBold & Italic'
                    settings:
                      display: 'Default'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.special-display");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CESigilGroup result = config.loadCESigilGroup("special-display", mainConfig, settingsConfig);

            assertNotNull(result);
            assertNotNull(result.getDisplay());
        }

        @Test
        @DisplayName("Should handle unicode in lore")
        void shouldHandleUnicodeInLore() throws IOException {
            File yamlFile = tempDir.resolve("unicode.yml").toFile();
            String content = """
                    list:
                      unicode-lore:
                        display: 'Unicode Test'
                        item-lore:
                          - 'Lore with symbols'
                    settings:
                      display: 'Default'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.unicode-lore");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CESigilGroup result = config.loadCESigilGroup("unicode-lore", mainConfig, settingsConfig);

            assertNotNull(result);
        }
    }
}
