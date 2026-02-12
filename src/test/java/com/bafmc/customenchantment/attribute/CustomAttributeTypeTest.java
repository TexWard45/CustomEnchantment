package com.bafmc.customenchantment.attribute;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CustomAttributeType class.
 * Tests attribute registration, default values, and percent flag.
 */
class CustomAttributeTypeTest {

    @Nested
    @DisplayName("Static attribute definitions")
    class StaticAttributeTests {

        @Test
        @DisplayName("DODGE_CHANCE should be percent type with 0 base value")
        void dodgeChance_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.DODGE_CHANCE.getBaseValue());
            assertTrue(CustomAttributeType.DODGE_CHANCE.isPercent());
        }

        @Test
        @DisplayName("CRITICAL_CHANCE should be percent type with 0 base value")
        void criticalChance_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.CRITICAL_CHANCE.getBaseValue());
            assertTrue(CustomAttributeType.CRITICAL_CHANCE.isPercent());
        }

        @Test
        @DisplayName("CRITICAL_DAMAGE should have base value of 2 and not be percent")
        void criticalDamage_hasCorrectProperties() {
            assertEquals(2, CustomAttributeType.CRITICAL_DAMAGE.getBaseValue());
            assertFalse(CustomAttributeType.CRITICAL_DAMAGE.isPercent());
        }

        @Test
        @DisplayName("LIFE_STEAL should be percent type with 0 base value")
        void lifeSteal_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.LIFE_STEAL.getBaseValue());
            assertTrue(CustomAttributeType.LIFE_STEAL.isPercent());
        }

        @Test
        @DisplayName("ARMOR_PENETRATION should be percent type with 0 base value")
        void armorPenetration_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.ARMOR_PENETRATION.getBaseValue());
            assertTrue(CustomAttributeType.ARMOR_PENETRATION.isPercent());
        }

        @Test
        @DisplayName("DAMAGE_REDUCTION should be percent type with 0 base value")
        void damageReduction_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.DAMAGE_REDUCTION.getBaseValue());
            assertTrue(CustomAttributeType.DAMAGE_REDUCTION.isPercent());
        }

        @Test
        @DisplayName("HEALTH_REGENERATION should not be percent with 0 base value")
        void healthRegeneration_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.HEALTH_REGENERATION.getBaseValue());
            assertFalse(CustomAttributeType.HEALTH_REGENERATION.isPercent());
        }

        @Test
        @DisplayName("HEALTH_REGENERATION_PERCENT should be percent type")
        void healthRegenerationPercent_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.HEALTH_REGENERATION_PERCENT.getBaseValue());
            assertTrue(CustomAttributeType.HEALTH_REGENERATION_PERCENT.isPercent());
        }

        @Test
        @DisplayName("ACCURACY_CHANCE should be percent type")
        void accuracyChance_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.ACCURACY_CHANCE.getBaseValue());
            assertTrue(CustomAttributeType.ACCURACY_CHANCE.isPercent());
        }

        @Test
        @DisplayName("AOE_RANGE should have base value of 1 and not be percent")
        void aoeRange_hasCorrectProperties() {
            assertEquals(1, CustomAttributeType.AOE_RANGE.getBaseValue());
            assertFalse(CustomAttributeType.AOE_RANGE.isPercent());
        }

        @Test
        @DisplayName("AOE_DAMAGE_RATIO should be percent type")
        void aoeDamageRatio_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.AOE_DAMAGE_RATIO.getBaseValue());
            assertTrue(CustomAttributeType.AOE_DAMAGE_RATIO.isPercent());
        }

        @Test
        @DisplayName("VULNERABILITY should be percent type")
        void vulnerability_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.VULNERABILITY.getBaseValue());
            assertTrue(CustomAttributeType.VULNERABILITY.isPercent());
        }

        @Test
        @DisplayName("GRIEVOUS_WOUNDS should be percent type")
        void grievousWounds_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.GRIEVOUS_WOUNDS.getBaseValue());
            assertTrue(CustomAttributeType.GRIEVOUS_WOUNDS.isPercent());
        }

        @Test
        @DisplayName("MINING_POWER should not be percent type")
        void miningPower_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.MINING_POWER.getBaseValue());
            assertFalse(CustomAttributeType.MINING_POWER.isPercent());
        }

        @Test
        @DisplayName("TOTAL_POWER should not be percent type")
        void totalPower_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.TOTAL_POWER.getBaseValue());
            assertFalse(CustomAttributeType.TOTAL_POWER.isPercent());
        }

        @Test
        @DisplayName("ATK_POWER should not be percent type")
        void atkPower_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.ATK_POWER.getBaseValue());
            assertFalse(CustomAttributeType.ATK_POWER.isPercent());
        }

        @Test
        @DisplayName("DEF_POWER should not be percent type")
        void defPower_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.DEF_POWER.getBaseValue());
            assertFalse(CustomAttributeType.DEF_POWER.isPercent());
        }
    }

    @Nested
    @DisplayName("Option attributes")
    class OptionAttributeTests {

        @Test
        @DisplayName("OPTION_ATTACK should have 0 base value")
        void optionAttack_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.OPTION_ATTACK.getBaseValue());
            assertFalse(CustomAttributeType.OPTION_ATTACK.isPercent());
        }

        @Test
        @DisplayName("OPTION_DEFENSE should have 0 base value")
        void optionDefense_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.OPTION_DEFENSE.getBaseValue());
            assertFalse(CustomAttributeType.OPTION_DEFENSE.isPercent());
        }

        @Test
        @DisplayName("OPTION_POWER should have 0 base value")
        void optionPower_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.OPTION_POWER.getBaseValue());
            assertFalse(CustomAttributeType.OPTION_POWER.isPercent());
        }

        @Test
        @DisplayName("OPTION_ARMOR_PENETRATION should have 0 base value")
        void optionArmorPenetration_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.OPTION_ARMOR_PENETRATION.getBaseValue());
            assertFalse(CustomAttributeType.OPTION_ARMOR_PENETRATION.isPercent());
        }
    }

    @Nested
    @DisplayName("Stat attributes")
    class StatAttributeTests {

        @Test
        @DisplayName("STAT_EXP should have 0 base value")
        void statExp_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.STAT_EXP.getBaseValue());
            assertFalse(CustomAttributeType.STAT_EXP.isPercent());
        }

        @Test
        @DisplayName("STAT_FOOD should have 0 base value")
        void statFood_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.STAT_FOOD.getBaseValue());
            assertFalse(CustomAttributeType.STAT_FOOD.isPercent());
        }

        @Test
        @DisplayName("STAT_HEALTH should have 0 base value")
        void statHealth_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.STAT_HEALTH.getBaseValue());
            assertFalse(CustomAttributeType.STAT_HEALTH.isPercent());
        }

        @Test
        @DisplayName("STAT_OXYGEN should have 0 base value")
        void statOxygen_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.STAT_OXYGEN.getBaseValue());
            assertFalse(CustomAttributeType.STAT_OXYGEN.isPercent());
        }

        @Test
        @DisplayName("STAT_ABSORPTION_HEART should have 0 base value")
        void statAbsorptionHeart_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.STAT_ABSORPTION_HEART.getBaseValue());
            assertFalse(CustomAttributeType.STAT_ABSORPTION_HEART.isPercent());
        }
    }

    @Nested
    @DisplayName("Resistance attributes")
    class ResistanceAttributeTests {

        @Test
        @DisplayName("SLOW_RESISTANCE should be percent type")
        void slowResistance_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.SLOW_RESISTANCE.getBaseValue());
            assertTrue(CustomAttributeType.SLOW_RESISTANCE.isPercent());
        }

        @Test
        @DisplayName("MAGIC_RESISTANCE should be percent type")
        void magicResistance_hasCorrectProperties() {
            assertEquals(0, CustomAttributeType.MAGIC_RESISTANCE.getBaseValue());
            assertTrue(CustomAttributeType.MAGIC_RESISTANCE.isPercent());
        }
    }

    @Nested
    @DisplayName("values() array")
    class ValuesArrayTests {

        @Test
        @DisplayName("values() should not be null")
        void values_isNotNull() {
            assertNotNull(CustomAttributeType.getValues());
        }

        @Test
        @DisplayName("values() should contain all registered attributes")
        void values_containsAllAttributes() {
            CustomAttributeType[] values = CustomAttributeType.getValues();

            assertTrue(values.length >= 24, "Should have at least 24 attribute types");
        }

        @Test
        @DisplayName("All values should have valid type name")
        void allValues_haveValidTypeName() {
            for (CustomAttributeType type : CustomAttributeType.getValues()) {
                assertNotNull(type.getType(), "Type name should not be null");
                assertFalse(type.getType().isEmpty(), "Type name should not be empty");
            }
        }
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("Constructor without percent flag defaults to false")
        void constructor_withoutPercent_defaultsToFalse() {
            // This is tested implicitly through the static attributes
            // CRITICAL_DAMAGE uses 3-arg constructor
            assertFalse(CustomAttributeType.CRITICAL_DAMAGE.isPercent());
        }

        @Test
        @DisplayName("Constructor with percent flag sets correctly")
        void constructor_withPercent_setsFlag() {
            // DODGE_CHANCE uses 4-arg constructor with percent=true
            assertTrue(CustomAttributeType.DODGE_CHANCE.isPercent());
        }
    }

    @Nested
    @DisplayName("init() method")
    class InitMethodTests {

        @Test
        @DisplayName("init() should not throw exception")
        void init_doesNotThrow() {
            assertDoesNotThrow(() -> CustomAttributeType.init());
        }
    }
}
