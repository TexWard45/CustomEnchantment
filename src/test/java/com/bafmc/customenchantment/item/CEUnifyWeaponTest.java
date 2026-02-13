package com.bafmc.customenchantment.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEUnifyWeapon Tests")
class CEUnifyWeaponTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
    }

    @Test
    @DisplayName("should create CEUnifyWeapon instance")
    void shouldCreateCEUnifyWeaponInstance() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        CEUnifyWeapon weapon = new CEUnifyWeapon(item);
        assertNotNull(weapon);
    }

    @Test
    @DisplayName("should extend CEUnify")
    void shouldExtendCEUnify() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        CEUnifyWeapon weapon = new CEUnifyWeapon(item);
        assertTrue(weapon instanceof CEUnify);
    }
}
