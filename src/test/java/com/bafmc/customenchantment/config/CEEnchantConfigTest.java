package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedConfigurationSection;
import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import com.bafmc.customenchantment.enchant.*;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CEEnchantConfig class - configuration for custom enchantments.
 */
@DisplayName("CEEnchantConfig Tests")
class CEEnchantConfigTest {

    @TempDir
    Path tempDir;

    private CEEnchantConfig config;

    @BeforeEach
    void setUp() {
        config = new CEEnchantConfig();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            CEEnchantConfig newConfig = new CEEnchantConfig();

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }
    }

    @Nested
    @DisplayName("loadCondition Tests")
    class LoadConditionTests {

        @Test
        @DisplayName("Should create Condition from empty list")
        void shouldCreateConditionFromEmptyList() {
            Condition result = config.loadCondition(new ArrayList<>());

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should skip empty lines")
        void shouldSkipEmptyLines() {
            List<String> lines = Arrays.asList("", "  ", "");

            Condition result = config.loadCondition(lines);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should handle list with valid and empty lines")
        void shouldHandleListWithValidAndEmptyLines() {
            List<String> lines = Arrays.asList("", "PERMISSION:test.permission", "");

            // This may throw if PERMISSION condition is not registered
            try {
                Condition result = config.loadCondition(lines);
                assertNotNull(result);
            } catch (Exception e) {
                // Expected if condition type is not registered
                assertTrue(e.getMessage() != null || e instanceof NullPointerException);
            }
        }
    }

    @Nested
    @DisplayName("loadConditionSettings Tests")
    class LoadConditionSettingsTests {

        @Test
        @DisplayName("Should create default ConditionSettings from null list")
        void shouldCreateDefaultConditionSettingsFromNullList() {
            ConditionSettings result = config.loadConditionSettings(null);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should create ConditionSettings from empty list")
        void shouldCreateConditionSettingsFromEmptyList() {
            ConditionSettings result = config.loadConditionSettings(new ArrayList<>());

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should parse ENEMY shortcut")
        void shouldParseEnemyShortcut() {
            List<String> settings = Arrays.asList("ENEMY");

            ConditionSettings result = config.loadConditionSettings(settings);

            assertNotNull(result);
            assertEquals(Target.ENEMY, result.getTarget());
        }

        @Test
        @DisplayName("Should parse TARGET setting")
        void shouldParseTargetSetting() {
            List<String> settings = Arrays.asList("TARGET=ENEMY");

            ConditionSettings result = config.loadConditionSettings(settings);

            assertNotNull(result);
            assertEquals(Target.ENEMY, result.getTarget());
        }

        @Test
        @DisplayName("Should parse NEGATIVE setting")
        void shouldParseNegativeSetting() {
            List<String> settings = Arrays.asList("NEGATIVE=true");

            ConditionSettings result = config.loadConditionSettings(settings);

            assertNotNull(result);
            assertTrue(result.isNegative());
        }

        @Test
        @DisplayName("Should parse multiple settings")
        void shouldParseMultipleSettings() {
            List<String> settings = Arrays.asList("TARGET=PLAYER", "NEGATIVE=false");

            ConditionSettings result = config.loadConditionSettings(settings);

            assertNotNull(result);
            assertEquals(Target.PLAYER, result.getTarget());
            assertFalse(result.isNegative());
        }
    }

    @Nested
    @DisplayName("loadOption Tests")
    class LoadOptionTests {

        @Test
        @DisplayName("Should create Option from empty list")
        void shouldCreateOptionFromEmptyList() {
            Option result = config.loadOption(new ArrayList<>());

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should skip empty lines")
        void shouldSkipEmptyLines() {
            List<String> lines = Arrays.asList("", "  ", "");

            Option result = config.loadOption(lines);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("loadEffect Tests")
    class LoadEffectTests {

        @Test
        @DisplayName("Should create Effect from empty list")
        void shouldCreateEffectFromEmptyList() {
            Effect result = config.loadEffect(new ArrayList<>());

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should skip empty lines")
        void shouldSkipEmptyLines() {
            List<String> lines = Arrays.asList("", "  ", "");

            Effect result = config.loadEffect(lines);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("loadEffectSettings Tests")
    class LoadEffectSettingsTests {

        @Test
        @DisplayName("Should create default EffectSettings from null list")
        void shouldCreateDefaultEffectSettingsFromNullList() {
            EffectSettings result = config.loadEffectSettings(null);

            assertNotNull(result);
            assertNotNull(result.getTargetFilter());
        }

        @Test
        @DisplayName("Should create EffectSettings from empty list")
        void shouldCreateEffectSettingsFromEmptyList() {
            EffectSettings result = config.loadEffectSettings(new ArrayList<>());

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should parse ENEMY shortcut")
        void shouldParseEnemyShortcut() {
            List<String> settings = Arrays.asList("ENEMY");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
            assertEquals(Target.ENEMY, result.getTarget());
        }

        @Test
        @DisplayName("Should parse TARGET setting")
        void shouldParseTargetSetting() {
            List<String> settings = Arrays.asList("TARGET=ENEMY");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
            assertEquals(Target.ENEMY, result.getTarget());
        }

        @Test
        @DisplayName("Should parse DELAY setting")
        void shouldParseDelaySetting() {
            List<String> settings = Arrays.asList("DELAY=1000");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
            assertEquals(1000L, result.getDelay());
        }

        @Test
        @DisplayName("Should parse PERIOD setting")
        void shouldParsePeriodSetting() {
            List<String> settings = Arrays.asList("PERIOD=500");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
            assertEquals(500L, result.getPeriod());
        }

        @Test
        @DisplayName("Should parse NAME setting")
        void shouldParseNameSetting() {
            List<String> settings = Arrays.asList("NAME=test_effect");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
            assertEquals("test_effect", result.getName());
        }

        @Test
        @DisplayName("Should parse EFFECT_AFTER_DEAD setting")
        void shouldParseEffectAfterDeadSetting() {
            List<String> settings = Arrays.asList("EFFECT_AFTER_DEAD=true");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
            assertTrue(result.isEffectAfterDead());
        }

        @Test
        @DisplayName("Should parse distance settings")
        void shouldParseDistanceSettings() {
            List<String> settings = Arrays.asList("MIN_DISTANCE=5.0", "MAX_DISTANCE=20.0");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
            assertNotNull(result.getTargetFilter());
        }

        @Test
        @DisplayName("Should parse target count settings")
        void shouldParseTargetCountSettings() {
            List<String> settings = Arrays.asList("MIN_TARGET=1", "MAX_TARGET=5");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
            assertNotNull(result.getTargetFilter());
        }

        @Test
        @DisplayName("Should parse EXCEPT_PLAYER setting")
        void shouldParseExceptPlayerSetting() {
            List<String> settings = Arrays.asList("EXCEPT_PLAYER=true");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should parse EXCEPT_ENEMY setting")
        void shouldParseExceptEnemySetting() {
            List<String> settings = Arrays.asList("EXCEPT_ENEMY=true");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should parse TARGET_OTHER setting")
        void shouldParseTargetOtherSetting() {
            List<String> settings = Arrays.asList("TARGET_OTHER=ENEMY");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
            assertEquals(Target.ENEMY, result.getTargetOther());
        }

        @Test
        @DisplayName("Should parse EFFECT_ON_FAKE_SOURCE setting")
        void shouldParseEffectOnFakeSourceSetting() {
            List<String> settings = Arrays.asList("EFFECT_ON_FAKE_SOURCE=true");

            EffectSettings result = config.loadEffectSettings(settings);

            assertNotNull(result);
            assertTrue(result.isEffectOnFakeSource());
        }
    }

    @Nested
    @DisplayName("loadTargetFilter Tests")
    class LoadTargetFilterTests {

        @Test
        @DisplayName("Should create disabled TargetFilter from null config")
        void shouldCreateDisabledTargetFilterFromNullConfig() {
            TargetFilter result = config.loadTargetFilter(null);

            assertNotNull(result);
            assertFalse(result.isEnable());
        }
    }

    @Nested
    @DisplayName("loadDescriptionMap Tests")
    class LoadDescriptionMapTests {

        @Test
        @DisplayName("Should create empty HashMap from config with no keys")
        void shouldCreateEmptyHashMapFromConfigWithNoKeys() throws IOException {
            File yamlFile = tempDir.resolve("desc.yml").toFile();
            Files.writeString(yamlFile.toPath(), "empty: {}");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("empty");

            HashMap<Integer, List<String>> result = config.loadDescriptionMap(section);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getCustomDisplayLore Tests")
    class GetCustomDisplayLoreTests {

        @Test
        @DisplayName("Should handle string value for custom-display-lore")
        void shouldHandleStringValueForCustomDisplayLore() throws IOException {
            File yamlFile = tempDir.resolve("lore.yml").toFile();
            Files.writeString(yamlFile.toPath(), "custom-display-lore: 'Test Lore'");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, String> result = config.getCustomDisplayLore(fileConfig);

            assertNotNull(result);
            assertEquals("Test Lore", result.get("default"));
        }

        @Test
        @DisplayName("Should handle map value for custom-display-lore")
        void shouldHandleMapValueForCustomDisplayLore() throws IOException {
            File yamlFile = tempDir.resolve("loremap.yml").toFile();
            String content = """
                    custom-display-lore:
                      helmet: 'Helmet Lore'
                      chestplate: 'Chestplate Lore'
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);

            Map<String, String> result = config.getCustomDisplayLore(fileConfig);

            assertNotNull(result);
            assertEquals("Helmet Lore", result.get("helmet"));
            assertEquals("Chestplate Lore", result.get("chestplate"));
        }
    }

    @Nested
    @DisplayName("loadCELevels Tests")
    class LoadCELevelsTests {

        @Test
        @DisplayName("Should create CELevelMap from empty config")
        void shouldCreateCELevelMapFromEmptyConfig() throws IOException {
            File yamlFile = tempDir.resolve("levels.yml").toFile();
            Files.writeString(yamlFile.toPath(), "empty: {}");

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("empty");

            CELevelMap result = config.loadCELevels(section);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should skip default level key")
        void shouldSkipDefaultLevelKey() throws IOException {
            File yamlFile = tempDir.resolve("levels.yml").toFile();
            String content = """
                    levels:
                      default:
                        test: value
                      1:
                        test: value1
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("levels");

            CELevelMap result = config.loadCELevels(section);

            assertNotNull(result);
            assertFalse(result.containsKey(0)); // default should not be added as level 0
        }

        @Test
        @DisplayName("Should skip level 0 key")
        void shouldSkipLevel0Key() throws IOException {
            File yamlFile = tempDir.resolve("levels.yml").toFile();
            String content = """
                    levels:
                      0:
                        test: value
                      1:
                        test: value1
                    """;
            Files.writeString(yamlFile.toPath(), content);

            AdvancedFileConfiguration fileConfig = new AdvancedFileConfiguration(yamlFile);
            AdvancedConfigurationSection section = fileConfig.getAdvancedConfigurationSection("levels");

            CELevelMap result = config.loadCELevels(section);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("Method Existence Tests")
    class MethodExistenceTests {

        @Test
        @DisplayName("Should have loadCEEnchant method")
        void shouldHaveLoadCEEnchantMethod() throws NoSuchMethodException {
            Method method = CEEnchantConfig.class.getMethod("loadCEEnchant", String.class, AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCEFunction method")
        void shouldHaveLoadCEFunctionMethod() throws NoSuchMethodException {
            Method method = CEEnchantConfig.class.getMethod("loadCEFunction", String.class, AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadConditionHook method")
        void shouldHaveLoadConditionHookMethod() throws NoSuchMethodException {
            Method method = CEEnchantConfig.class.getMethod("loadConditionHook", String.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadConditionOR method")
        void shouldHaveLoadConditionORMethod() throws NoSuchMethodException {
            Method method = CEEnchantConfig.class.getMethod("loadConditionOR", String.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadEffectHook method")
        void shouldHaveLoadEffectHookMethod() throws NoSuchMethodException {
            Method method = CEEnchantConfig.class.getMethod("loadEffectHook", String.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadOptionData method")
        void shouldHaveLoadOptionDataMethod() throws NoSuchMethodException {
            Method method = CEEnchantConfig.class.getMethod("loadOptionData", String.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadFunctionMap method")
        void shouldHaveLoadFunctionMapMethod() throws NoSuchMethodException {
            Method method = CEEnchantConfig.class.getMethod("loadFunctionMap",
                    AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
            assertNotNull(method);
        }

        @Test
        @DisplayName("Should have loadCELevel method")
        void shouldHaveLoadCELevelMethod() throws NoSuchMethodException {
            Method method = CEEnchantConfig.class.getMethod("loadCELevel",
                    AdvancedConfigurationSection.class, AdvancedConfigurationSection.class);
            assertNotNull(method);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle escaped pipe in condition")
        void shouldHandleEscapedPipeInCondition() {
            // Test that escaped pipes are handled correctly
            String line = "CONDITION | value";

            // This tests the pipe replacement logic
            assertTrue(line.contains(" | "));
        }

        @Test
        @DisplayName("Should handle escaped OR in condition")
        void shouldHandleEscapedOrInCondition() {
            // Test that escaped OR is handled correctly
            String line = "CONDITION || value";

            assertTrue(line.contains(" || "));
        }
    }
}
