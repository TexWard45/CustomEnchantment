package com.bafmc.customenchantment.menu;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for MenuModule - menu system module
 * Covers module initialization, plugin reference, and type validation.
 */
@DisplayName("MenuModule Tests")
class MenuModuleTest {

    private MenuModule menuModule;
    private CustomEnchantment mockPlugin;

    @BeforeEach
    void setUp() {
        mockPlugin = mock(CustomEnchantment.class);
        menuModule = new MenuModule(mockPlugin);
    }

    @Nested
    @DisplayName("Module Initialization Tests")
    class ModuleInitializationTests {

        @Test
        @DisplayName("creates MenuModule instance successfully")
        void shouldCreateModuleInstance() {
            assertNotNull(menuModule);
        }

        @Test
        @DisplayName("is a PluginModule subclass")
        void shouldBePluginModule() {
            assertInstanceOf(PluginModule.class, menuModule);
        }

        @Test
        @DisplayName("stores plugin reference via getPlugin()")
        void shouldStorePluginReference() {
            assertSame(mockPlugin, menuModule.getPlugin());
        }

        @Test
        @DisplayName("accepts different plugin instances in constructor")
        void shouldAcceptDifferentPluginInstances() {
            CustomEnchantment plugin1 = mock(CustomEnchantment.class);
            CustomEnchantment plugin2 = mock(CustomEnchantment.class);

            MenuModule module1 = new MenuModule(plugin1);
            MenuModule module2 = new MenuModule(plugin2);

            assertSame(plugin1, module1.getPlugin());
            assertSame(plugin2, module2.getPlugin());
            assertNotSame(module1.getPlugin(), module2.getPlugin());
        }
    }

    @Nested
    @DisplayName("Module Type Tests")
    class ModuleTypeTests {

        @Test
        @DisplayName("MenuModule generic type is CustomEnchantment")
        void shouldHaveCorrectGenericType() {
            // Verifies the module is properly typed as PluginModule<CustomEnchantment>
            PluginModule<CustomEnchantment> typed = menuModule;
            assertNotNull(typed);
            assertInstanceOf(CustomEnchantment.class, typed.getPlugin());
        }

        @Test
        @DisplayName("MenuModule is not the same as another module instance")
        void shouldCreateDistinctInstances() {
            MenuModule another = new MenuModule(mockPlugin);
            assertNotSame(menuModule, another);
        }
    }
}
