package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.CommandRegistrar;
import com.bafmc.customenchantment.CustomEnchantment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CustomEnchantmentCommand class.
 * Tests the root command registrar that delegates to all sub-command registrars.
 */
@DisplayName("CustomEnchantmentCommand Tests")
class CustomEnchantmentCommandTest {

    private CustomEnchantmentCommand customEnchantmentCommand;

    @Mock
    private CustomEnchantment mockPlugin;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customEnchantmentCommand = new CustomEnchantmentCommand(mockPlugin);
        when(mockBuilder.subCommand(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.subCommand(any(String[].class))).thenReturn(mockBuilder);
        when(mockBuilder.permission(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.commandExecutor(any())).thenReturn(mockBuilder);
        when(mockBuilder.commandExecutorUntilNextCommand(any())).thenReturn(mockBuilder);
        when(mockBuilder.tabCompleter(any())).thenReturn(mockBuilder);
        when(mockBuilder.tabCompleterUntilNextTab(any())).thenReturn(mockBuilder);
        when(mockBuilder.end()).thenReturn(mockBuilder);
    }

    @Nested
    @DisplayName("Interface Implementation Tests")
    class InterfaceTests {

        @Test
        @DisplayName("should implement CommandRegistrar interface")
        void shouldImplementCommandRegistrar() {
            assertInstanceOf(CommandRegistrar.class, customEnchantmentCommand);
        }

        @Test
        @DisplayName("should be instantiable with plugin parameter")
        void shouldBeInstantiable() {
            CustomEnchantmentCommand cmd = new CustomEnchantmentCommand(mockPlugin);
            assertNotNull(cmd);
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register all subcommands on builder")
        void shouldRegisterAllSubCommands() {
            customEnchantmentCommand.onRegister(mockBuilder);

            // Verify key subcommands are registered (atLeast(1) for names
            // that also appear as nested subcommands in CommandGiveItem)
            verify(mockBuilder, atLeast(1)).subCommand("addenchant");
            verify(mockBuilder, atLeast(1)).subCommand("addgem");
            verify(mockBuilder, atLeast(1)).subCommand("removegem");
            verify(mockBuilder, atLeast(1)).subCommand("additem");
            verify(mockBuilder, atLeast(1)).subCommand("admin");
            verify(mockBuilder, atLeast(1)).subCommand("cleartime");
            verify(mockBuilder, atLeast(1)).subCommand("disablehelmet");
            verify(mockBuilder, atLeast(1)).subCommand("give");
            verify(mockBuilder, atLeast(1)).subCommand("info");
            verify(mockBuilder, atLeast(1)).subCommand("open");
            verify(mockBuilder, atLeast(1)).subCommand("reload");
            verify(mockBuilder, atLeast(1)).subCommand("removeenchant");
            verify(mockBuilder, atLeast(1)).subCommand("removeitem");
            verify(mockBuilder, atLeast(1)).subCommand("updateitem");
            verify(mockBuilder, atLeast(1)).subCommand("useitem");
            verify(mockBuilder, atLeast(1)).subCommand("debug");
            verify(mockBuilder, atLeast(1)).subCommand("fullchance");
            verify(mockBuilder, atLeast(1)).subCommand("debugall");
            verify(mockBuilder, atLeast(1)).subCommand("debugce");
            verify(mockBuilder, atLeast(1)).subCommand("test");
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> customEnchantmentCommand.onRegister(mockBuilder));
        }

        @Test
        @DisplayName("should delegate to 19 sub-command registrars")
        void shouldDelegateTo19SubCommands() {
            customEnchantmentCommand.onRegister(mockBuilder);

            // All 19 sub-commands call subCommand() at least once
            // Plus permissions, executors, etc.
            verify(mockBuilder, atLeast(19)).subCommand(anyString());
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should accept non-null plugin")
        void shouldAcceptNonNullPlugin() {
            assertDoesNotThrow(() -> new CustomEnchantmentCommand(mockPlugin));
        }

        @Test
        @DisplayName("should accept null plugin without exception")
        void shouldAcceptNullPlugin() {
            // Constructor stores plugin but doesn't use it in onRegister
            assertDoesNotThrow(() -> new CustomEnchantmentCommand(null));
        }
    }
}
