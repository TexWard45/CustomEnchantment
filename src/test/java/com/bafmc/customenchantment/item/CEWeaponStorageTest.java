package com.bafmc.customenchantment.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEWeaponStorage Tests")
class CEWeaponStorageTest {

    @Test
    @DisplayName("should create CEWeaponStorage instance")
    void shouldCreateCEWeaponStorageInstance() {
        CEWeaponStorage storage = new CEWeaponStorage();
        assertNotNull(storage);
    }
}
