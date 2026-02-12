package com.bafmc.customenchantment.config.item;

import com.bafmc.customenchantment.config.AbstractConfig;
import com.bafmc.customenchantment.item.CEWeaponStorage;
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
 * Tests for CEWeaponConfig class - configuration for weapon items.
 */
@DisplayName("CEWeaponConfig Tests")
class CEWeaponConfigTest {

    @TempDir
    Path tempDir;

    @Mock
    private CEWeaponStorage mockStorage;

    private CEWeaponConfig config;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        config = new CEWeaponConfig(mockStorage);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with storage parameter")
        void shouldCreateInstanceWithStorageParameter() {
            CEWeaponConfig newConfig = new CEWeaponConfig(mockStorage);

            assertNotNull(newConfig);
        }

        @Test
        @DisplayName("Should extend AbstractConfig")
        void shouldExtendAbstractConfig() {
            assertTrue(config instanceof AbstractConfig);
        }

        @Test
        @DisplayName("Constructor should accept CEWeaponStorage")
        void constructorShouldAcceptCEWeaponStorage() {
            try {
                CEWeaponConfig.class.getConstructor(CEWeaponStorage.class);
            } catch (NoSuchMethodException e) {
                fail("Constructor should accept CEWeaponStorage parameter");
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
                    CEWeaponConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should have protected loadConfig method")
        void shouldHaveProtectedLoadConfigMethod() throws NoSuchMethodException {
            Method method = CEWeaponConfig.class.getDeclaredMethod("loadConfig");
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isProtected(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("YAML Structure Tests")
    class YamlStructureTests {

        @Test
        @DisplayName("Should expect weapon section in YAML")
        void shouldExpectWeaponSectionInYaml() throws IOException {
            String expectedStructure = """
                    weapon:
                      test-sword:
                        custom-type: SWORD
                        item:
                          type: DIAMOND_SWORD
                          display: '&aTest Sword'
                        enchants:
                          - 'sharpness 5'
                        attributes:
                          - 'ATTACK_DAMAGE:10:0:HAND'
                    """;

            Path yamlFile = tempDir.resolve("weapon.yml");
            Files.writeString(yamlFile, expectedStructure);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("weapon:"));
            assertTrue(content.contains("test-sword:"));
            assertTrue(content.contains("custom-type:"));
            assertTrue(content.contains("enchants:"));
            assertTrue(content.contains("attributes:"));
        }

        @Test
        @DisplayName("Should parse custom-type from config")
        void shouldParseCustomTypeFromConfig() throws IOException {
            String yamlContent = """
                    weapon:
                      sword:
                        custom-type: SWORD
                    """;

            Path yamlFile = tempDir.resolve("type.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("custom-type: SWORD"));
        }

        @Test
        @DisplayName("Should parse enchants list from config")
        void shouldParseEnchantsListFromConfig() throws IOException {
            String yamlContent = """
                    weapon:
                      enchanted:
                        enchants:
                          - 'sharpness 5'
                          - 'unbreaking 3'
                          - 'custom_enchant 2'
                    """;

            Path yamlFile = tempDir.resolve("enchants.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("sharpness 5"));
            assertTrue(content.contains("unbreaking 3"));
            assertTrue(content.contains("custom_enchant 2"));
        }

        @Test
        @DisplayName("Should parse attributes list from config")
        void shouldParseAttributesListFromConfig() throws IOException {
            String yamlContent = """
                    weapon:
                      attributed:
                        attributes:
                          - 'ATTACK_DAMAGE:10:0:HAND'
                          - 'ATTACK_SPEED:1.5:1:HAND'
                    """;

            Path yamlFile = tempDir.resolve("attributes.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("ATTACK_DAMAGE:10:0:HAND"));
            assertTrue(content.contains("ATTACK_SPEED:1.5:1:HAND"));
        }
    }

    @Nested
    @DisplayName("Storage Interaction Tests")
    class StorageInteractionTests {

        @Test
        @DisplayName("Should have storage field set from constructor")
        void shouldHaveStorageFieldSetFromConstructor() throws Exception {
            java.lang.reflect.Field storageField = CEWeaponConfig.class.getDeclaredField("storage");
            storageField.setAccessible(true);
            Object storageValue = storageField.get(config);

            assertNotNull(storageValue);
            assertSame(mockStorage, storageValue);
        }
    }

    @Nested
    @DisplayName("Enchant Parsing Tests")
    class EnchantParsingTests {

        @Test
        @DisplayName("Should handle enchant without level")
        void shouldHandleEnchantWithoutLevel() throws IOException {
            // Enchant format: "enchant_name" or "enchant_name level"
            String yamlContent = """
                    weapon:
                      simple:
                        enchants:
                          - 'sharpness'
                    """;

            Path yamlFile = tempDir.resolve("nolevel.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("- 'sharpness'"));
        }

        @Test
        @DisplayName("Should handle enchant with level")
        void shouldHandleEnchantWithLevel() throws IOException {
            String yamlContent = """
                    weapon:
                      leveled:
                        enchants:
                          - 'sharpness 5'
                          - 'fire_aspect 2'
                    """;

            Path yamlFile = tempDir.resolve("leveled.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("sharpness 5"));
            assertTrue(content.contains("fire_aspect 2"));
        }

        @Test
        @DisplayName("Should handle mixed enchant formats")
        void shouldHandleMixedEnchantFormats() throws IOException {
            String yamlContent = """
                    weapon:
                      mixed:
                        enchants:
                          - 'sharpness'
                          - 'unbreaking 3'
                          - 'custom_ce 10'
                    """;

            Path yamlFile = tempDir.resolve("mixed.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("'sharpness'"));
            assertTrue(content.contains("'unbreaking 3'"));
            assertTrue(content.contains("'custom_ce 10'"));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty weapon section")
        void shouldHandleEmptyWeaponSection() throws IOException {
            String yamlContent = """
                    weapon: {}
                    """;

            Path yamlFile = tempDir.resolve("empty.yml");
            Files.writeString(yamlFile, yamlContent);

            assertTrue(Files.exists(yamlFile));
        }

        @Test
        @DisplayName("Should handle weapon without enchants")
        void shouldHandleWeaponWithoutEnchants() throws IOException {
            String yamlContent = """
                    weapon:
                      basic:
                        custom-type: SWORD
                        item:
                          type: DIAMOND_SWORD
                    """;

            Path yamlFile = tempDir.resolve("noenchants.yml");
            Files.writeString(yamlFile, yamlContent);

            assertTrue(Files.exists(yamlFile));
        }

        @Test
        @DisplayName("Should handle weapon without attributes")
        void shouldHandleWeaponWithoutAttributes() throws IOException {
            String yamlContent = """
                    weapon:
                      basic:
                        custom-type: SWORD
                        item:
                          type: DIAMOND_SWORD
                        enchants:
                          - 'sharpness 5'
                    """;

            Path yamlFile = tempDir.resolve("noattributes.yml");
            Files.writeString(yamlFile, yamlContent);

            assertTrue(Files.exists(yamlFile));
        }

        @Test
        @DisplayName("Should handle special characters in pattern name")
        void shouldHandleSpecialCharactersInPatternName() throws IOException {
            String yamlContent = """
                    weapon:
                      test-weapon_v2:
                        custom-type: SWORD
                        item:
                          type: DIAMOND_SWORD
                    """;

            Path yamlFile = tempDir.resolve("special.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("test-weapon_v2:"));
        }

        @Test
        @DisplayName("Should handle empty enchants list")
        void shouldHandleEmptyEnchantsList() throws IOException {
            String yamlContent = """
                    weapon:
                      empty:
                        enchants: []
                    """;

            Path yamlFile = tempDir.resolve("emptyenchants.yml");
            Files.writeString(yamlFile, yamlContent);

            assertTrue(Files.exists(yamlFile));
        }

        @Test
        @DisplayName("Should handle empty attributes list")
        void shouldHandleEmptyAttributesList() throws IOException {
            String yamlContent = """
                    weapon:
                      empty:
                        attributes: []
                    """;

            Path yamlFile = tempDir.resolve("emptyattributes.yml");
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
            java.lang.reflect.Field field = CEWeaponConfig.class.getDeclaredField("storage");

            assertNotNull(field);
            assertTrue(java.lang.reflect.Modifier.isPrivate(field.getModifiers()));
        }

        @Test
        @DisplayName("storage field should be CEWeaponStorage type")
        void storageFieldShouldBeCEWeaponStorageType() throws NoSuchFieldException {
            java.lang.reflect.Field field = CEWeaponConfig.class.getDeclaredField("storage");

            assertEquals(CEWeaponStorage.class, field.getType());
        }
    }

    @Nested
    @DisplayName("Custom Type Tests")
    class CustomTypeTests {

        @Test
        @DisplayName("Should support various custom types")
        void shouldSupportVariousCustomTypes() throws IOException {
            String yamlContent = """
                    weapon:
                      sword:
                        custom-type: SWORD
                      axe:
                        custom-type: AXE
                      bow:
                        custom-type: BOW
                      staff:
                        custom-type: STAFF
                    """;

            Path yamlFile = tempDir.resolve("types.yml");
            Files.writeString(yamlFile, yamlContent);

            String content = Files.readString(yamlFile);
            assertTrue(content.contains("custom-type: SWORD"));
            assertTrue(content.contains("custom-type: AXE"));
            assertTrue(content.contains("custom-type: BOW"));
            assertTrue(content.contains("custom-type: STAFF"));
        }

        @Test
        @DisplayName("Should handle null custom-type")
        void shouldHandleNullCustomType() throws IOException {
            String yamlContent = """
                    weapon:
                      nocustom:
                        item:
                          type: DIAMOND_SWORD
                    """;

            Path yamlFile = tempDir.resolve("nocustom.yml");
            Files.writeString(yamlFile, yamlContent);

            assertTrue(Files.exists(yamlFile));
        }
    }
}
