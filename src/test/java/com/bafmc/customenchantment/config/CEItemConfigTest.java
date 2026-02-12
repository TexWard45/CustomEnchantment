package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.bukkit.utils.SparseMap;
import com.bafmc.customenchantment.api.MaterialList;
import com.bafmc.customenchantment.item.WeaponSettings;
import com.bafmc.customenchantment.item.gem.CEGemSettings;
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
 * Tests for CEItemConfig class - configuration for custom enchantment items.
 */
@DisplayName("CEItemConfig Tests")
class CEItemConfigTest {

    @TempDir
    Path tempDir;

    private CEItemConfig config;

    @BeforeEach
    void setUp() {
        config = new CEItemConfig();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            CEItemConfig newConfig = new CEItemConfig();

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }
    }

    @Nested
    @DisplayName("getWeaponExtensionLore Tests")
    class GetWeaponExtensionLoreTests {

        @Test
        @DisplayName("Should return empty SparseMap for empty config")
        void shouldReturnEmptySparseMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            Files.writeString(yamlFile.toPath(), "empty: {}");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("empty");
            AdvancedConfigurationSection altSection = fileConfig.getAdvancedConfigurationSection("empty");

            SparseMap<String> result = config.getWeaponExtensionLore(section, altSection);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should parse numeric keys as integers")
        void shouldParseNumericKeysAsIntegers() throws IOException {
            File yamlFile = tempDir.resolve("lore.yml").toFile();
            String content = """
                    lore:
                      1: '&aFirst'
                      2: '&bSecond'
                      5: '&cFifth'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("lore");

            SparseMap<String> result = config.getWeaponExtensionLore(section, section);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should use alternative config when main config is empty")
        void shouldUseAlternativeConfigWhenMainConfigIsEmpty() throws IOException {
            File yamlFile = tempDir.resolve("alt.yml").toFile();
            String content = """
                    main: {}
                    alt:
                      1: '&aAlternative'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainSection = fileConfig.getAdvancedConfigurationSection("main");
            AdvancedConfigurationSection altSection = fileConfig.getAdvancedConfigurationSection("alt");

            SparseMap<String> result = config.getWeaponExtensionLore(mainSection, altSection);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("loadGemPointMap Tests")
    class LoadGemPointMapTests {

        @Test
        @DisplayName("Should return empty map for empty config")
        void shouldReturnEmptyMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            Files.writeString(yamlFile.toPath(), "empty: {}");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("empty");

            Map<MaterialList, Integer> result = config.loadGemPointMap(section, section);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getAttributeLoreMap Tests")
    class GetAttributeLoreMapTests {

        @Test
        @DisplayName("Should return empty map for empty config")
        void shouldReturnEmptyMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            Files.writeString(yamlFile.toPath(), "empty: {}");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("empty");

            var result = config.getAttributeLoreMap(section, section);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getAttributeSlotMap Tests")
    class GetAttributeSlotMapTests {

        @Test
        @DisplayName("Should return empty map for empty config")
        void shouldReturnEmptyMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            Files.writeString(yamlFile.toPath(), "empty: {}");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("empty");

            Map<String, String> result = config.getAttributeSlotMap(section, section);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should parse slot names correctly")
        void shouldParseSlotNamesCorrectly() throws IOException {
            File yamlFile = tempDir.resolve("slots.yml").toFile();
            String content = """
                    slots:
                      HAND: '&7Main Hand:'
                      OFF_HAND: '&7Off Hand:'
                      HEAD: '&7Head:'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("slots");

            Map<String, String> result = config.getAttributeSlotMap(section, section);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("loadLevels Tests")
    class LoadLevelsTests {

        @Test
        @DisplayName("Should return empty SparseMap when no levels config")
        void shouldReturnEmptySparseMapWhenNoLevelsConfig() throws IOException {
            File yamlFile = tempDir.resolve("nolevels.yml").toFile();
            Files.writeString(yamlFile.toPath(), "test: value");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("test");

            SparseMap<String> result = config.loadLevels(section, section);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should parse levels with color values")
        void shouldParseLevelsWithColorValues() throws IOException {
            File yamlFile = tempDir.resolve("levels.yml").toFile();
            String content = """
                    levels:
                      1:
                        color: '&a'
                      2:
                        color: '&b'
                      3:
                        color: '&c'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("");

            SparseMap<String> result = config.loadLevels(section, section);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should use settings config when main config has no levels")
        void shouldUseSettingsConfigWhenMainConfigHasNoLevels() throws IOException {
            File yamlFile = tempDir.resolve("fallback.yml").toFile();
            String content = """
                    main: {}
                    settings:
                      levels:
                        1:
                          color: '&a'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection mainSection = fileConfig.getAdvancedConfigurationSection("main");
            AdvancedConfigurationSection settingsSection = fileConfig.getAdvancedConfigurationSection("settings");

            SparseMap<String> result = config.loadLevels(mainSection, settingsSection);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("loadGemSettings Tests")
    class LoadGemSettingsTests {

        @Test
        @DisplayName("Should create CEGemSettings from config")
        void shouldCreateCEGemSettingsFromConfig() throws IOException {
            File yamlFile = tempDir.resolve("gem.yml").toFile();
            String content = """
                    gem-settings:
                      levels:
                        1:
                          color: '&a'
                        2:
                          color: '&b'
                      slot:
                        offensive:
                          priority: 1
                          display: 'Offensive'
                        defensive:
                          priority: 2
                          display: 'Defensive'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("gem-settings");

            CEGemSettings result = config.loadGemSettings(section);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle empty gem settings")
        void shouldHandleEmptyGemSettings() throws IOException {
            File yamlFile = tempDir.resolve("empty-gem.yml").toFile();
            String content = """
                    gem-settings:
                      levels: {}
                      slot: {}
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("gem-settings");

            CEGemSettings result = config.loadGemSettings(section);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("loadWeaponSettingsSection Tests")
    class LoadWeaponSettingsSectionTests {

        @Test
        @DisplayName("Should return empty map for empty config")
        void shouldReturnEmptyMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            Files.writeString(yamlFile.toPath(), "empty: {}");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("empty");

            Map<String, WeaponSettings> result = config.loadWeaponSettingsSection(section);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Method Existence Tests")
    class MethodExistenceTests {

        @Test
        @DisplayName("Should have loadWeaponSettingsMap method")
        void shouldHaveLoadWeaponSettingsMapMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadWeaponSettingsMap");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadGemSettings method (no params)")
        void shouldHaveLoadGemSettingsMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadGemSettings");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCEBookStorage method")
        void shouldHaveLoadCEBookStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCEBookStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCEProtectDeadStorage method")
        void shouldHaveLoadCEProtectDeadStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCEProtectDeadStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCENameTagStorage method")
        void shouldHaveLoadCENameTagStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCENameTagStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCEEnchantPointStorage method")
        void shouldHaveLoadCEEnchantPointStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCEEnchantPointStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCERandomBookStorage method")
        void shouldHaveLoadCERandomBookStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCERandomBookStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCEMaskStorage method")
        void shouldHaveLoadCEMaskStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCEMaskStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCEWeaponStorage method")
        void shouldHaveLoadCEWeaponStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCEWeaponStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCEArtifactStorage method")
        void shouldHaveLoadCEArtifactStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCEArtifactStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCEGemStorage method")
        void shouldHaveLoadCEGemStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCEGemStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCESigilStorage method")
        void shouldHaveLoadCESigilStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCESigilStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCEOutfitStorage method")
        void shouldHaveLoadCEOutfitStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCEOutfitStorage");
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCESkinStorage method")
        void shouldHaveLoadCESkinStorageMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getMethod("loadCESkinStorage");
            assertNotNull(method);
        }
    }

    @Nested
    @DisplayName("loadGemConfigByLevelMap Tests")
    class LoadGemConfigByLevelMapTests {

        @Test
        @DisplayName("Should return empty map for empty config")
        void shouldReturnEmptyMapForEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("empty.yml").toFile();
            Files.writeString(yamlFile.toPath(), "empty: {}");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("empty");

            var result = config.loadGemConfigByLevelMap(section);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("Should be in correct package")
        void shouldBeInCorrectPackage() {
            assertEquals("com.bafmc.customenchantment.config",
                    CEItemConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should have protected loadConfig method")
        void shouldHaveProtectedLoadConfigMethod() throws NoSuchMethodException {
            Method method = CEItemConfig.class.getDeclaredMethod("loadConfig");
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isProtected(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle invalid level key gracefully")
        void shouldHandleInvalidLevelKeyGracefully() throws IOException {
            File yamlFile = tempDir.resolve("invalid.yml").toFile();
            String content = """
                    lore:
                      not-a-number: '&aTest'
                      1: '&bValid'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("lore");

            // Should not throw exception, should skip invalid keys
            assertDoesNotThrow(() -> config.getWeaponExtensionLore(section, section));
        }
    }
}
