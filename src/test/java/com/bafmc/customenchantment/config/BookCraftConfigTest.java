package com.bafmc.customenchantment.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BookCraftConfig class - configuration for book crafting.
 */
@DisplayName("BookCraftConfig Tests")
class BookCraftConfigTest {

    private BookCraftConfig config;

    @BeforeEach
    void setUp() {
        config = new BookCraftConfig();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            BookCraftConfig newConfig = new BookCraftConfig();

            assertNotNull(newConfig);
        }
    }

    @Nested
    @DisplayName("Annotation Tests")
    class AnnotationTests {

        @Test
        @DisplayName("Should have Configuration annotation")
        void shouldHaveConfigurationAnnotation() {
            assertNotNull(BookCraftConfig.class.getAnnotation(
                    com.bafmc.bukkit.config.annotation.Configuration.class));
        }

        @Test
        @DisplayName("Should have Getter annotation from Lombok")
        void shouldHaveGetterAnnotationFromLombok() {
            // Lombok @Getter has SOURCE retention, so verify by checking generated getter methods exist
            assertDoesNotThrow(() -> BookCraftConfig.class.getMethod("getMoneyGroupRequireMap"),
                    "Lombok @Getter should generate getMoneyGroupRequireMap method");
        }

        @Test
        @DisplayName("moneyGroupRequireMap field should have Path annotation")
        void moneyGroupRequireMapFieldShouldHavePathAnnotation() throws NoSuchFieldException {
            Field field = BookCraftConfig.class.getDeclaredField("moneyGroupRequireMap");

            assertNotNull(field.getAnnotation(com.bafmc.bukkit.config.annotation.Path.class));
        }

        @Test
        @DisplayName("moneyGroupRequireMap field should have ValueType annotation")
        void moneyGroupRequireMapFieldShouldHaveValueTypeAnnotation() throws NoSuchFieldException {
            Field field = BookCraftConfig.class.getDeclaredField("moneyGroupRequireMap");

            assertNotNull(field.getAnnotation(com.bafmc.bukkit.config.annotation.ValueType.class));
        }
    }

    @Nested
    @DisplayName("getMoneyRequire Tests")
    class GetMoneyRequireTests {

        @Test
        @DisplayName("Should return 0 for non-existent group")
        void shouldReturnZeroForNonExistentGroup() {
            double result = config.getMoneyRequire("non-existent-group");

            assertEquals(0.0, result);
        }

        @Test
        @DisplayName("Should return configured money for existing group")
        void shouldReturnConfiguredMoneyForExistingGroup() throws Exception {
            Map<String, Double> moneyMap = new LinkedHashMap<>();
            moneyMap.put("common", 100.0);
            moneyMap.put("rare", 500.0);
            moneyMap.put("legendary", 1000.0);
            setPrivateField(config, "moneyGroupRequireMap", moneyMap);

            assertEquals(100.0, config.getMoneyRequire("common"));
            assertEquals(500.0, config.getMoneyRequire("rare"));
            assertEquals(1000.0, config.getMoneyRequire("legendary"));
        }

        @Test
        @DisplayName("Should be case sensitive for group names")
        void shouldBeCaseSensitiveForGroupNames() throws Exception {
            Map<String, Double> moneyMap = new LinkedHashMap<>();
            moneyMap.put("Common", 100.0);
            setPrivateField(config, "moneyGroupRequireMap", moneyMap);

            assertEquals(100.0, config.getMoneyRequire("Common"));
            assertEquals(0.0, config.getMoneyRequire("common"));
            assertEquals(0.0, config.getMoneyRequire("COMMON"));
        }

        @Test
        @DisplayName("Should handle zero money requirement")
        void shouldHandleZeroMoneyRequirement() throws Exception {
            Map<String, Double> moneyMap = new LinkedHashMap<>();
            moneyMap.put("free", 0.0);
            setPrivateField(config, "moneyGroupRequireMap", moneyMap);

            assertEquals(0.0, config.getMoneyRequire("free"));
        }

        @Test
        @DisplayName("Should handle decimal money values")
        void shouldHandleDecimalMoneyValues() throws Exception {
            Map<String, Double> moneyMap = new LinkedHashMap<>();
            moneyMap.put("fractional", 99.99);
            setPrivateField(config, "moneyGroupRequireMap", moneyMap);

            assertEquals(99.99, config.getMoneyRequire("fractional"));
        }
    }

    @Nested
    @DisplayName("getMoneyGroupRequireMap Tests")
    class GetMoneyGroupRequireMapTests {

        @Test
        @DisplayName("Should return empty map by default")
        void shouldReturnEmptyMapByDefault() {
            Map<String, Double> result = config.getMoneyGroupRequireMap();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return configured map")
        void shouldReturnConfiguredMap() throws Exception {
            Map<String, Double> moneyMap = new LinkedHashMap<>();
            moneyMap.put("test1", 100.0);
            moneyMap.put("test2", 200.0);
            setPrivateField(config, "moneyGroupRequireMap", moneyMap);

            Map<String, Double> result = config.getMoneyGroupRequireMap();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(100.0, result.get("test1"));
            assertEquals(200.0, result.get("test2"));
        }
    }

    @Nested
    @DisplayName("Method Existence Tests")
    class MethodExistenceTests {

        @Test
        @DisplayName("Should have getMoneyRequire method")
        void shouldHaveGetMoneyRequireMethod() throws NoSuchMethodException {
            config.getClass().getMethod("getMoneyRequire", String.class);
        }

        @Test
        @DisplayName("Should have isRequireMoney method for player and group")
        void shouldHaveIsRequireMoneyMethodForPlayerAndGroup() throws NoSuchMethodException {
            config.getClass().getMethod("isRequireMoney",
                    org.bukkit.entity.Player.class, String.class);
        }

        @Test
        @DisplayName("Should have isRequireMoney method with amount")
        void shouldHaveIsRequireMoneyMethodWithAmount() throws NoSuchMethodException {
            config.getClass().getMethod("isRequireMoney",
                    org.bukkit.entity.Player.class, String.class, double.class);
        }

        @Test
        @DisplayName("Should have payMoney method for player and group")
        void shouldHavePayMoneyMethodForPlayerAndGroup() throws NoSuchMethodException {
            config.getClass().getMethod("payMoney",
                    org.bukkit.entity.Player.class, String.class);
        }

        @Test
        @DisplayName("Should have payMoney method with amount")
        void shouldHavePayMoneyMethodWithAmount() throws NoSuchMethodException {
            config.getClass().getMethod("payMoney",
                    org.bukkit.entity.Player.class, String.class, double.class);
        }

        @Test
        @DisplayName("Should have getMoneyGroupRequireMap method")
        void shouldHaveGetMoneyGroupRequireMapMethod() throws NoSuchMethodException {
            config.getClass().getMethod("getMoneyGroupRequireMap");
        }
    }

    @Nested
    @DisplayName("Field Type Tests")
    class FieldTypeTests {

        @Test
        @DisplayName("moneyGroupRequireMap should be Map type")
        void moneyGroupRequireMapShouldBeMapType() throws NoSuchFieldException {
            Field field = BookCraftConfig.class.getDeclaredField("moneyGroupRequireMap");

            assertEquals(Map.class, field.getType());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("moneyGroupRequireMap should be initialized")
        void moneyGroupRequireMapShouldBeInitialized() throws Exception {
            Field field = BookCraftConfig.class.getDeclaredField("moneyGroupRequireMap");
            field.setAccessible(true);
            Object value = field.get(config);

            assertNotNull(value);
            assertTrue(value instanceof Map);
        }

        @Test
        @DisplayName("moneyGroupRequireMap should be LinkedHashMap")
        void moneyGroupRequireMapShouldBeLinkedHashMap() throws Exception {
            Field field = BookCraftConfig.class.getDeclaredField("moneyGroupRequireMap");
            field.setAccessible(true);
            Object value = field.get(config);

            assertTrue(value instanceof LinkedHashMap);
        }
    }

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("Should be in correct package")
        void shouldBeInCorrectPackage() {
            assertEquals("com.bafmc.customenchantment.config",
                    BookCraftConfig.class.getPackageName());
        }

        @Test
        @DisplayName("Should not extend AbstractConfig")
        void shouldNotExtendAbstractConfig() {
            assertFalse(AbstractConfig.class.isAssignableFrom(BookCraftConfig.class));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle large money values")
        void shouldHandleLargeMoneyValues() throws Exception {
            Map<String, Double> moneyMap = new LinkedHashMap<>();
            moneyMap.put("expensive", 999999999.99);
            setPrivateField(config, "moneyGroupRequireMap", moneyMap);

            assertEquals(999999999.99, config.getMoneyRequire("expensive"));
        }

        @Test
        @DisplayName("Should handle negative money values")
        void shouldHandleNegativeMoneyValues() throws Exception {
            Map<String, Double> moneyMap = new LinkedHashMap<>();
            moneyMap.put("negative", -100.0);
            setPrivateField(config, "moneyGroupRequireMap", moneyMap);

            assertEquals(-100.0, config.getMoneyRequire("negative"));
        }

        @Test
        @DisplayName("Should handle empty string group name")
        void shouldHandleEmptyStringGroupName() {
            double result = config.getMoneyRequire("");

            assertEquals(0.0, result);
        }

        @Test
        @DisplayName("Should handle group name with spaces")
        void shouldHandleGroupNameWithSpaces() throws Exception {
            Map<String, Double> moneyMap = new LinkedHashMap<>();
            moneyMap.put("group with spaces", 50.0);
            setPrivateField(config, "moneyGroupRequireMap", moneyMap);

            assertEquals(50.0, config.getMoneyRequire("group with spaces"));
        }
    }

    // Helper method to set private fields via reflection
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
