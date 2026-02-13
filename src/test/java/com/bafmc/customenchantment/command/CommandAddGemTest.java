package com.bafmc.customenchantment.command;

import com.bafmc.bukkit.command.AdvancedCommandBuilder;
import com.bafmc.bukkit.command.ArgumentType;
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
 * Tests for CommandAddGem class.
 * Tests add gem command registration.
 */
@DisplayName("CommandAddGem Tests")
class CommandAddGemTest {

    private CommandAddGem commandAddGem;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandAddGem = new CommandAddGem();
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
            assertInstanceOf(CommandRegistrar.class, commandAddGem);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandAddGem());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register addgem subcommand")
        void shouldRegisterAddGemSubCommand() {
            commandAddGem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("addgem");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandAddGem.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.addgem.other");
        }

        @Test
        @DisplayName("should register player argument type")
        void shouldRegisterPlayerArgument() {
            commandAddGem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand(ArgumentType.PLAYER);
        }

        @Test
        @DisplayName("should register gem subcommand")
        void shouldRegisterGemSubCommand() {
            commandAddGem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("<gem>");
        }

        @Test
        @DisplayName("should register level subcommand")
        void shouldRegisterLevelSubCommand() {
            commandAddGem.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("<level>");
        }

        @Test
        @DisplayName("should register tab completers for gem and level")
        void shouldRegisterTabCompleters() {
            commandAddGem.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(2)).tabCompleter(any());
        }

        @Test
        @DisplayName("should register command executor")
        void shouldRegisterCommandExecutor() {
            commandAddGem.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandAddGem.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(4)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandAddGem.onRegister(mockBuilder));
        }
    }
}
