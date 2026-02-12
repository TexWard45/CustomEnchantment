package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.customenchantment.enchant.CEGroup;
import com.bafmc.customenchantment.enchant.Priority;
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
 * Tests for CEEnchantGroupConfig class - configuration for enchantment groups.
 */
@DisplayName("CEEnchantGroupConfig Tests")
class CEEnchantGroupConfigTest {

    @TempDir
    Path tempDir;

    private CEEnchantGroupConfig config;

    @BeforeEach
    void setUp() {
        config = new CEEnchantGroupConfig();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            CEEnchantGroupConfig newConfig = new CEEnchantGroupConfig();

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }
    }

    @Nested
    @DisplayName("loadCEGroup Tests")
    class LoadCEGroupTests {

        @Test
        @DisplayName("Should create CEGroup from config section")
        void shouldCreateCEGroupFromConfigSection() throws IOException {
            File yamlFile = tempDir.resolve("group.yml").toFile();
            String content = """
                    test-group:
                      display: '&aTest Group'
                      enchant-display: '&bEnchant Display'
                      book-display: '&cBook Display'
                      prefix: '[TEST]'
                      disable-enchant-lore: false
                      success: '50-100'
                      success-sigma: 0.5
                      destroy: '0-10'
                      destroy-sigma: 1.0
                      valuable: 100
                      priority: NORMAL
                      craft: true
                      filter: false
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("test-group");

            CEGroup result = config.loadCEGroup("test-group", section);

            assertNotNull(result);
            assertEquals("test-group", result.getName());
            assertNotNull(result.getDisplay());
            assertEquals(Priority.NORMAL, result.getPriority());
            assertTrue(result.isCraft());
            assertFalse(result.isFilter());
        }

        @Test
        @DisplayName("Should handle missing optional fields with defaults")
        void shouldHandleMissingOptionalFieldsWithDefaults() throws IOException {
            File yamlFile = tempDir.resolve("minimal.yml").toFile();
            String content = """
                    minimal-group:
                      display: 'Minimal'
                      success: '50'
                      destroy: '0'
                      valuable: 50
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("minimal-group");

            CEGroup result = config.loadCEGroup("minimal-group", section);

            assertNotNull(result);
            assertEquals("minimal-group", result.getName());
            // Default priority should be NORMAL when not specified or when parsing fails
        }

        @Test
        @DisplayName("Should parse success range correctly")
        void shouldParseSuccessRangeCorrectly() throws IOException {
            File yamlFile = tempDir.resolve("range.yml").toFile();
            String content = """
                    range-group:
                      display: 'Range Test'
                      success: '25-75'
                      success-sigma: 0.5
                      destroy: '5-15'
                      destroy-sigma: 1.0
                      valuable: 75
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("range-group");

            CEGroup result = config.loadCEGroup("range-group", section);

            assertNotNull(result);
            assertNotNull(result.getSuccess());
            assertNotNull(result.getDestroy());
        }

        @Test
        @DisplayName("Should parse all Priority values")
        void shouldParseAllPriorityValues() throws IOException {
            for (Priority priority : Priority.values()) {
                File yamlFile = tempDir.resolve("priority-" + priority.name() + ".yml").toFile();
                String content = String.format("""
                        priority-group:
                          display: 'Priority Test'
                          success: '50'
                          destroy: '0'
                          valuable: 50
                          priority: %s
                        """, priority.name());
                Files.writeString(yamlFile.toPath(), content);

                AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
                AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("priority-group");

                CEGroup result = config.loadCEGroup("priority-group", section);

                assertNotNull(result);
                assertEquals(priority, result.getPriority());
            }
        }

        @Test
        @DisplayName("Should handle boolean fields correctly")
        void shouldHandleBooleanFieldsCorrectly() throws IOException {
            File yamlFile = tempDir.resolve("booleans.yml").toFile();
            String content = """
                    boolean-group:
                      display: 'Boolean Test'
                      success: '50'
                      destroy: '0'
                      valuable: 50
                      disable-enchant-lore: true
                      craft: true
                      filter: true
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("boolean-group");

            CEGroup result = config.loadCEGroup("boolean-group", section);

            assertNotNull(result);
            assertTrue(result.isDisableEnchantLore());
            assertTrue(result.isCraft());
            assertTrue(result.isFilter());
        }
    }

    @Nested
    @DisplayName("Method Existence Tests")
    class MethodExistenceTests {

        @Test
        @DisplayName("Should have loadCEGroup method")
        void shouldHaveLoadCEGroupMethod() throws NoSuchMethodException {
            Method method = CEEnchantGroupConfig.class.getMethod("loadCEGroup",
                    String.class, AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("loadCEGroup should return CEGroup")
        void loadCEGroupShouldReturnCEGroup() throws NoSuchMethodException {
            Method method = CEEnchantGroupConfig.class.getMethod("loadCEGroup",
                    String.class, AdvancedConfigurationSection.class);
            assertEquals(CEGroup.class, method.getReturnType());
        }
    }

    @Nested
    @DisplayName("YAML Parsing Tests")
    class YamlParsingTests {

        @Test
        @DisplayName("Should handle YAML with color codes")
        void shouldHandleYamlWithColorCodes() throws IOException {
            File yamlFile = tempDir.resolve("colors.yml").toFile();
            String content = """
                    color-group:
                      display: '&a&lGreen Bold'
                      enchant-display: '&b&oBlue Italic'
                      prefix: '&c[RED]'
                      success: '50'
                      destroy: '0'
                      valuable: 50
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("color-group");

            CEGroup result = config.loadCEGroup("color-group", section);

            assertNotNull(result);
            assertNotNull(result.getDisplay());
        }

        @Test
        @DisplayName("Should handle YAML with hex color codes")
        void shouldHandleYamlWithHexColorCodes() throws IOException {
            File yamlFile = tempDir.resolve("hex.yml").toFile();
            String content = """
                    hex-group:
                      display: '&#FF5500Orange Text'
                      success: '50'
                      destroy: '0'
                      valuable: 50
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("hex-group");

            CEGroup result = config.loadCEGroup("hex-group", section);

            assertNotNull(result);
            assertNotNull(result.getDisplay());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle zero valuable")
        void shouldHandleZeroValuable() throws IOException {
            File yamlFile = tempDir.resolve("zero.yml").toFile();
            String content = """
                    zero-group:
                      display: 'Zero Value'
                      success: '50'
                      destroy: '0'
                      valuable: 0
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("zero-group");

            CEGroup result = config.loadCEGroup("zero-group", section);

            assertNotNull(result);
            assertEquals(0, result.getValuable());
        }

        @Test
        @DisplayName("Should handle negative valuable")
        void shouldHandleNegativeValuable() throws IOException {
            File yamlFile = tempDir.resolve("negative.yml").toFile();
            String content = """
                    negative-group:
                      display: 'Negative Value'
                      success: '50'
                      destroy: '0'
                      valuable: -10
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("negative-group");

            CEGroup result = config.loadCEGroup("negative-group", section);

            assertNotNull(result);
            assertEquals(-10, result.getValuable());
        }

        @Test
        @DisplayName("Should handle single value success/destroy")
        void shouldHandleSingleValueSuccessDestroy() throws IOException {
            File yamlFile = tempDir.resolve("single.yml").toFile();
            String content = """
                    single-group:
                      display: 'Single Value'
                      success: '100'
                      destroy: '5'
                      valuable: 50
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("single-group");

            CEGroup result = config.loadCEGroup("single-group", section);

            assertNotNull(result);
            assertNotNull(result.getSuccess());
            assertNotNull(result.getDestroy());
        }

        @Test
        @DisplayName("Should handle empty prefix")
        void shouldHandleEmptyPrefix() throws IOException {
            File yamlFile = tempDir.resolve("noprefix.yml").toFile();
            String content = """
                    noprefix-group:
                      display: 'No Prefix'
                      prefix: ''
                      success: '50'
                      destroy: '0'
                      valuable: 50
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("noprefix-group");

            CEGroup result = config.loadCEGroup("noprefix-group", section);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle large valuable values")
        void shouldHandleLargeValuableValues() throws IOException {
            File yamlFile = tempDir.resolve("large.yml").toFile();
            String content = """
                    large-group:
                      display: 'Large Value'
                      success: '50'
                      destroy: '0'
                      valuable: 999999
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("large-group");

            CEGroup result = config.loadCEGroup("large-group", section);

            assertNotNull(result);
            assertEquals(999999, result.getValuable());
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("Should be in correct package")
        void shouldBeInCorrectPackage() {
            assertEquals("com.bafmc.customenchantment.config",
                    CEEnchantGroupConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should have protected loadConfig method")
        void shouldHaveProtectedLoadConfigMethod() throws NoSuchMethodException {
            Method method = CEEnchantGroupConfig.class.getDeclaredMethod("loadConfig");
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isProtected(method.getModifiers()));
        }
    }
}
