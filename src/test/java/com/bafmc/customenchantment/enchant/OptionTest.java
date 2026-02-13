package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.attribute.RangeAttribute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Option - holds a list of RangeAttribute for enchant options.
 */
@DisplayName("Option Tests")
class OptionTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create with empty list")
        void shouldCreateWithEmptyList() {
            Option option = new Option(new ArrayList<>());

            assertNotNull(option);
            assertTrue(option.getOptionDataList().isEmpty());
        }

        @Test
        @DisplayName("should create with populated list")
        void shouldCreateWithPopulatedList() {
            RangeAttribute attr = mock(RangeAttribute.class);
            List<RangeAttribute> list = new ArrayList<>(Collections.singletonList(attr));

            Option option = new Option(list);

            assertEquals(1, option.getOptionDataList().size());
        }
    }

    @Nested
    @DisplayName("getOptionDataList Tests")
    class GetOptionDataListTests {

        @Test
        @DisplayName("should return the option data list")
        void shouldReturnOptionDataList() {
            RangeAttribute attr1 = mock(RangeAttribute.class);
            RangeAttribute attr2 = mock(RangeAttribute.class);
            List<RangeAttribute> list = new ArrayList<>();
            list.add(attr1);
            list.add(attr2);

            Option option = new Option(list);

            assertEquals(2, option.getOptionDataList().size());
            assertSame(attr1, option.getOptionDataList().get(0));
            assertSame(attr2, option.getOptionDataList().get(1));
        }
    }

    @Nested
    @DisplayName("setOptionDataList Tests")
    class SetOptionDataListTests {

        @Test
        @DisplayName("should replace the list")
        void shouldReplaceList() {
            Option option = new Option(new ArrayList<>());
            assertTrue(option.getOptionDataList().isEmpty());

            RangeAttribute attr = mock(RangeAttribute.class);
            List<RangeAttribute> newList = new ArrayList<>(Collections.singletonList(attr));
            option.setOptionDataList(newList);

            assertEquals(1, option.getOptionDataList().size());
        }

        @Test
        @DisplayName("should clear by setting empty list")
        void shouldClearBySettingEmpty() {
            RangeAttribute attr = mock(RangeAttribute.class);
            List<RangeAttribute> list = new ArrayList<>(Collections.singletonList(attr));
            Option option = new Option(list);

            option.setOptionDataList(new ArrayList<>());

            assertTrue(option.getOptionDataList().isEmpty());
        }
    }
}
