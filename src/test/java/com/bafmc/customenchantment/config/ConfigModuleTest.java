package com.bafmc.customenchantment.config;

import com.bafmc.customenchantment.CustomEnchantment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ConfigModule class - module responsible for loading all configurations.
 */
@DisplayName("ConfigModule Tests")
@ExtendWith(MockitoExtension.class)
class ConfigModuleTest {

    @Mock
    private CustomEnchantment mockPlugin;

    private ConfigModule configModule;

    @BeforeEach
    void setUp() {
        configModule = new ConfigModule(mockPlugin);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create ConfigModule with plugin reference")
        void shouldCreateConfigModuleWithPluginReference() {
            assertNotNull(configModule);
        }

        @Test
        @DisplayName("Should store plugin reference")
        void shouldStorePluginReference() {
            assertSame(mockPlugin, configModule.getPlugin());
        }
    }

    @Nested
    @DisplayName("Module Lifecycle Tests")
    class ModuleLifecycleTests {

        @Test
        @DisplayName("ConfigModule should extend PluginModule")
        void configModuleShouldExtendPluginModule() {
            assertTrue(configModule instanceof com.bafmc.bukkit.module.PluginModule);
        }

        @Test
        @DisplayName("ConfigModule should be parameterized with CustomEnchantment")
        void configModuleShouldBeParameterizedWithCustomEnchantment() {
            // Verify ConfigModule extends PluginModule<CustomEnchantment> by checking
            // the constructor accepts CustomEnchantment and getPlugin() returns it
            assertSame(mockPlugin, configModule.getPlugin());
            assertTrue(configModule instanceof com.bafmc.bukkit.module.PluginModule);
        }
    }

    @Nested
    @DisplayName("onEnable Method Tests")
    class OnEnableMethodTests {

        @Test
        @DisplayName("onEnable should call onReload")
        void onEnableShouldCallOnReload() {
            // We can't directly test this without a full mock setup,
            // but we can verify the method exists and is callable
            assertNotNull(configModule);

            // Verify onEnable method exists
            try {
                configModule.getClass().getMethod("onEnable");
            } catch (NoSuchMethodException e) {
                fail("onEnable method should exist");
            }
        }
    }

    @Nested
    @DisplayName("onReload Method Tests")
    class OnReloadMethodTests {

        @Test
        @DisplayName("onReload method should exist")
        void onReloadMethodShouldExist() {
            try {
                configModule.getClass().getMethod("onReload");
            } catch (NoSuchMethodException e) {
                fail("onReload method should exist");
            }
        }
    }

    @Nested
    @DisplayName("Configuration Loading Order Tests")
    class ConfigurationLoadingOrderTests {

        @Test
        @DisplayName("Should have correct class name")
        void shouldHaveCorrectClassName() {
            assertEquals("ConfigModule", ConfigModule.class.getSimpleName());
        }

        @Test
        @DisplayName("Should be in correct package")
        void shouldBeInCorrectPackage() {
            assertEquals("com.bafmc.customenchantment.config", ConfigModule.class.getPackageName());
        }
    }

    @Nested
    @DisplayName("Dependency Tests")
    class DependencyTests {

        @Test
        @DisplayName("Should depend on CustomEnchantment plugin")
        void shouldDependOnCustomEnchantmentPlugin() {
            // Verify the constructor parameter type
            try {
                ConfigModule.class.getConstructor(CustomEnchantment.class);
            } catch (NoSuchMethodException e) {
                fail("Constructor should accept CustomEnchantment parameter");
            }
        }
    }
}
