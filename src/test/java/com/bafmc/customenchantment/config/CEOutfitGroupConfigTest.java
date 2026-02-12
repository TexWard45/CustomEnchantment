package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.customenchantment.item.outfit.CEOutfitGroup;
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
 * Tests for CEOutfitGroupConfig class - configuration for outfit groups.
 */
@DisplayName("CEOutfitGroupConfig Tests")
class CEOutfitGroupConfigTest {

    @TempDir
    Path tempDir;

    private CEOutfitGroupConfig config;

    @BeforeEach
    void setUp() {
        config = new CEOutfitGroupConfig();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            CEOutfitGroupConfig newConfig = new CEOutfitGroupConfig();

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }
    }

    @Nested
    @DisplayName("loadCEOutfitGroup Tests")
    class LoadCEOutfitGroupTests {

        @Test
        @DisplayName("Should create CEOutfitGroup from config")
        void shouldCreateCEOutfitGroupFromConfig() throws IOException {
            File yamlFile = tempDir.resolve("outfit.yml").toFile();
            String content = """
                    list:
                      test-outfit:
                        display: '&aTest Outfit'
                        item-display: '&b{item_display}'
                        item-lore:
                          - '&7Outfit lore line 1'
                          - '&7Outfit lore line 2'
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
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.test-outfit");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEOutfitGroup result = config.loadCEOutfitGroup("test-outfit", mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("test-outfit", result.getName());
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

            CEOutfitGroup result = config.loadCEOutfitGroup("no-display", mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("Settings Display", result.getDisplay());
        }

        @Test
        @DisplayName("Should handle multiple item lore lines")
        void shouldHandleMultipleItemLoreLines() throws IOException {
            File yamlFile = tempDir.resolve("lore.yml").toFile();
            String content = """
                    list:
                      multi-lore:
                        display: 'Multi Lore'
                        item-lore:
                          - '&7Line 1'
                          - '&7Line 2'
                          - '&7Line 3'
                          - '&7Line 4'
                    settings:
                      display: 'Default'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.multi-lore");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEOutfitGroup result = config.loadCEOutfitGroup("multi-lore", mainConfig, settingsConfig);

            assertNotNull(result);
            assertNotNull(result.getItemLore());
            assertEquals(4, result.getItemLore().size());
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
                        5:
                          color: '&d'
                        10:
                          color: '&6'
                    settings: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("main");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            SparseMap<String> result = config.loadLevels(mainConfig, settingsConfig);

            assertNotNull(result);
            assertEquals("&a", result.get(1));
            assertEquals("&d", result.get(5));
            assertEquals("&6", result.get(10));
        }
    }

    @Nested
    @DisplayName("Method Existence Tests")
    class MethodExistenceTests {

        @Test
        @DisplayName("Should have loadCEOutfitGroup method")
        void shouldHaveLoadCEOutfitGroupMethod() throws NoSuchMethodException {
            Method method = CEOutfitGroupConfig.class.getMethod("loadCEOutfitGroup",
                    String.class, AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("loadCEOutfitGroup should return CEOutfitGroup")
        void loadCEOutfitGroupShouldReturnCEOutfitGroup() throws NoSuchMethodException {
            Method method = CEOutfitGroupConfig.class.getMethod("loadCEOutfitGroup",
                    String.class, AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
            assertEquals(CEOutfitGroup.class, method.getReturnType());
        }

        @Test
        @DisplayName("Should have loadLevels method")
        void shouldHaveLoadLevelsMethod() throws NoSuchMethodException {
            Method method = CEOutfitGroupConfig.class.getMethod("loadLevels",
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
                    CEOutfitGroupConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should have protected loadConfig method")
        void shouldHaveProtectedLoadConfigMethod() throws NoSuchMethodException {
            Method method = CEOutfitGroupConfig.class.getDeclaredMethod("loadConfig");
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isProtected(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty item lore list")
        void shouldHandleEmptyItemLoreList() throws IOException {
            File yamlFile = tempDir.resolve("emptylore.yml").toFile();
            String content = """
                    list:
                      empty-lore:
                        display: 'Empty Lore'
                        item-lore: []
                    settings:
                      display: 'Default'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.empty-lore");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEOutfitGroup result = config.loadCEOutfitGroup("empty-lore", mainConfig, settingsConfig);

            assertNotNull(result);
            assertTrue(result.getItemLore().isEmpty());
        }

        @Test
        @DisplayName("Should handle null item display")
        void shouldHandleNullItemDisplay() throws IOException {
            File yamlFile = tempDir.resolve("noitemdisplay.yml").toFile();
            String content = """
                    list:
                      no-item-display:
                        display: 'No Item Display'
                    settings:
                      display: 'Default'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainConfig = fileConfig.getAdvancedConfigurationSection("list.no-item-display");
            AdvancedConfigurationSection settingsConfig = fileConfig.getAdvancedConfigurationSection("settings");

            CEOutfitGroup result = config.loadCEOutfitGroup("no-item-display", mainConfig, settingsConfig);

            assertNotNull(result);
        }
    }
}
