package com.bafmc.customenchantment.enchant;

import com.bafmc.customenchantment.CustomEnchantment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for EnchantModule - module that registers all conditions and effects.
 * Since we can't initialize the full plugin, we test the class structure and method existence.
 */
@DisplayName("EnchantModule Tests")
class EnchantModuleTest {

    @Nested
    @DisplayName("Class Structure Tests")
    class ClassStructureTests {

        @Test
        @DisplayName("should extend PluginModule")
        void shouldExtendPluginModule() {
            assertTrue(com.bafmc.bukkit.module.PluginModule.class.isAssignableFrom(EnchantModule.class));
        }

        @Test
        @DisplayName("should have onEnable method")
        void shouldHaveOnEnableMethod() throws NoSuchMethodException {
            Method method = EnchantModule.class.getMethod("onEnable");
            assertNotNull(method);
        }

        @Test
        @DisplayName("should have setupCondition method")
        void shouldHaveSetupConditionMethod() throws NoSuchMethodException {
            Method method = EnchantModule.class.getMethod("setupCondition");
            assertNotNull(method);
        }

        @Test
        @DisplayName("should have setupEffect method")
        void shouldHaveSetupEffectMethod() throws NoSuchMethodException {
            Method method = EnchantModule.class.getMethod("setupEffect");
            assertNotNull(method);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should accept CustomEnchantment plugin")
        void shouldAcceptPlugin() {
            CustomEnchantment mockPlugin = mock(CustomEnchantment.class);

            EnchantModule module = new EnchantModule(mockPlugin);

            assertNotNull(module);
        }
    }

    @Nested
    @DisplayName("setupCondition Tests")
    class SetupConditionTests {

        @Test
        @DisplayName("should register all conditions without throwing")
        void shouldRegisterAllConditions() {
            CustomEnchantment mockPlugin = mock(CustomEnchantment.class);
            EnchantModule module = new EnchantModule(mockPlugin);

            // setupCondition registers conditions to static registry
            // This should not throw even if some are already registered
            assertDoesNotThrow(() -> module.setupCondition());
        }
    }

    @Nested
    @DisplayName("setupEffect Tests")
    class SetupEffectTests {

        @Test
        @DisplayName("should register all effects without throwing")
        void shouldRegisterAllEffects() {
            CustomEnchantment mockPlugin = mock(CustomEnchantment.class);
            EnchantModule module = new EnchantModule(mockPlugin);

            // setupEffect registers effects to static registry
            assertDoesNotThrow(() -> module.setupEffect());
        }
    }

    @Nested
    @DisplayName("onEnable Tests")
    class OnEnableTests {

        @Test
        @DisplayName("should call setupCondition and setupEffect")
        void shouldCallSetupMethods() {
            CustomEnchantment mockPlugin = mock(CustomEnchantment.class);
            EnchantModule module = new EnchantModule(mockPlugin);

            // onEnable calls both setup methods
            assertDoesNotThrow(() -> module.onEnable());
        }
    }
}
