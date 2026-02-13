package com.bafmc.customenchantment.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEWeaponFactory Tests")
class CEWeaponFactoryTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
    }

    @Test
    @DisplayName("should create CEWeaponFactory instance")
    void shouldCreateCEWeaponFactoryInstance() {
        CEWeaponFactory factory = new CEWeaponFactory();
        assertNotNull(factory);
    }

    @Test
    @DisplayName("should extend CEItemFactory")
    void shouldExtendCEItemFactory() {
        CEWeaponFactory factory = new CEWeaponFactory();
        assertTrue(factory instanceof CEItemFactory);
    }

    @Test
    @DisplayName("should create weapon from ItemStack")
    void shouldCreateWeaponFromItemStack() {
        CEWeaponFactory factory = new CEWeaponFactory();
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

        CEWeapon weapon = factory.create(item);
        assertNotNull(weapon);
    }

    @Test
    @DisplayName("should match WEAPON type")
    void shouldMatchWeaponType() {
        CEWeaponFactory factory = new CEWeaponFactory();
        assertTrue(factory.isMatchType(CEItemType.WEAPON));
    }
}
