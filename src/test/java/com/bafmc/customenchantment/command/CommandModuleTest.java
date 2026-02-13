package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.module.PluginModule;
import com.bafmc.customenchantment.CustomEnchantment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CommandModule class.
 * Tests the command module lifecycle and structure.
 */
@DisplayName("CommandModule Tests")
class CommandModuleTest {

    @Mock
    private CustomEnchantment mockPlugin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should be instantiable with plugin parameter")
        void shouldBeInstantiable() {
            CommandModule module = new CommandModule(mockPlugin);
            assertNotNull(module);
        }

        @Test
        @DisplayName("should store plugin reference")
        void shouldStorePluginReference() {
            CommandModule module = new CommandModule(mockPlugin);
            assertEquals(mockPlugin, module.getPlugin());
        }
    }

    @Nested
    @DisplayName("Inheritance Tests")
    class InheritanceTests {

        @Test
        @DisplayName("should extend PluginModule")
        void shouldExtendPluginModule() {
            CommandModule module = new CommandModule(mockPlugin);
            assertInstanceOf(PluginModule.class, module);
        }

        @Test
        @DisplayName("should have onEnable method")
        void shouldHaveOnEnableMethod() {
            assertDoesNotThrow(() ->
                    CommandModule.class.getMethod("onEnable"));
        }
    }

    @Nested
    @DisplayName("Module Type Tests")
    class ModuleTypeTests {

        @Test
        @DisplayName("should be parameterized with CustomEnchantment")
        void shouldBeParameterizedWithCustomEnchantment() {
            CommandModule module = new CommandModule(mockPlugin);
            // Verify the plugin type is correct
            Object plugin = module.getPlugin();
            assertInstanceOf(CustomEnchantment.class, plugin);
        }
    }
}
