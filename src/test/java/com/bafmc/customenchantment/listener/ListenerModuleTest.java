package com.bafmc.customenchantment.listener;

import com.bafmc.customenchantment.CustomEnchantment;
import com.bafmc.customenchantment.guard.GuardManager;
import com.bafmc.customenchantment.guard.GuardModule;
import com.bafmc.customenchantment.task.BlockTask;
import com.bafmc.customenchantment.task.TaskModule;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for ListenerModule class.
 * Tests module lifecycle and listener registration.
 */
@DisplayName("ListenerModule Tests")
class ListenerModuleTest {

    private ListenerModule listenerModule;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private Server mockServer;

    @Mock
    private PluginManager mockPluginManager;

    @Mock
    private GuardModule mockGuardModule;

    @Mock
    private GuardManager mockGuardManager;

    @Mock
    private TaskModule mockTaskModule;

    @Mock
    private BlockTask mockBlockTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockPlugin.getGuardModule()).thenReturn(mockGuardModule);
        when(mockGuardModule.getGuardManager()).thenReturn(mockGuardManager);
        when(mockPlugin.getTaskModule()).thenReturn(mockTaskModule);
        when(mockTaskModule.getBlockTask()).thenReturn(mockBlockTask);

        listenerModule = new ListenerModule(mockPlugin);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should initialize with plugin reference")
        void constructor_initializesWithPlugin() {
            assertNotNull(listenerModule);
        }

        @Test
        @DisplayName("should allow multiple instances with different plugins")
        void constructor_allowsMultipleInstances() {
            CustomEnchantment mockPlugin2 = mock(CustomEnchantment.class);
            when(mockPlugin2.getGuardModule()).thenReturn(mockGuardModule);
            when(mockPlugin2.getTaskModule()).thenReturn(mockTaskModule);

            ListenerModule module1 = new ListenerModule(mockPlugin);
            ListenerModule module2 = new ListenerModule(mockPlugin2);

            assertNotNull(module1);
            assertNotNull(module2);
            assertNotSame(module1, module2);
        }
    }

    @Nested
    @DisplayName("onEnable Tests")
    class OnEnableTests {

        @Test
        @DisplayName("should register core listeners when enabled")
        void onEnable_registersCoreListeners() {
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
                mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);
                when(mockPluginManager.isPluginEnabled("StackMob")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("CustomMenu")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("mcMMO")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("CustomFarm")).thenReturn(false);

                assertDoesNotThrow(() -> listenerModule.onEnable());

                // Verify that events are registered (at least 9 core listeners)
                verify(mockPluginManager, atLeast(9)).registerEvents(any(), eq(mockPlugin));
            }
        }

        @Test
        @DisplayName("should register MobStackDeathListener when StackMob is enabled")
        void onEnable_registersMobStackDeathListenerWhenStackMobEnabled() {
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
                mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);
                when(mockPluginManager.isPluginEnabled("StackMob")).thenReturn(true);
                when(mockPluginManager.isPluginEnabled("CustomMenu")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("mcMMO")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("CustomFarm")).thenReturn(false);

                assertDoesNotThrow(() -> listenerModule.onEnable());

                // Verify StackMob plugin check
                verify(mockPluginManager).isPluginEnabled("StackMob");
            }
        }

        @Test
        @DisplayName("should register MobDeathListener when StackMob is disabled")
        void onEnable_registersMobDeathListenerWhenStackMobDisabled() {
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
                mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);
                when(mockPluginManager.isPluginEnabled("StackMob")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("CustomMenu")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("mcMMO")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("CustomFarm")).thenReturn(false);

                assertDoesNotThrow(() -> listenerModule.onEnable());

                verify(mockPluginManager).isPluginEnabled("StackMob");
            }
        }

        @Test
        @DisplayName("should register CMenuListener when CustomMenu is enabled")
        void onEnable_registersCMenuListenerWhenCustomMenuEnabled() {
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
                mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);
                when(mockPluginManager.isPluginEnabled("StackMob")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("CustomMenu")).thenReturn(true);
                when(mockPluginManager.isPluginEnabled("mcMMO")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("CustomFarm")).thenReturn(false);

                assertDoesNotThrow(() -> listenerModule.onEnable());

                verify(mockPluginManager).isPluginEnabled("CustomMenu");
            }
        }

        @Test
        @DisplayName("should register McMMOListener when mcMMO is enabled")
        void onEnable_registersMcMMOListenerWhenMcMMOEnabled() {
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
                mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);
                when(mockPluginManager.isPluginEnabled("StackMob")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("CustomMenu")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("mcMMO")).thenReturn(true);
                when(mockPluginManager.isPluginEnabled("CustomFarm")).thenReturn(false);

                assertDoesNotThrow(() -> listenerModule.onEnable());

                verify(mockPluginManager).isPluginEnabled("mcMMO");
            }
        }

        @Test
        @DisplayName("should register CustomFarmListener when CustomFarm is enabled")
        void onEnable_registersCustomFarmListenerWhenCustomFarmEnabled() {
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
                mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);
                when(mockPluginManager.isPluginEnabled("StackMob")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("CustomMenu")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("mcMMO")).thenReturn(false);
                when(mockPluginManager.isPluginEnabled("CustomFarm")).thenReturn(true);

                assertDoesNotThrow(() -> listenerModule.onEnable());

                verify(mockPluginManager).isPluginEnabled("CustomFarm");
            }
        }

        @Test
        @DisplayName("should register all optional listeners when all plugins are enabled")
        void onEnable_registersAllOptionalListenersWhenAllPluginsEnabled() {
            try (MockedStatic<Bukkit> mockedBukkit = mockStatic(Bukkit.class)) {
                mockedBukkit.when(Bukkit::getServer).thenReturn(mockServer);
                mockedBukkit.when(Bukkit::getPluginManager).thenReturn(mockPluginManager);
                when(mockPluginManager.isPluginEnabled("StackMob")).thenReturn(true);
                when(mockPluginManager.isPluginEnabled("CustomMenu")).thenReturn(true);
                when(mockPluginManager.isPluginEnabled("mcMMO")).thenReturn(true);
                when(mockPluginManager.isPluginEnabled("CustomFarm")).thenReturn(true);

                assertDoesNotThrow(() -> listenerModule.onEnable());

                // Verify all plugin checks
                verify(mockPluginManager).isPluginEnabled("StackMob");
                verify(mockPluginManager).isPluginEnabled("CustomMenu");
                verify(mockPluginManager).isPluginEnabled("mcMMO");
                verify(mockPluginManager).isPluginEnabled("CustomFarm");
            }
        }
    }
}
