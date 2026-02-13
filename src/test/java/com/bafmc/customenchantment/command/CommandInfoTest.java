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
 * Tests for CommandInfo class.
 * Tests player info command registration.
 */
@DisplayName("CommandInfo Tests")
class CommandInfoTest {

    private CommandInfo commandInfo;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandInfo = new CommandInfo();
        when(mockBuilder.subCommand(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.subCommand(any(String[].class))).thenReturn(mockBuilder);
        when(mockBuilder.permission(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.commandExecutor(any())).thenReturn(mockBuilder);
        when(mockBuilder.end()).thenReturn(mockBuilder);
    }

    @Nested
    @DisplayName("Interface Implementation Tests")
    class InterfaceTests {

        @Test
        @DisplayName("should implement CommandRegistrar interface")
        void shouldImplementCommandRegistrar() {
            assertInstanceOf(CommandRegistrar.class, commandInfo);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandInfo());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register info subcommand")
        void shouldRegisterInfoSubCommand() {
            commandInfo.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("info");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandInfo.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.info.other");
        }

        @Test
        @DisplayName("should register player argument type")
        void shouldRegisterPlayerArgument() {
            commandInfo.onRegister(mockBuilder);

            verify(mockBuilder).subCommand(ArgumentType.PLAYER);
        }

        @Test
        @DisplayName("should register command executor")
        void shouldRegisterCommandExecutor() {
            commandInfo.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandInfo.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(2)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandInfo.onRegister(mockBuilder));
        }
    }
}
