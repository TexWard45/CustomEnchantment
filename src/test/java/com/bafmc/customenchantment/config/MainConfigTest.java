package com.bafmc.customenchantment.config;

import com.bafmc.bukkit.utils.EquipSlot;
import com.bafmc.customenchantment.config.data.ExtraSlotSettingsData;
import com.bafmc.customenchantment.item.CEWeaponType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MainConfig class - main plugin configuration.
 */
@DisplayName("MainConfig Tests")
class MainConfigTest {

    private MainConfig config;

    @BeforeEach
    void setUp() {
        config = new MainConfig();
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have default move event period")
        void shouldHaveDefaultMoveEventPeriod() {
            assertEquals(1000L, config.getMoveEventPeriod());
        }

        @Test
        @DisplayName("Should have default sneak event period")
        void shouldHaveDefaultSneakEventPeriod() {
            assertEquals(250L, config.getSneakEventPeriod());
        }

        @Test
        @DisplayName("Should have default max extra slot use count")
        void shouldHaveDefaultMaxExtraSlotUseCount() {
            assertEquals(3, config.getMaxExtraSlotUseCount());
        }

        @Test
        @DisplayName("Should have default combat time")
        void shouldHaveDefaultCombatTime() {
            assertEquals(10000, config.getCombatTime());
        }

        @Test
        @DisplayName("Should have default combat staff min required attack strength scale")
        void shouldHaveDefaultCombatStaffMinRequiredAttackStrengthScale() {
            assertEquals(0.5, config.getCombatStaffMinRequiredAttackStrengthScale());
        }

        @Test
        @DisplayName("Should have default combat settings require weapon")
        void shouldHaveDefaultCombatSettingsRequireWeapon() {
            assertTrue(config.isCombatSettingsRequireWeapon());
        }

        @Test
        @DisplayName("Should have default unbreakable armor enable")
        void shouldHaveDefaultUnbreakableArmorEnable() {
            assertTrue(config.isUnbreakableArmorEnable());
        }

        @Test
        @DisplayName("Should have default unbreakable armor player per tick")
        void shouldHaveDefaultUnbreakableArmorPlayerPerTick() {
            assertEquals(1, config.getUnbreakableArmorPlayerPerTick());
        }

        @Test
        @DisplayName("Should have default unbreakable armor tick interval")
        void shouldHaveDefaultUnbreakableArmorTickInterval() {
            assertEquals(20, config.getUnbreakableArmorTickInterval());
        }

        @Test
        @DisplayName("Should have default sigil extra slot")
        void shouldHaveDefaultSigilExtraSlot() {
            assertEquals(EquipSlot.EXTRA_SLOT_8, config.getSigilExtraSlot());
        }

        @Test
        @DisplayName("Should have default outfit extra slot")
        void shouldHaveDefaultOutfitExtraSlot() {
            assertEquals(EquipSlot.EXTRA_SLOT_9, config.getOutfitExtraSlot());
        }

        @Test
        @DisplayName("Should have default outfit task per player")
        void shouldHaveDefaultOutfitTaskPerPlayer() {
            assertEquals(5, config.getOutfitTaskPerPlayer());
        }

        @Test
        @DisplayName("Should have default outfit task per item")
        void shouldHaveDefaultOutfitTaskPerItem() {
            assertEquals(10, config.getOutfitTaskPerItem());
        }

        @Test
        @DisplayName("Should have default staff color")
        void shouldHaveDefaultStaffColor() {
            assertEquals("#FFCC00", config.getStaffColor());
        }

        @Test
        @DisplayName("Should have default staff speed")
        void shouldHaveDefaultStaffSpeed() {
            assertEquals(1.6, config.getStaffSpeed());
        }

        @Test
        @DisplayName("Should have default staff step size")
        void shouldHaveDefaultStaffStepSize() {
            assertEquals(0.75, config.getStaffStepSize());
        }

        @Test
        @DisplayName("Should have default sigil display enable as false")
        void shouldHaveDefaultSigilDisplayEnableAsFalse() {
            assertFalse(config.isSigilDisplayEnable());
        }

        @Test
        @DisplayName("Should have empty effect type blacklist by default")
        void shouldHaveEmptyEffectTypeBlacklistByDefault() {
            assertNotNull(config.getEffectTypeBlacklist());
            assertTrue(config.getEffectTypeBlacklist().isEmpty());
        }

        @Test
        @DisplayName("Should have empty enchant disable worlds by default")
        void shouldHaveEmptyEnchantDisableWorldsByDefault() {
            assertNotNull(config.getEnchantDisableWorlds());
            assertTrue(config.getEnchantDisableWorlds().isEmpty());
        }

        @Test
        @DisplayName("Should have empty outfit title update blacklist by default")
        void shouldHaveEmptyOutfitTitleUpdateBlacklistByDefault() {
            assertNotNull(config.getOutfitTitleUpdateBlacklist());
            assertTrue(config.getOutfitTitleUpdateBlacklist().isEmpty());
        }
    }

    @Nested
    @DisplayName("isEnchantDisableLocation Tests")
    class IsEnchantDisableLocationTests {

        @Test
        @DisplayName("Should return false when enchant disable worlds is empty")
        void shouldReturnFalseWhenEnchantDisableWorldsIsEmpty() {
            assertFalse(config.isEnchantDisableLocation("world"));
        }

        @Test
        @DisplayName("Should return true for disabled world string")
        void shouldReturnTrueForDisabledWorldString() throws Exception {
            setPrivateField(config, "enchantDisableWorlds", Arrays.asList("disabled_world", "another_disabled"));

            assertTrue(config.isEnchantDisableLocation("disabled_world"));
            assertTrue(config.isEnchantDisableLocation("another_disabled"));
        }

        @Test
        @DisplayName("Should return false for non-disabled world string")
        void shouldReturnFalseForNonDisabledWorldString() throws Exception {
            setPrivateField(config, "enchantDisableWorlds", Arrays.asList("disabled_world"));

            assertFalse(config.isEnchantDisableLocation("enabled_world"));
        }

        @Test
        @DisplayName("Should be case sensitive for world names")
        void shouldBeCaseSensitiveForWorldNames() throws Exception {
            setPrivateField(config, "enchantDisableWorlds", Arrays.asList("World"));

            assertTrue(config.isEnchantDisableLocation("World"));
            assertFalse(config.isEnchantDisableLocation("world"));
            assertFalse(config.isEnchantDisableLocation("WORLD"));
        }
    }

    @Nested
    @DisplayName("getExtraSlotSettings Tests")
    class GetExtraSlotSettingsTests {

        @Test
        @DisplayName("Should return null for null CEItem")
        void shouldReturnNullForNullCEItem() {
            ExtraSlotSettingsData result = config.getExtraSlotSettings(null);

            assertNull(result);
        }

        @Test
        @DisplayName("Should return empty map by default")
        void shouldReturnEmptyMapByDefault() {
            Map<String, ExtraSlotSettingsData> result = config.getExtraSlotSettingMap();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return defensive copy of extra slot setting map")
        void shouldReturnDefensiveCopyOfExtraSlotSettingMap() throws Exception {
            Map<String, ExtraSlotSettingsData> originalMap = new LinkedHashMap<>();
            setPrivateField(config, "extraSlotSettingMap", originalMap);

            Map<String, ExtraSlotSettingsData> result = config.getExtraSlotSettingMap();

            assertNotSame(originalMap, result);
        }
    }

    @Nested
    @DisplayName("Annotation Tests")
    class AnnotationTests {

        @Test
        @DisplayName("Should have Configuration annotation")
        void shouldHaveConfigurationAnnotation() {
            assertNotNull(MainConfig.class.getAnnotation(
                    com.bafmc.bukkit.config.annotation.Configuration.class));
        }

        @Test
        @DisplayName("Fields should have Path annotations")
        void fieldsShouldHavePathAnnotations() {
            Field[] fields = MainConfig.class.getDeclaredFields();
            int pathAnnotatedFields = 0;

            for (Field field : fields) {
                if (field.getAnnotation(com.bafmc.bukkit.config.annotation.Path.class) != null) {
                    pathAnnotatedFields++;
                }
            }

            assertTrue(pathAnnotatedFields > 0, "Should have at least one @Path annotated field");
        }

        @Test
        @DisplayName("weaponIconMap field should have KeyType annotation")
        void weaponIconMapFieldShouldHaveKeyTypeAnnotation() throws Exception {
            Field field = MainConfig.class.getDeclaredField("weaponIconMap");

            assertNotNull(field.getAnnotation(com.bafmc.bukkit.config.annotation.KeyType.class));
        }

        @Test
        @DisplayName("extraSlotSettingMap field should have ValueType annotation")
        void extraSlotSettingMapFieldShouldHaveValueTypeAnnotation() throws Exception {
            Field field = MainConfig.class.getDeclaredField("extraSlotSettingMap");

            assertNotNull(field.getAnnotation(com.bafmc.bukkit.config.annotation.ValueType.class));
        }
    }

    @Nested
    @DisplayName("Collection Initialization Tests")
    class CollectionInitializationTests {

        @Test
        @DisplayName("Material group string list should be initialized")
        void materialGroupStringListShouldBeInitialized() throws Exception {
            Field field = MainConfig.class.getDeclaredField("materialGroupStringList");
            field.setAccessible(true);
            Object value = field.get(config);

            assertNotNull(value);
            assertTrue(value instanceof Map);
        }

        @Test
        @DisplayName("Entity group string list should be initialized")
        void entityGroupStringListShouldBeInitialized() throws Exception {
            Field field = MainConfig.class.getDeclaredField("entityGroupStringList");
            field.setAccessible(true);
            Object value = field.get(config);

            assertNotNull(value);
            assertTrue(value instanceof Map);
        }

        @Test
        @DisplayName("CE item material whitelist should be initialized")
        void ceItemMaterialWhitelistShouldBeInitialized() throws Exception {
            Field field = MainConfig.class.getDeclaredField("ceItemMaterialWhitelist");
            field.setAccessible(true);
            Object value = field.get(config);

            assertNotNull(value);
            assertTrue(value instanceof List);
        }

        @Test
        @DisplayName("Weapon icon map should be initialized")
        void weaponIconMapShouldBeInitialized() throws Exception {
            Field field = MainConfig.class.getDeclaredField("weaponIconMap");
            field.setAccessible(true);
            Object value = field.get(config);

            assertNotNull(value);
            assertTrue(value instanceof Map);
        }
    }

    @Nested
    @DisplayName("IConfigurationLoader Implementation Tests")
    class IConfigurationLoaderImplementationTests {

        @Test
        @DisplayName("Should implement IConfigurationLoader interface")
        void shouldImplementIConfigurationLoaderInterface() {
            assertTrue(config instanceof com.bafmc.bukkit.config.IConfigurationLoader);
        }
    }

    // Helper method to set private fields via reflection
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
