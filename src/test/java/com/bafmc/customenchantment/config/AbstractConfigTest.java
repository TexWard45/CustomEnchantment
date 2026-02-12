package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.config.AdvancedFileConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AbstractConfig class - base class for configuration loading.
 */
@DisplayName("AbstractConfig Tests")
class AbstractConfigTest {

    @TempDir
    Path tempDir;

    private TestableAbstractConfig config;

    /**
     * Concrete implementation for testing AbstractConfig.
     */
    private static class TestableAbstractConfig extends AbstractConfig {
        private AtomicInteger loadCount = new AtomicInteger(0);
        private AtomicBoolean loadCalled = new AtomicBoolean(false);

        @Override
        protected void loadConfig() {
            loadCalled.set(true);
            loadCount.incrementAndGet();
        }

        public boolean wasLoadCalled() {
            return loadCalled.get();
        }

        public int getLoadCount() {
            return loadCount.get();
        }

        public AdvancedFileConfiguration getConfig() {
            return config;
        }

        public void reset() {
            loadCalled.set(false);
            loadCount.set(0);
        }
    }

    @BeforeEach
    void setUp() {
        config = new TestableAbstractConfig();
    }

    @AfterEach
    void tearDown() {
        config = null;
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            TestableAbstractConfig newConfig = new TestableAbstractConfig();

            assertNotNull(newConfig);
            assertNull(newConfig.getConfig());
        }
    }

    @Nested
    @DisplayName("loadConfig(File) Tests")
    class LoadConfigFileTests {

        @Test
        @DisplayName("Should load single YAML file")
        void shouldLoadSingleYamlFile() throws IOException {
            File yamlFile = tempDir.resolve("test.yml").toFile();
            Files.writeString(yamlFile.toPath(), "key: value");

            config.loadConfig(yamlFile);

            assertTrue(config.wasLoadCalled());
            assertNotNull(config.getConfig());
        }

        @Test
        @DisplayName("Should not load non-YAML file")
        void shouldNotLoadNonYamlFile() throws IOException {
            File txtFile = tempDir.resolve("test.txt").toFile();
            Files.writeString(txtFile.toPath(), "key: value");

            config.loadConfig(txtFile);

            assertFalse(config.wasLoadCalled());
        }

        @Test
        @DisplayName("Should load all YAML files in directory")
        void shouldLoadAllYamlFilesInDirectory() throws IOException {
            File dir = tempDir.resolve("configs").toFile();
            dir.mkdir();

            Files.writeString(new File(dir, "config1.yml").toPath(), "key1: value1");
            Files.writeString(new File(dir, "config2.yml").toPath(), "key2: value2");
            Files.writeString(new File(dir, "config3.yml").toPath(), "key3: value3");

            config.loadConfig(dir);

            assertEquals(3, config.getLoadCount());
        }

        @Test
        @DisplayName("Should skip non-YAML files in directory")
        void shouldSkipNonYamlFilesInDirectory() throws IOException {
            File dir = tempDir.resolve("mixed").toFile();
            dir.mkdir();

            Files.writeString(new File(dir, "config.yml").toPath(), "key: value");
            Files.writeString(new File(dir, "readme.txt").toPath(), "readme content");
            Files.writeString(new File(dir, "data.json").toPath(), "{\"key\": \"value\"}");

            config.loadConfig(dir);

            assertEquals(1, config.getLoadCount());
        }

        @Test
        @DisplayName("Should recursively load YAML files in subdirectories")
        void shouldRecursivelyLoadYamlFilesInSubdirectories() throws IOException {
            File dir = tempDir.resolve("nested").toFile();
            dir.mkdir();

            File subDir = new File(dir, "subdir");
            subDir.mkdir();

            Files.writeString(new File(dir, "root.yml").toPath(), "root: value");
            Files.writeString(new File(subDir, "nested.yml").toPath(), "nested: value");

            config.loadConfig(dir);

            assertEquals(2, config.getLoadCount());
        }

        @Test
        @DisplayName("Should handle empty directory")
        void shouldHandleEmptyDirectory() {
            File emptyDir = tempDir.resolve("empty").toFile();
            emptyDir.mkdir();

            config.loadConfig(emptyDir);

            assertEquals(0, config.getLoadCount());
        }

        @Test
        @DisplayName("Should handle YAML file with yml extension case sensitivity")
        void shouldHandleYamlFileExtensionCaseSensitivity() throws IOException {
            // Only .yml (lowercase) should be loaded based on implementation
            Files.writeString(tempDir.resolve("lower.yml").toFile().toPath(), "key: value");

            config.loadConfig(tempDir.resolve("lower.yml").toFile());

            assertTrue(config.wasLoadCalled());
        }
    }

    @Nested
    @DisplayName("loadConfig(AdvancedFileConfiguration) Tests")
    class LoadConfigAdvancedFileConfigurationTests {

        @Test
        @DisplayName("Should load from AdvancedFileConfiguration")
        void shouldLoadFromAdvancedFileConfiguration() throws IOException {
            File yamlFile = tempDir.resolve("test.yml").toFile();
            Files.writeString(yamlFile.toPath(), "test-key: test-value");

            AdvancedFileConfiguration advConfig = new AdvancedFileConfiguration(yamlFile);

            config.loadConfig(advConfig);

            assertTrue(config.wasLoadCalled());
            assertSame(advConfig, config.getConfig());
        }

        @Test
        @DisplayName("Should set config before calling loadConfig")
        void shouldSetConfigBeforeCallingLoadConfig() throws IOException {
            File yamlFile = tempDir.resolve("test.yml").toFile();
            Files.writeString(yamlFile.toPath(), "key: value");

            AdvancedFileConfiguration advConfig = new AdvancedFileConfiguration(yamlFile);

            // Create a custom config that verifies config is set before loadConfig is called
            TestableAbstractConfig verifyConfig = new TestableAbstractConfig() {
                @Override
                protected void loadConfig() {
                    assertNotNull(this.config, "config should be set before loadConfig is called");
                    super.loadConfig();
                }
            };

            verifyConfig.loadConfig(advConfig);

            assertTrue(verifyConfig.wasLoadCalled());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle file with special characters in name")
        void shouldHandleFileWithSpecialCharactersInName() throws IOException {
            File specialFile = tempDir.resolve("config-test_v2.yml").toFile();
            Files.writeString(specialFile.toPath(), "key: value");

            config.loadConfig(specialFile);

            assertTrue(config.wasLoadCalled());
        }

        @Test
        @DisplayName("Should handle empty YAML file")
        void shouldHandleEmptyYamlFile() throws IOException {
            File emptyYaml = tempDir.resolve("empty.yml").toFile();
            Files.writeString(emptyYaml.toPath(), "");

            config.loadConfig(emptyYaml);

            assertTrue(config.wasLoadCalled());
        }

        @Test
        @DisplayName("Should handle YAML file with only comments")
        void shouldHandleYamlFileWithOnlyComments() throws IOException {
            File commentYaml = tempDir.resolve("comments.yml").toFile();
            Files.writeString(commentYaml.toPath(), "# This is a comment\n# Another comment");

            config.loadConfig(commentYaml);

            assertTrue(config.wasLoadCalled());
        }

        @Test
        @DisplayName("Should handle deeply nested directory structure")
        void shouldHandleDeeplyNestedDirectoryStructure() throws IOException {
            File level1 = tempDir.resolve("level1").toFile();
            File level2 = new File(level1, "level2");
            File level3 = new File(level2, "level3");
            level3.mkdirs();

            Files.writeString(new File(level3, "deep.yml").toPath(), "deep: value");

            config.loadConfig(level1);

            assertEquals(1, config.getLoadCount());
        }
    }
}
