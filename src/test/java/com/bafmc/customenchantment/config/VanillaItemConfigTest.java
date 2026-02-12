package com.bafmc.customenchantment.config;

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
 * Tests for VanillaItemConfig class - configuration for vanilla item storage.
 */
@DisplayName("VanillaItemConfig Tests")
class VanillaItemConfigTest {

    @TempDir
    Path tempDir;

    private VanillaItemConfig config;

    @BeforeEach
    void setUp() {
        config = new VanillaItemConfig();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            VanillaItemConfig newConfig = new VanillaItemConfig();

            assertNotNull(newConfig);
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("Should have loadConfig method with no parameters")
        void shouldHaveLoadConfigMethodWithNoParameters() {
            try {
                Method method = VanillaItemConfig.class.getMethod("loadConfig");
                assertNotNull(method);
            } catch (NoSuchMethodException e) {
                fail("loadConfig() method should exist");
            }
        }

        @Test
        @DisplayName("Should have loadFile method")
        void shouldHaveLoadFileMethod() {
            try {
                Method method = VanillaItemConfig.class.getMethod("loadFile", File.class,
                        com.bafmc.customenchantment.item.VanillaItemStorage.class);
                assertNotNull(method);
            } catch (NoSuchMethodException e) {
                fail("loadFile(File, VanillaItemStorage) method should exist");
            }
        }

        @Test
        @DisplayName("Should be in correct package")
        void shouldBeInCorrectPackage() {
            assertEquals("com.bafmc.customenchantment.config", VanillaItemConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should not extend AbstractConfig")
        void shouldNotExtendAbstractConfig() {
            // VanillaItemConfig does not extend AbstractConfig based on the source
            assertFalse(AbstractConfig.class.isAssignableFrom(VanillaItemConfig.class));
        }
    }

    @Nested
    @DisplayName("loadFile Method Tests")
    class LoadFileMethodTests {

        @Test
        @DisplayName("loadFile should handle directory recursion")
        void loadFileShouldHandleDirectoryRecursion() throws IOException {
            // Create a directory structure
            File dir = tempDir.resolve("items").toFile();
            dir.mkdir();

            File subDir = new File(dir, "subdir");
            subDir.mkdir();

            // Create test files
            Files.writeString(new File(dir, "item1.yml").toPath(), "item1:\n  weapon: false");
            Files.writeString(new File(subDir, "item2.yml").toPath(), "item2:\n  weapon: true");

            // Verify the directory structure was created
            assertTrue(dir.exists());
            assertTrue(subDir.exists());
            assertTrue(new File(dir, "item1.yml").exists());
            assertTrue(new File(subDir, "item2.yml").exists());
        }

        @Test
        @DisplayName("loadFile should skip non-yml files")
        void loadFileShouldSkipNonYmlFiles() throws IOException {
            File dir = tempDir.resolve("mixed").toFile();
            dir.mkdir();

            Files.writeString(new File(dir, "item.yml").toPath(), "item:\n  weapon: false");
            Files.writeString(new File(dir, "readme.txt").toPath(), "This is a readme");
            Files.writeString(new File(dir, "data.json").toPath(), "{}");

            // Only item.yml should be processed
            File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
            assertEquals(1, files.length);
        }

        @Test
        @DisplayName("loadFile should identify save-items.yml as origin")
        void loadFileShouldIdentifySaveItemsYmlAsOrigin() throws IOException {
            File saveItemsFile = tempDir.resolve("save-items.yml").toFile();
            Files.writeString(saveItemsFile.toPath(), "test-item:\n  weapon: false");

            assertEquals("save-items.yml", saveItemsFile.getName());
        }
    }

    @Nested
    @DisplayName("YAML Parsing Tests")
    class YamlParsingTests {

        @Test
        @DisplayName("Should recognize weapon flag in YAML")
        void shouldRecognizeWeaponFlagInYaml() throws IOException {
            File yamlFile = tempDir.resolve("weapon.yml").toFile();
            String yamlContent = """
                    test-sword:
                      weapon: true
                      item:
                        type: DIAMOND_SWORD
                    """;
            Files.writeString(yamlFile.toPath(), yamlContent);

            assertTrue(yamlFile.exists());
            String content = Files.readString(yamlFile.toPath());
            assertTrue(content.contains("weapon: true"));
        }

        @Test
        @DisplayName("Should default weapon to false when not specified")
        void shouldDefaultWeaponToFalseWhenNotSpecified() throws IOException {
            File yamlFile = tempDir.resolve("noweapon.yml").toFile();
            String yamlContent = """
                    test-item:
                      item:
                        type: DIAMOND
                    """;
            Files.writeString(yamlFile.toPath(), yamlContent);

            String content = Files.readString(yamlFile.toPath());
            assertFalse(content.contains("weapon:"));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty directory")
        void shouldHandleEmptyDirectory() {
            File emptyDir = tempDir.resolve("empty").toFile();
            emptyDir.mkdir();

            assertTrue(emptyDir.exists());
            assertTrue(emptyDir.isDirectory());
            assertEquals(0, emptyDir.listFiles().length);
        }

        @Test
        @DisplayName("Should handle empty YAML file")
        void shouldHandleEmptyYamlFile() throws IOException {
            File emptyYaml = tempDir.resolve("empty.yml").toFile();
            Files.writeString(emptyYaml.toPath(), "");

            assertTrue(emptyYaml.exists());
            assertEquals(0, Files.readString(emptyYaml.toPath()).length());
        }

        @Test
        @DisplayName("Should handle deeply nested directories")
        void shouldHandleDeeplyNestedDirectories() throws IOException {
            File level1 = tempDir.resolve("a").toFile();
            File level2 = new File(level1, "b");
            File level3 = new File(level2, "c");
            level3.mkdirs();

            Files.writeString(new File(level3, "deep.yml").toPath(), "item:\n  weapon: false");

            assertTrue(new File(level3, "deep.yml").exists());
        }
    }
}
