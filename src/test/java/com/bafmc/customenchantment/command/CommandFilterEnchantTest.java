package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.CommandRegistrar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CommandFilterEnchant class.
 * Tests filter enchant command registration with add/remove/clear/list/help sub-commands.
 */
@DisplayName("CommandFilterEnchant Tests")
class CommandFilterEnchantTest {

    private CommandFilterEnchant commandFilterEnchant;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandFilterEnchant = new CommandFilterEnchant();
        when(mockBuilder.subCommand(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.subCommand(any(String[].class))).thenReturn(mockBuilder);
        when(mockBuilder.permission(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.commandExecutor(any())).thenReturn(mockBuilder);
        when(mockBuilder.tabCompleter(any())).thenReturn(mockBuilder);
        when(mockBuilder.end()).thenReturn(mockBuilder);
    }

    @Nested
    @DisplayName("Interface Implementation Tests")
    class InterfaceTests {

        @Test
        @DisplayName("should implement CommandRegistrar interface")
        void shouldImplementCommandRegistrar() {
            assertInstanceOf(CommandRegistrar.class, commandFilterEnchant);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandFilterEnchant());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register add subcommand")
        void shouldRegisterAddSubCommand() {
            commandFilterEnchant.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("add");
        }

        @Test
        @DisplayName("should register remove subcommand")
        void shouldRegisterRemoveSubCommand() {
            commandFilterEnchant.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("remove");
        }

        @Test
        @DisplayName("should register clear subcommand")
        void shouldRegisterClearSubCommand() {
            commandFilterEnchant.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("clear");
        }

        @Test
        @DisplayName("should register list subcommand")
        void shouldRegisterListSubCommand() {
            commandFilterEnchant.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("list");
        }

        @Test
        @DisplayName("should register help subcommand")
        void shouldRegisterHelpSubCommand() {
            commandFilterEnchant.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("help");
        }

        @Test
        @DisplayName("should register enchant subcommand for add and remove")
        void shouldRegisterEnchantSubCommand() {
            commandFilterEnchant.onRegister(mockBuilder);

            // <enchant> is registered twice: once for add, once for remove
            verify(mockBuilder, atLeast(2)).subCommand("<enchant>");
        }

        @Test
        @DisplayName("should register tab completers for enchant lists")
        void shouldRegisterTabCompleters() {
            commandFilterEnchant.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(2)).tabCompleter(any());
        }

        @Test
        @DisplayName("should register command executors for all subcommands")
        void shouldRegisterCommandExecutors() {
            commandFilterEnchant.onRegister(mockBuilder);

            // 5 executors: add, remove, clear, list, help
            verify(mockBuilder, atLeast(5)).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandFilterEnchant.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(5)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandFilterEnchant.onRegister(mockBuilder));
        }
    }
}
