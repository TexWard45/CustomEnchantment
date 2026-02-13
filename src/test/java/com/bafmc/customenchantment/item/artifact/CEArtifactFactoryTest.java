package com.bafmc.customenchantment.item.artifact;

import com.bafmc.customenchantment.item.CEItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CEArtifactFactory Tests")
class CEArtifactFactoryTest {

    private static ServerMock server;

    @BeforeAll
    static void setUpAll() {
        if (MockBukkit.isMocked()) {
            MockBukkit.unmock();
        }
        server = MockBukkit.mock();
    }

    @Test
    @DisplayName("should create CEArtifactFactory instance")
    void shouldCreateCEArtifactFactoryInstance() {
        CEArtifactFactory factory = new CEArtifactFactory();
        assertNotNull(factory);
    }

    @Test
    @DisplayName("should extend CEItemFactory")
    void shouldExtendCEItemFactory() {
        CEArtifactFactory factory = new CEArtifactFactory();
        assertTrue(factory instanceof CEItemFactory);
    }

    @Test
    @DisplayName("should create artifact from ItemStack")
    void shouldCreateArtifactFromItemStack() {
        CEArtifactFactory factory = new CEArtifactFactory();
        ItemStack item = new ItemStack(Material.DIAMOND);

        CEArtifact artifact = factory.create(item);
        assertNotNull(artifact);
    }
}
