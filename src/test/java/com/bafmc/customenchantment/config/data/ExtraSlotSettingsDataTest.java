package com.bafmc.customenchantment.config.data;

import com.bafmc.bukkit.utils.EquipSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ExtraSlotSettingsData class - data class for extra slot settings.
 */
@DisplayName("ExtraSlotSettingsData Tests")
class ExtraSlotSettingsDataTest {

    private ExtraSlotSettingsData data;

    @BeforeEach
    void setUp() {
        data = new ExtraSlotSettingsData();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create instance with default constructor")
        void shouldCreateInstanceWithDefaultConstructor() {
            ExtraSlotSettingsData newData = new ExtraSlotSettingsData();

            assertNotNull(newData);
        }
    }

    @Nested
    @DisplayName("Annotation Tests")
    class AnnotationTests {

        @Test
        @DisplayName("Should have Configuration annotation")
        void shouldHaveConfigurationAnnotation() {
            assertNotNull(ExtraSlotSettingsData.class.getAnnotation(
                    com.bafmc.bukkit.config.annotation.Configuration.class));
        }

        @Test
        @DisplayName("Should have Getter annotation from Lombok")
        void shouldHaveGetterAnnotationFromLombok() {
            assertNotNull(ExtraSlotSettingsData.class.getAnnotation(lombok.Getter.class));
        }

        @Test
        @DisplayName("Should have ToString annotation from Lombok")
        void shouldHaveToStringAnnotationFromLombok() {
            assertNotNull(ExtraSlotSettingsData.class.getAnnotation(lombok.ToString.class));
        }

        @Test
        @DisplayName("maxCount field should have Path annotation")
        void maxCountFieldShouldHavePathAnnotation() throws NoSuchFieldException {
            Field field = ExtraSlotSettingsData.class.getDeclaredField("maxCount");

            assertNotNull(field.getAnnotation(com.bafmc.bukkit.config.annotation.Path.class));
        }

        @Test
        @DisplayName("list field should have Path annotation")
        void listFieldShouldHavePathAnnotation() throws NoSuchFieldException {
            Field field = ExtraSlotSettingsData.class.getDeclaredField("list");

            assertNotNull(field.getAnnotation(com.bafmc.bukkit.config.annotation.Path.class));
        }

        @Test
        @DisplayName("slots field should have Path annotation")
        void slotsFieldShouldHavePathAnnotation() throws NoSuchFieldException {
            Field field = ExtraSlotSettingsData.class.getDeclaredField("slots");

            assertNotNull(field.getAnnotation(com.bafmc.bukkit.config.annotation.Path.class));
        }

        @Test
        @DisplayName("slots field should have ListType annotation")
        void slotsFieldShouldHaveListTypeAnnotation() throws NoSuchFieldException {
            Field field = ExtraSlotSettingsData.class.getDeclaredField("slots");

            assertNotNull(field.getAnnotation(com.bafmc.bukkit.config.annotation.ListType.class));
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("Should have getMaxCount method")
        void shouldHaveGetMaxCountMethod() {
            try {
                data.getClass().getMethod("getMaxCount");
            } catch (NoSuchMethodException e) {
                fail("getMaxCount method should exist");
            }
        }

        @Test
        @DisplayName("Should have getList method")
        void shouldHaveGetListMethod() {
            try {
                data.getClass().getMethod("getList");
            } catch (NoSuchMethodException e) {
                fail("getList method should exist");
            }
        }

        @Test
        @DisplayName("Should have getSlots method")
        void shouldHaveGetSlotsMethod() {
            try {
                data.getClass().getMethod("getSlots");
            } catch (NoSuchMethodException e) {
                fail("getSlots method should exist");
            }
        }

        @Test
        @DisplayName("getMaxCount should return int")
        void getMaxCountShouldReturnInt() throws Exception {
            assertEquals(int.class, data.getClass().getMethod("getMaxCount").getReturnType());
        }

        @Test
        @DisplayName("getList should return List")
        void getListShouldReturnList() throws Exception {
            assertEquals(List.class, data.getClass().getMethod("getList").getReturnType());
        }

        @Test
        @DisplayName("getSlots should return List")
        void getSlotsShouldReturnList() throws Exception {
            assertEquals(List.class, data.getClass().getMethod("getSlots").getReturnType());
        }
    }

    @Nested
    @DisplayName("getSlot Method Tests")
    class GetSlotMethodTests {

        @Test
        @DisplayName("Should have getSlot method")
        void shouldHaveGetSlotMethod() {
            try {
                data.getClass().getMethod("getSlot", int.class);
            } catch (NoSuchMethodException e) {
                fail("getSlot(int) method should exist");
            }
        }

        @Test
        @DisplayName("getSlot should return EquipSlot")
        void getSlotShouldReturnEquipSlot() throws Exception {
            assertEquals(EquipSlot.class, data.getClass().getMethod("getSlot", int.class).getReturnType());
        }

        @Test
        @DisplayName("getSlot should return slot at given index")
        void getSlotShouldReturnSlotAtGivenIndex() throws Exception {
            // Set up test data
            List<EquipSlot> slots = Arrays.asList(
                    EquipSlot.EXTRA_SLOT_1,
                    EquipSlot.EXTRA_SLOT_2,
                    EquipSlot.EXTRA_SLOT_3
            );
            setPrivateField(data, "slots", slots);

            assertEquals(EquipSlot.EXTRA_SLOT_1, data.getSlot(0));
            assertEquals(EquipSlot.EXTRA_SLOT_2, data.getSlot(1));
            assertEquals(EquipSlot.EXTRA_SLOT_3, data.getSlot(2));
        }

        @Test
        @DisplayName("getSlot should throw IndexOutOfBoundsException for invalid index")
        void getSlotShouldThrowExceptionForInvalidIndex() throws Exception {
            List<EquipSlot> slots = Arrays.asList(EquipSlot.EXTRA_SLOT_1);
            setPrivateField(data, "slots", slots);

            assertThrows(IndexOutOfBoundsException.class, () -> data.getSlot(5));
        }

        @Test
        @DisplayName("getSlot should throw exception for negative index")
        void getSlotShouldThrowExceptionForNegativeIndex() throws Exception {
            List<EquipSlot> slots = Arrays.asList(EquipSlot.EXTRA_SLOT_1);
            setPrivateField(data, "slots", slots);

            assertThrows(IndexOutOfBoundsException.class, () -> data.getSlot(-1));
        }
    }

    @Nested
    @DisplayName("toString Method Tests")
    class ToStringMethodTests {

        @Test
        @DisplayName("Should have toString method from Lombok")
        void shouldHaveToStringMethodFromLombok() {
            String toString = data.toString();

            assertNotNull(toString);
            assertTrue(toString.contains("ExtraSlotSettingsData"));
        }
    }

    @Nested
    @DisplayName("Field Type Tests")
    class FieldTypeTests {

        @Test
        @DisplayName("maxCount should be int type")
        void maxCountShouldBeIntType() throws NoSuchFieldException {
            Field field = ExtraSlotSettingsData.class.getDeclaredField("maxCount");

            assertEquals(int.class, field.getType());
        }

        @Test
        @DisplayName("list should be List type")
        void listShouldBeListType() throws NoSuchFieldException {
            Field field = ExtraSlotSettingsData.class.getDeclaredField("list");

            assertEquals(List.class, field.getType());
        }

        @Test
        @DisplayName("slots should be List type")
        void slotsShouldBeListType() throws NoSuchFieldException {
            Field field = ExtraSlotSettingsData.class.getDeclaredField("slots");

            assertEquals(List.class, field.getType());
        }
    }

    @Nested
    @DisplayName("Data Setting Tests")
    class DataSettingTests {

        @Test
        @DisplayName("Should be able to set maxCount via reflection")
        void shouldBeAbleToSetMaxCountViaReflection() throws Exception {
            setPrivateField(data, "maxCount", 5);

            assertEquals(5, data.getMaxCount());
        }

        @Test
        @DisplayName("Should be able to set list via reflection")
        void shouldBeAbleToSetListViaReflection() throws Exception {
            List<String> testList = Arrays.asList("artifact", "sigil", "outfit");
            setPrivateField(data, "list", testList);

            assertEquals(testList, data.getList());
            assertEquals(3, data.getList().size());
        }

        @Test
        @DisplayName("Should be able to set slots via reflection")
        void shouldBeAbleToSetSlotsViaReflection() throws Exception {
            List<EquipSlot> testSlots = Arrays.asList(
                    EquipSlot.EXTRA_SLOT_1,
                    EquipSlot.EXTRA_SLOT_2
            );
            setPrivateField(data, "slots", testSlots);

            assertEquals(testSlots, data.getSlots());
            assertEquals(2, data.getSlots().size());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null list")
        void shouldHandleNullList() throws Exception {
            setPrivateField(data, "list", null);

            assertNull(data.getList());
        }

        @Test
        @DisplayName("Should handle null slots")
        void shouldHandleNullSlots() throws Exception {
            setPrivateField(data, "slots", null);

            assertNull(data.getSlots());
        }

        @Test
        @DisplayName("Should handle empty list")
        void shouldHandleEmptyList() throws Exception {
            setPrivateField(data, "list", Arrays.asList());

            assertNotNull(data.getList());
            assertTrue(data.getList().isEmpty());
        }

        @Test
        @DisplayName("Should handle empty slots")
        void shouldHandleEmptySlots() throws Exception {
            setPrivateField(data, "slots", Arrays.asList());

            assertNotNull(data.getSlots());
            assertTrue(data.getSlots().isEmpty());
        }

        @Test
        @DisplayName("Should handle zero maxCount")
        void shouldHandleZeroMaxCount() throws Exception {
            setPrivateField(data, "maxCount", 0);

            assertEquals(0, data.getMaxCount());
        }

        @Test
        @DisplayName("Should handle negative maxCount")
        void shouldHandleNegativeMaxCount() throws Exception {
            setPrivateField(data, "maxCount", -1);

            assertEquals(-1, data.getMaxCount());
        }
    }

    // Helper method to set private fields via reflection
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
