package com.bafmc.customenchantment.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEUnify Tests")
class CEUnifyTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
    }

    @Test
    @DisplayName("should create CEUnify instance")
    void shouldCreateCEUnifyInstance() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        CEUnify unify = new CEUnify(item);
        assertNotNull(unify);
    }

    @Test
    @DisplayName("should get type")
    void shouldGetType() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        CEUnify unify = new CEUnify(item);
        assertNotNull(unify.getType());
    }
}
