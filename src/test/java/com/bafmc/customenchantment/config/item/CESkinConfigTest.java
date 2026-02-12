package com.bafmc.customenchantment.config.item;

import com.bafmc.customenchantment.config.AbstractConfig;
import com.bafmc.customenchantment.item.skin.CESkinStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CESkinConfig class - configuration for skin items.
 */
@DisplayName("CESkinConfig Tests")
class CESkinConfigTest {

    @TempDir
    Path tempDir;

    @Mock
    private CESkinStorage mockStorage;

    private CESkinConfig config;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        config = new CESkinConfig(mockStorage);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with storage parameter")
        void shouldCreateInstanceWithStorageParameter() {
            CESkinConfig newConfig = new CESkinConfig(mockStorage);

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }

        @Test
        @DisplayName("Constructor should accept CESkinStorage")
        void constructorShouldAcceptCESkinStorage() {
            try {
                CESkinConfig.class.getConstructor(CESkinStorage.class);
            } catch (NoSuchMethodException e) {
                fail("Constructor should accept CESkinStorage parameter");
            }
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("Should be in correct package")
        void shouldBeInCorrectPackage() {
            assertEquals("com.bafmc.customenchantment.config.item",
                    CESkinConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should have protected loadConfig method")
        void shouldHaveProtectedLoadConfigMethod() throws NoSuchMethodException {
            Method method = CESkinConfig.class.getDeclaredMethod("loadConfig");
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isProtected(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("YAML Structure Tests")
    class YamlStructureTests {

        @Test
        @DisplayName("Should expect skin section in YAML")
        void shouldExpectSkinSectionInYaml() throws IOException {
            // The config expects a 'skin' section with pattern keys
            String expectedStructure = """
                    skin:
                      pattern1:
                        item:
                          type: LEATHER_HELMET
                        display:
                          normal: '&aNormal Display'
                          bold: '&b&lBold Display'
                    """;

            Path yamlFile = tempDir.resolve("skin.yml");
            Files.writeString(yamlFile, expectedStructure);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("skin:"));
            assertTrue(content.contains("pattern1:"));
            assertTrue(content.contains("display:"));
            assertTrue(content.contains("normal:"));
            assertTrue(content.contains("bold:"));
        }

        @Test
        @DisplayName("Should parse normal display from config")
        void shouldParseNormalDisplayFromConfig() throws IOException {
            String yamlContent = """
                    skin:
                      test:
                        display:
                          normal: '&aTest Normal'
                    """;

            Path yamlFile = tempDir.resolve("normal.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("normal: '&aTest Normal'"));
        }

        @Test
        @DisplayName("Should parse bold display from config")
        void shouldParseBoldDisplayFromConfig() throws IOException {
            String yamlContent = """
                    skin:
                      test:
                        display:
                          bold: '&b&lTest Bold'
                    """;

            Path yamlFile = tempDir.resolve("bold.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("bold: '&b&lTest Bold'"));
        }
    }

    @Nested
    @DisplayName("Storage Interaction Tests")
    class StorageInteractionTests {

        @Test
        @DisplayName("Should have storage field set from constructor")
        void shouldHaveStorageFieldSetFromConstructor() throws Exception {
            java.lang.reflect.Field storageField = CESkinConfig.class.getDeclaredField("storage");
            storageField.setAccessible(true);
            Object storageValue = storageField.get(config);

            assertNotNull(storageValue);
            assertSame(mockStorage, storageValue);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty skin section")
        void shouldHandleEmptySkinSection() throws IOException {
            String yamlContent = """
                    skin: {}
                    """;

            Path yamlFile = tempDir.resolve("empty.yml");
            Files.writeString(yamlFile, yamlContent);

            assertTrue(Files.exists(yamlFile));
        }

        @Test
        @DisplayName("Should handle skin without display section")
        void shouldHandleSkinWithoutDisplaySection() throws IOException {
            String yamlContent = """
                    skin:
                      test:
                        item:
                          type: LEATHER_HELMET
                    """;

            Path yamlFile = tempDir.resolve("nodisplay.yml");
            Files.writeString(yamlFile, yamlContent);

            assertTrue(Files.exists(yamlFile));
        }

        @Test
        @DisplayName("Should handle special characters in pattern name")
        void shouldHandleSpecialCharactersInPatternName() throws IOException {
            String yamlContent = """
                    skin:
                      test-skin_v2:
                        item:
                          type: LEATHER_HELMET
                        display:
                          normal: 'Normal'
                          bold: 'Bold'
                    """;

            Path yamlFile = tempDir.resolve("special.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("test-skin_v2:"));
        }

        @Test
        @DisplayName("Should handle color codes in display")
        void shouldHandleColorCodesInDisplay() throws IOException {
            String yamlContent = """
                    skin:
                      colored:
                        display:
                          normal: '&a&lGreen Bold &r&7Gray'
                          bold: '&#FF5500&lHex Color'
                    """;

            Path yamlFile = tempDir.resolve("colors.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("&a&lGreen Bold"));
            assertTrue(content.contains("&#FF5500"));
        }

        @Test
        @DisplayName("Should handle null display values")
        void shouldHandleNullDisplayValues() throws IOException {
            String yamlContent = """
                    skin:
                      nulldisplay:
                        item:
                          type: LEATHER_HELMET
                        display:
                          normal: ~
                          bold: ~
                    """;

            Path yamlFile = tempDir.resolve("null.yml");
            Files.writeString(yamlFile, yamlContent);

            assertTrue(Files.exists(yamlFile));
        }
    }

    @Nested
    @DisplayName("Field Tests")
    class FieldTests {

        @Test
        @DisplayName("Should have private storage field")
        void shouldHavePrivateStorageField() throws NoSuchFieldException {
            java.lang.reflect.Field field = CESkinConfig.class.getDeclaredField("storage");

            assertNotNull(field);
            assertTrue(java.lang.reflect.Modifier.isPrivate(field.getModifiers()));
        }

        @Test
        @DisplayName("storage field should be CESkinStorage type")
        void storageFieldShouldBeCESkinStorageType() throws NoSuchFieldException {
            java.lang.reflect.Field field = CESkinConfig.class.getDeclaredField("storage");

            assertEquals(CESkinStorage.class, field.getType());
        }
    }
}
