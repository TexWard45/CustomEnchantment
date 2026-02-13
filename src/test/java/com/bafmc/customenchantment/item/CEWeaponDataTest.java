package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEWeaponData Tests")
class CEWeaponDataTest {

    @Test
    @DisplayName("should extend CEItemData")
    void shouldExtendCEItemData() {
        CEWeaponData data = new CEWeaponData();
        assertTrue(data instanceof CEItemData);
    }

    @Test
    @DisplayName("should create CEWeaponData instance")
    void shouldCreateCEWeaponDataInstance() {
        CEWeaponData data = new CEWeaponData();
        assertNotNull(data);
    }

    @Test
    @DisplayName("should inherit CEItemData behavior")
    void shouldInheritCEItemDataBehavior() {
        CEWeaponData data1 = new CEWeaponData();
        CEWeaponData data2 = new CEWeaponData();

        assertNotNull(data1);
        assertNotNull(data2);
        assertNotSame(data1, data2);
    }
}
