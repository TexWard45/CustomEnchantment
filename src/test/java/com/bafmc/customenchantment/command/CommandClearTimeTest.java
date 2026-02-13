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
 * Tests for CommandClearTime class.
 * Tests clear time modifier command registration.
 */
@DisplayName("CommandClearTime Tests")
class CommandClearTimeTest {

    private CommandClearTime commandClearTime;

    @Mock
    private AdvancedCommandBuilder mockBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commandClearTime = new CommandClearTime();
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
            assertInstanceOf(CommandRegistrar.class, commandClearTime);
        }

        @Test
        @DisplayName("should be instantiable with no-arg constructor")
        void shouldBeInstantiable() {
            assertNotNull(new CommandClearTime());
        }
    }

    @Nested
    @DisplayName("onRegister Tests")
    class OnRegisterTests {

        @Test
        @DisplayName("should register cleartime subcommand")
        void shouldRegisterClearTimeSubCommand() {
            commandClearTime.onRegister(mockBuilder);

            verify(mockBuilder).subCommand("cleartime");
        }

        @Test
        @DisplayName("should register correct permission")
        void shouldRegisterCorrectPermission() {
            commandClearTime.onRegister(mockBuilder);

            verify(mockBuilder).permission("customenchantment.cleartime.other");
        }

        @Test
        @DisplayName("should register player argument type")
        void shouldRegisterPlayerArgument() {
            commandClearTime.onRegister(mockBuilder);

            verify(mockBuilder).subCommand(ArgumentType.PLAYER);
        }

        @Test
        @DisplayName("should register command executor")
        void shouldRegisterCommandExecutor() {
            commandClearTime.onRegister(mockBuilder);

            verify(mockBuilder).commandExecutor(any());
        }

        @Test
        @DisplayName("should call end to close command tree")
        void shouldCallEnd() {
            commandClearTime.onRegister(mockBuilder);

            verify(mockBuilder, atLeast(2)).end();
        }

        @Test
        @DisplayName("should not throw when registering on builder")
        void shouldNotThrowOnRegister() {
            assertDoesNotThrow(() -> commandClearTime.onRegister(mockBuilder));
        }
    }
}
