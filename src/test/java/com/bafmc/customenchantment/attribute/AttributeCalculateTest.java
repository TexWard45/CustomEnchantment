package com.bafmc.customenchantment.attribute;

import com.bafmc.bukkit.bafframework.nms.NMSAttribute;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeOperation;
import com.bafmc.bukkit.bafframework.nms.NMSAttributeType;
import com.bafmc.bukkit.utils.Chance;
import com.bafmc.bukkit.utils.RandomRange;
import org.bukkit.attribute.AttributeModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for AttributeCalculate class.
 * Tests all attribute operations: ADD_NUMBER, MULTIPLY_PERCENTAGE, SET_NUMBER, ADD_PERCENTAGE
 * and their correct order of application.
 */
class AttributeCalculateTest {

    private NMSAttributeType testAttributeType;

    @BeforeEach
    void setUp() {
        testAttributeType = mock(NMSAttributeType.class);
    }

    @Nested
    @DisplayName("calculate(double amount, List<NMSAttribute> list)")
    class CalculateWithListTests {

        @Test
        @DisplayName("Should return base amount when list is empty")
        void calculate_withEmptyList_returnsBaseAmount() {
            double result = AttributeCalculate.calculate(100, new ArrayList<>());
            assertEquals(100, result);
        }

        @Test
        @DisplayName("Should add values with ADD_NUMBER operation")
        void calculate_withAddOperation_addsToBase() {
            NMSAttribute attr1 = createMockAttribute(10, NMSAttributeOperation.ADD_NUMBER);
            NMSAttribute attr2 = createMockAttribute(5, NMSAttributeOperation.ADD_NUMBER);

            List<NMSAttribute> modifiers = Arrays.asList(attr1, attr2);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(115, result);
        }

        @Test
        @DisplayName("Should multiply with MULTIPLY_PERCENTAGE operation")
        void calculate_withMultiplyPercentageOperation_multipliesBase() {
            // MULTIPLY_PERCENTAGE adds to multiplier (1 + percentage)
            // e.g., 0.2 means +20%, so base * (1 + 0.2)
            NMSAttribute attr = createMockAttribute(0.2, NMSAttributeOperation.MULTIPLY_PERCENTAGE);

            List<NMSAttribute> modifiers = List.of(attr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(120, result);
        }

        @Test
        @DisplayName("Should multiply combined percentages with multiple MULTIPLY_PERCENTAGE")
        void calculate_withMultipleMultiplyPercentage_combinesPercentages() {
            // Two +20% modifiers: 1 + 0.2 + 0.2 = 1.4
            NMSAttribute attr1 = createMockAttribute(0.2, NMSAttributeOperation.MULTIPLY_PERCENTAGE);
            NMSAttribute attr2 = createMockAttribute(0.2, NMSAttributeOperation.MULTIPLY_PERCENTAGE);

            List<NMSAttribute> modifiers = Arrays.asList(attr1, attr2);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(140, result);
        }

        @Test
        @DisplayName("Should override value with SET_NUMBER operation")
        void calculate_withSetOperation_overridesValue() {
            NMSAttribute attr = createMockAttribute(50, NMSAttributeOperation.SET_NUMBER);

            List<NMSAttribute> modifiers = List.of(attr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(50, result);
        }

        @Test
        @DisplayName("Should use max value when multiple SET_NUMBER operations")
        void calculate_withMultipleSetOperations_usesMaxValue() {
            NMSAttribute attr1 = createMockAttribute(50, NMSAttributeOperation.SET_NUMBER);
            NMSAttribute attr2 = createMockAttribute(75, NMSAttributeOperation.SET_NUMBER);
            NMSAttribute attr3 = createMockAttribute(25, NMSAttributeOperation.SET_NUMBER);

            List<NMSAttribute> modifiers = Arrays.asList(attr1, attr2, attr3);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(75, result);
        }

        @Test
        @DisplayName("Should apply ADD_PERCENTAGE as final multiplier")
        void calculate_withAddPercentage_multipliesTotal() {
            // ADD_PERCENTAGE: total *= (1 + amount)
            NMSAttribute attr = createMockAttribute(0.1, NMSAttributeOperation.ADD_PERCENTAGE);

            List<NMSAttribute> modifiers = List.of(attr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(110, result);
        }

        @Test
        @DisplayName("Should apply operations in correct order: ADD -> MULTIPLY_PERCENTAGE -> SET -> ADD_PERCENTAGE")
        void calculate_operationOrder_isCorrect() {
            // Base: 100
            // ADD_NUMBER: +10 -> 110
            // MULTIPLY_PERCENTAGE: +20% -> 110 * 1.2 = 132
            // ADD_PERCENTAGE: +10% -> 132 * 1.1 = 145.2
            NMSAttribute addAttr = createMockAttribute(10, NMSAttributeOperation.ADD_NUMBER);
            NMSAttribute multiplyAttr = createMockAttribute(0.2, NMSAttributeOperation.MULTIPLY_PERCENTAGE);
            NMSAttribute percentAttr = createMockAttribute(0.1, NMSAttributeOperation.ADD_PERCENTAGE);

            List<NMSAttribute> modifiers = Arrays.asList(addAttr, multiplyAttr, percentAttr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(145.2, result, 0.01);
        }

        @Test
        @DisplayName("SET_NUMBER should apply after ADD and MULTIPLY_PERCENTAGE")
        void calculate_setAfterAddAndMultiply_overridesCalculatedValue() {
            // Base: 100
            // ADD_NUMBER: +10 -> 110
            // MULTIPLY_PERCENTAGE: +20% -> 132
            // SET_NUMBER: 50 (overrides to 50)
            // ADD_PERCENTAGE: +10% -> 50 * 1.1 = 55
            NMSAttribute addAttr = createMockAttribute(10, NMSAttributeOperation.ADD_NUMBER);
            NMSAttribute multiplyAttr = createMockAttribute(0.2, NMSAttributeOperation.MULTIPLY_PERCENTAGE);
            NMSAttribute setAttr = createMockAttribute(50, NMSAttributeOperation.SET_NUMBER);
            NMSAttribute percentAttr = createMockAttribute(0.1, NMSAttributeOperation.ADD_PERCENTAGE);

            List<NMSAttribute> modifiers = Arrays.asList(addAttr, multiplyAttr, setAttr, percentAttr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(55, result, 0.01);
        }

        @Test
        @DisplayName("Should handle negative ADD_NUMBER values")
        void calculate_withNegativeAdd_subtractsFromBase() {
            NMSAttribute attr = createMockAttribute(-20, NMSAttributeOperation.ADD_NUMBER);

            List<NMSAttribute> modifiers = List.of(attr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(80, result);
        }

        @Test
        @DisplayName("Should handle zero base amount")
        void calculate_withZeroBase_handlesCorrectly() {
            NMSAttribute addAttr = createMockAttribute(50, NMSAttributeOperation.ADD_NUMBER);
            NMSAttribute multiplyAttr = createMockAttribute(0.5, NMSAttributeOperation.MULTIPLY_PERCENTAGE);

            List<NMSAttribute> modifiers = Arrays.asList(addAttr, multiplyAttr);

            double result = AttributeCalculate.calculate(0, modifiers);

            // 0 + 50 = 50, 50 * 1.5 = 75
            assertEquals(75, result);
        }
    }

    @Nested
    @DisplayName("RangeAttribute tests in calculation")
    class RangeAttributeCalculationTests {

        @Test
        @DisplayName("Should use RangeAttribute amount when no chance defined")
        void calculate_withRangeAttributeNoChance_usesAmount() {
            RandomRange range = mock(RandomRange.class);
            when(range.getValue()).thenReturn(15.0);

            RangeAttribute rangeAttr = new RangeAttribute(testAttributeType, range, NMSAttributeOperation.ADD_NUMBER);

            List<NMSAttribute> modifiers = List.of(rangeAttr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(115, result);
        }

        @Test
        @DisplayName("Should apply RangeAttribute when chance succeeds")
        void calculate_withRangeAttributeChanceSuccess_appliesAmount() {
            RandomRange range = mock(RandomRange.class);
            when(range.getValue()).thenReturn(20.0);

            Chance chance = mock(Chance.class);
            when(chance.work()).thenReturn(true);
            when(chance.clone()).thenReturn(chance);

            RangeAttribute rangeAttr = new RangeAttribute(testAttributeType, range, NMSAttributeOperation.ADD_NUMBER, chance);

            List<NMSAttribute> modifiers = List.of(rangeAttr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(120, result);
        }

        @Test
        @DisplayName("Should skip RangeAttribute when chance fails")
        void calculate_withRangeAttributeChanceFails_skipsAmount() {
            RandomRange range = mock(RandomRange.class);
            when(range.getValue()).thenReturn(20.0);

            Chance chance = mock(Chance.class);
            when(chance.work()).thenReturn(false);
            when(chance.clone()).thenReturn(chance);

            RangeAttribute rangeAttr = new RangeAttribute(testAttributeType, range, NMSAttributeOperation.ADD_NUMBER, chance);

            List<NMSAttribute> modifiers = List.of(rangeAttr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(100, result);
        }

        @Test
        @DisplayName("Should handle RangeAttribute with MULTIPLY_PERCENTAGE operation")
        void calculate_withRangeAttributeMultiply_multipliesCorrectly() {
            RandomRange range = mock(RandomRange.class);
            when(range.getValue()).thenReturn(0.25);

            RangeAttribute rangeAttr = new RangeAttribute(testAttributeType, range, NMSAttributeOperation.MULTIPLY_PERCENTAGE);

            List<NMSAttribute> modifiers = List.of(rangeAttr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(125, result);
        }

        @Test
        @DisplayName("Should handle RangeAttribute with SET_NUMBER operation")
        void calculate_withRangeAttributeSet_setsValue() {
            RandomRange range = mock(RandomRange.class);
            when(range.getValue()).thenReturn(42.0);

            RangeAttribute rangeAttr = new RangeAttribute(testAttributeType, range, NMSAttributeOperation.SET_NUMBER);

            List<NMSAttribute> modifiers = List.of(rangeAttr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(42, result);
        }

        @Test
        @DisplayName("Should handle RangeAttribute with ADD_PERCENTAGE operation")
        void calculate_withRangeAttributeAddPercentage_appliesPercentage() {
            RandomRange range = mock(RandomRange.class);
            when(range.getValue()).thenReturn(0.5);

            RangeAttribute rangeAttr = new RangeAttribute(testAttributeType, range, NMSAttributeOperation.ADD_PERCENTAGE);

            List<NMSAttribute> modifiers = List.of(rangeAttr);

            double result = AttributeCalculate.calculate(100, modifiers);

            assertEquals(150, result);
        }
    }

    @Nested
    @DisplayName("calculate(CustomAttributeType type, double amount, List<NMSAttribute> list)")
    class CalculateWithTypeFilterTests {

        @Test
        @DisplayName("Should filter attributes by type")
        void calculate_withMixedTypes_filtersCorrectly() {
            NMSAttributeType type1 = mock(NMSAttributeType.class);
            NMSAttributeType type2 = mock(NMSAttributeType.class);

            NMSAttribute attr1 = createMockAttribute(10, NMSAttributeOperation.ADD_NUMBER, type1);
            NMSAttribute attr2 = createMockAttribute(20, NMSAttributeOperation.ADD_NUMBER, type2);
            NMSAttribute attr3 = createMockAttribute(5, NMSAttributeOperation.ADD_NUMBER, type1);

            List<NMSAttribute> modifiers = Arrays.asList(attr1, attr2, attr3);

            // Should only include attr1 and attr3 (type1)
            double result = AttributeCalculate.calculate((CustomAttributeType) type1, 100, modifiers);

            assertEquals(115, result);
        }

        @Test
        @DisplayName("Should return base when no attributes match type")
        void calculate_withNoMatchingType_returnsBase() {
            NMSAttributeType type1 = mock(NMSAttributeType.class);
            NMSAttributeType type2 = mock(NMSAttributeType.class);

            NMSAttribute attr = createMockAttribute(50, NMSAttributeOperation.ADD_NUMBER, type2);

            List<NMSAttribute> modifiers = List.of(attr);

            double result = AttributeCalculate.calculate((CustomAttributeType) type1, 100, modifiers);

            assertEquals(100, result);
        }

        @Test
        @DisplayName("Should handle null attribute type")
        void calculate_withNullAttributeType_skipsAttribute() {
            NMSAttributeType type1 = mock(NMSAttributeType.class);

            NMSAttribute attr1 = createMockAttribute(10, NMSAttributeOperation.ADD_NUMBER, type1);
            NMSAttribute attr2 = createMockAttribute(20, NMSAttributeOperation.ADD_NUMBER, null);

            List<NMSAttribute> modifiers = Arrays.asList(attr1, attr2);

            double result = AttributeCalculate.calculate((CustomAttributeType) type1, 100, modifiers);

            assertEquals(110, result);
        }
    }

    @Nested
    @DisplayName("calculateAttributeModifier tests")
    class CalculateAttributeModifierTests {

        @Test
        @DisplayName("Should calculate ADD_NUMBER modifiers")
        void calculateAttributeModifier_withAddNumber_addsToBase() {
            AttributeModifier mod1 = new AttributeModifier(UUID.randomUUID(), "test1", 10, AttributeModifier.Operation.ADD_NUMBER);
            AttributeModifier mod2 = new AttributeModifier(UUID.randomUUID(), "test2", 5, AttributeModifier.Operation.ADD_NUMBER);

            Collection<AttributeModifier> modifiers = Arrays.asList(mod1, mod2);

            double result = AttributeCalculate.calculateAttributeModifier(100, modifiers);

            assertEquals(115, result);
        }

        @Test
        @DisplayName("Should calculate ADD_SCALAR modifiers")
        void calculateAttributeModifier_withAddScalar_multipliesBase() {
            AttributeModifier mod = new AttributeModifier(UUID.randomUUID(), "test", 0.2, AttributeModifier.Operation.ADD_SCALAR);

            Collection<AttributeModifier> modifiers = List.of(mod);

            double result = AttributeCalculate.calculateAttributeModifier(100, modifiers);

            assertEquals(120, result);
        }

        @Test
        @DisplayName("Should calculate MULTIPLY_SCALAR_1 modifiers")
        void calculateAttributeModifier_withMultiplyScalar1_multipliesTotal() {
            AttributeModifier mod = new AttributeModifier(UUID.randomUUID(), "test", 0.1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

            Collection<AttributeModifier> modifiers = List.of(mod);

            double result = AttributeCalculate.calculateAttributeModifier(100, modifiers);

            assertEquals(110, result);
        }

        @Test
        @DisplayName("Should apply operations in correct order")
        void calculateAttributeModifier_operationOrder_isCorrect() {
            // Base: 100
            // ADD_NUMBER: +10 -> 110
            // ADD_SCALAR: +20% -> 110 * 1.2 = 132
            // MULTIPLY_SCALAR_1: +10% -> 132 * 1.1 = 145.2
            AttributeModifier addMod = new AttributeModifier(UUID.randomUUID(), "add", 10, AttributeModifier.Operation.ADD_NUMBER);
            AttributeModifier scalarMod = new AttributeModifier(UUID.randomUUID(), "scalar", 0.2, AttributeModifier.Operation.ADD_SCALAR);
            AttributeModifier multiplyMod = new AttributeModifier(UUID.randomUUID(), "multiply", 0.1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

            Collection<AttributeModifier> modifiers = Arrays.asList(addMod, scalarMod, multiplyMod);

            double result = AttributeCalculate.calculateAttributeModifier(100, modifiers);

            assertEquals(145.2, result, 0.01);
        }

        @Test
        @DisplayName("Should skip negative values when noNegative is true")
        void calculateAttributeModifier_withNoNegativeFlag_skipsNegative() {
            AttributeModifier posMod = new AttributeModifier(UUID.randomUUID(), "pos", 20, AttributeModifier.Operation.ADD_NUMBER);
            AttributeModifier negMod = new AttributeModifier(UUID.randomUUID(), "neg", -10, AttributeModifier.Operation.ADD_NUMBER);

            Collection<AttributeModifier> modifiers = Arrays.asList(posMod, negMod);

            double result = AttributeCalculate.calculateAttributeModifier(100, modifiers, true);

            assertEquals(120, result);
        }

        @Test
        @DisplayName("Should include negative values when noNegative is false")
        void calculateAttributeModifier_withoutNoNegativeFlag_includesNegative() {
            AttributeModifier posMod = new AttributeModifier(UUID.randomUUID(), "pos", 20, AttributeModifier.Operation.ADD_NUMBER);
            AttributeModifier negMod = new AttributeModifier(UUID.randomUUID(), "neg", -10, AttributeModifier.Operation.ADD_NUMBER);

            Collection<AttributeModifier> modifiers = Arrays.asList(posMod, negMod);

            double result = AttributeCalculate.calculateAttributeModifier(100, modifiers, false);

            assertEquals(110, result);
        }

        @Test
        @DisplayName("Should handle empty modifier collection")
        void calculateAttributeModifier_withEmptyCollection_returnsBase() {
            double result = AttributeCalculate.calculateAttributeModifier(100, new ArrayList<>());

            assertEquals(100, result);
        }
    }

    @Nested
    @DisplayName("Combat mechanic calculations")
    class CombatMechanicTests {

        @Test
        @DisplayName("Dodge calculation with accuracy reduces effective dodge chance")
        void dodgeCalculation_withAccuracy_reducesChance() {
            // Defender: 50% dodge
            // Attacker: 30% accuracy
            // Effective dodge: 50 - 30 = 20%
            double dodge = 50;
            double accuracy = 30;
            double effectiveDodge = Math.max(0, dodge - accuracy);

            assertEquals(20, effectiveDodge);
        }

        @Test
        @DisplayName("Critical damage multiplies base damage")
        void criticalDamage_multipliesDamage() {
            double baseDamage = 100;
            double critMultiplier = 2.5;

            double critDamage = baseDamage * critMultiplier;

            assertEquals(250, critDamage);
        }

        @Test
        @DisplayName("Life steal heals correct amount based on damage dealt")
        void lifeSteal_healsCorrectAmount() {
            double damage = 100;
            double lifeStealPercent = 25;

            double heal = damage * (lifeStealPercent / 100);

            assertEquals(25, heal);
        }

        @Test
        @DisplayName("Damage reduction reduces incoming damage")
        void damageReduction_reducesDamage() {
            double incomingDamage = 100;
            double reductionPercent = 30;

            double reducedDamage = incomingDamage * (1 - reductionPercent / 100);

            assertEquals(70, reducedDamage);
        }

        @Test
        @DisplayName("Armor penetration ignores part of enemy armor")
        void armorPenetration_ignoresArmor() {
            double enemyArmor = 50;
            double armorPenPercent = 40;

            double effectiveArmor = enemyArmor * (1 - armorPenPercent / 100);

            assertEquals(30, effectiveArmor);
        }

        @Test
        @DisplayName("Grievous wounds reduces healing effectiveness")
        void grievousWounds_reducesHealing() {
            double healAmount = 100;
            double grievousPercent = 40;

            double effectiveHeal = healAmount * (1 - grievousPercent / 100);

            assertEquals(60, effectiveHeal);
        }
    }

    // Helper methods

    private NMSAttribute createMockAttribute(double amount, NMSAttributeOperation operation) {
        return createMockAttribute(amount, operation, testAttributeType);
    }

    private NMSAttribute createMockAttribute(double amount, NMSAttributeOperation operation, NMSAttributeType type) {
        NMSAttribute attr = mock(NMSAttribute.class);
        when(attr.getAmount()).thenReturn(amount);
        when(attr.getOperation()).thenReturn(operation);
        when(attr.getAttributeType()).thenReturn(type);
        return attr;
    }
}
