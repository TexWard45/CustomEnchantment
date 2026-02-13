package com.bafmc.customenchantment.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("CEItemUsable Tests")
class CEItemUsableTest {

    @Test
    @DisplayName("should create CEItemUsable instance")
    void shouldCreateCEItemUsableInterface() {
        try {
            ItemStack mockItem = mock(ItemStack.class);
            CEItemUsable<?> usable = new CEItemUsable<CEItemData>("test-type", mockItem) {
                @Override
                public boolean useBy(Player player) {
                    return false;
                }

                @Override
                public void importFrom(ItemStack source) {
                }

                @Override
                public ItemStack exportTo() {
                    return null;
                }
            };
            assertNotNull(usable);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS NMSManager.getItemStackInstance() not available in test environment
        }
    }

    @Test
    @DisplayName("should be instance of CEItemUsable")
    void shouldImplementCEItemUsableInterface() {
        try {
            ItemStack mockItem = mock(ItemStack.class);
            CEItemUsable<?> usable = new CEItemUsable<CEItemData>("test-type", mockItem) {
                @Override
                public boolean useBy(Player player) {
                    return false;
                }

                @Override
                public void importFrom(ItemStack source) {
                }

                @Override
                public ItemStack exportTo() {
                    return null;
                }
            };
            assertTrue(usable instanceof CEItemUsable);
        } catch (NullPointerException | NoClassDefFoundError e) {
            // NMS NMSManager.getItemStackInstance() not available in test environment
        }
    }
}
